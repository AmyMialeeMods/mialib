package xyz.amymialee.mialib.mvalues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.runnables.CachedSupplier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class MValueCategory {
    public static final List<MValueCategory> CATEGORIES = new ArrayList<>();
    private final List<MValue<?>> values = new ArrayList<>();
    public final Identifier id;
    public final Supplier<ItemStack> stackSupplier;
    public final Identifier backgroundTexture;
    public final int width;
    public final int height;

    public MValueCategory(Identifier id, @NotNull Item item, Identifier backgroundTexture) {
        this(id, item.getDefaultStack(), backgroundTexture, 16, 16);
    }

    public MValueCategory(Identifier id, @NotNull Item item, Identifier backgroundTexture, int width, int height) {
        this(id, item.getDefaultStack(), backgroundTexture, width, height);
    }

    public MValueCategory(Identifier id, ItemStack stack, Identifier backgroundTexture) {
        this(id, new CachedSupplier<>(stack), backgroundTexture, 16, 16);
    }

    public MValueCategory(Identifier id, ItemStack stack, Identifier backgroundTexture, int width, int height) {
        this(id, new CachedSupplier<>(stack), backgroundTexture, width, height);
    }

    public MValueCategory(Identifier id, Supplier<ItemStack> stackSupplier, Identifier backgroundTexture) {
        this(id, stackSupplier, backgroundTexture, 16, 16);
    }

    public MValueCategory(Identifier id, Supplier<ItemStack> stackSupplier, Identifier backgroundTexture, int width, int height) {
        this.id = id;
        this.stackSupplier = stackSupplier;
        this.backgroundTexture = backgroundTexture;
        this.width = width;
        this.height = height;
        CATEGORIES.add(this);
        CATEGORIES.sort(Comparator.comparing(a -> a.id.getPath()));
    }

    public void addValue(MValue<?> value) {
        this.values.add(value);
    }

    public List<MValue<?>> getValues(PlayerEntity player) {
        var list = new ArrayList<>(this.values);
        list.removeIf((m) -> !player.hasPermissionLevel(m.permissionLevel) || !m.canChange.test(player));
        return list;
    }

    public String getTranslationKey() {
        return "mvaluecategory.%s.%s".formatted(this.id.getNamespace(), this.id.getPath());
    }

    @Environment(EnvType.CLIENT)
    public MValueCategoryWidget getWidget(int x, int y, ButtonWidget.PressAction consumer) {
        return new MValueCategoryWidget(x, y, this, consumer);
    }

    @Environment(EnvType.CLIENT)
    public static class MValueCategoryWidget extends ButtonWidget {
        public final MValueCategory category;
        public boolean enabled;
        public double scroll;
        public double velocity;
        private boolean scissorContains;

        public MValueCategoryWidget(int x, int y, @NotNull MValueCategory value, PressAction consumer) {
            super(x, y, 24, 24, Text.translatable(value.getTranslationKey()), consumer, DEFAULT_NARRATION_SUPPLIER);
            this.category = value;
            this.enabled = consumer == null;
            this.setTooltip(Tooltip.of(this.getMessage()));
        }

        @Override
        protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
            var scroll = this.scroll + this.velocity * delta;
            this.scissorContains = context.scissorContains(mouseX, mouseY);
            this.hovered = this.scissorContains
                    && mouseX >= this.getX()
                    && mouseY >= this.getY() - scroll
                    && mouseX < this.getX() + this.width
                    && mouseY < this.getY() + this.height - scroll;
            context.mialib$drawTexture(RenderPipelines.GUI_TEXTURED, this.enabled ? SelectionState.SELECTED.texture : (this.hovered ? SelectionState.HIGHLIGHTED : SelectionState.DESELECTED).texture, this.getX(), this.getY(), 22, 22, 22, 22, 22, 22);
            context.drawItem(this.category.stackSupplier.get(), this.getX() + 3, this.getY() + 3);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.scissorContains && super.mouseClicked(mouseX, mouseY + this.scroll, button);
        }

        @Override
        public void onPress() {
            if (this.onPress == null) return;
            this.onPress.onPress(this);
        }
    }

    private enum SelectionState {
        DESELECTED(Mialib.id("textures/gui/mvalue/tab_deselected.png")),
        HIGHLIGHTED(Mialib.id("textures/gui/mvalue/tab_highlighted.png")),
        SELECTED(Mialib.id("textures/gui/mvalue/tab_selected.png"));

        public final Identifier texture;

        SelectionState(Identifier texture) {
            this.texture = texture;
        }
    }
}