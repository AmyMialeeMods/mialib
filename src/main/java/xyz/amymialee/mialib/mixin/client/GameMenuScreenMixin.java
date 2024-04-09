package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.mvalues.MValueScreen;

import java.util.function.Supplier;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Unique private static final Identifier LOGO_TEXTURE = MiaLib.id("textures/gui/logo.png");

    @SuppressWarnings("unused")
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @WrapOperation(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;createButton(Lnet/minecraft/text/Text;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/widget/ButtonWidget;", ordinal = 2))
    private ButtonWidget mialib$getRef(GameMenuScreen instance, Text text, Supplier<Screen> screenSupplier, @NotNull Operation<ButtonWidget> original, @Share("button") @NotNull LocalRef<ButtonWidget> ref) {
        var buttonWidget = original.call(instance, text, screenSupplier);
        ref.set(buttonWidget);
        return buttonWidget;
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void mialib$mvalues(CallbackInfo ci, @Share("button") @NotNull LocalRef<ButtonWidget> ref) {
        if (this.client == null || this.client.player == null || !this.client.player.hasPermissionLevel(4)) return;
        var button = this.addDrawableChild(new ButtonWidget(ref.get().getX() - 20 - 4, ref.get().getY(), 20, 20, Text.translatable("%s.screen.mvalues".formatted(MiaLib.MOD_ID)), (b) -> this.client.setScreen(new MValueScreen()), Supplier::get) {
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderButton(context, mouseX, mouseY, delta);
                context.drawTexture(LOGO_TEXTURE, this.getX() + 2, this.getY() + 2, 16, 16, 0, 0, 16, 16, 16, 16);
            }

            @Override
            public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {}
        });
        button.setTooltip(Tooltip.of(Text.translatable("%s.screen.mvalues".formatted(MiaLib.MOD_ID))));
    }
}