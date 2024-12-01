package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.Mialib;

import java.util.function.Supplier;

@Mixin(AddServerScreen.class)
public class AddServerScreenMixin extends Screen {
    @Unique private static final Identifier LOGO_TEXTURE = Mialib.id("textures/gui/logo.png");
    @Unique private static final Identifier LOGO_GRAY_TEXTURE = Mialib.id("textures/gui/logo_gray.png");

    @Shadow @Final private Screen parent;

    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void mialib$mialibServers(CallbackInfo ci, @Share("button") @NotNull LocalRef<ButtonWidget> ref) {
        if (this.parent instanceof MultiplayerScreen multiplayerScreen) {
            var serverList = multiplayerScreen.getServerList();
            serverList.mialib$setEditingMialibServer(serverList.mialib$getMialibServers().contains(multiplayerScreen.getServerList().mialib$getEditTarget()));
            var button = new ButtonWidget(this.width / 2 + 104, this.height / 4 + 72, 20, 20, Text.empty(), (b) -> {
                serverList.mialib$setEditingMialibServer(!serverList.mialib$isEditingMialibServer());
                b.setTooltip(Tooltip.of(Text.translatable((serverList.mialib$isEditingMialibServer() ? "%s.mialib_server.true" : "%s.mialib_server.false").formatted(Mialib.MOD_ID))));
            }, Supplier::get) {
                @Override
                protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    super.renderWidget(context, mouseX, mouseY, delta);
                    context.mialib$drawTexture(serverList.mialib$isEditingMialibServer() ? LOGO_TEXTURE : LOGO_GRAY_TEXTURE, this.getX() + 2, this.getY() + 2, 16, 16, 16, 16);
                }

                @Override
                public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {}
            };
            button.setTooltip(Tooltip.of(Text.translatable((serverList.mialib$isEditingMialibServer() ? "%s.mialib_server.true" : "%s.mialib_server.false").formatted(Mialib.MOD_ID))));
            this.addDrawableChild(button);
        }
    }
}