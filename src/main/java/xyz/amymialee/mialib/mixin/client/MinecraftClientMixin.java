package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.config.MialibProperties;
import xyz.amymialee.mialib.modules.client.NetworkingClientModule;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Final public GameOptions options;

	@Unique private boolean mialib$attacking = false;
	@Unique private boolean mialib$using = false;

	@WrapOperation(method = "onInitFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0))
	private void mialib$skipNarrator(MinecraftClient instance, Screen screen, Operation<Void> original) {
		if (MialibProperties.skipNarrator.get()) {
			original.call(instance, new TitleScreen(true));
		} else {
			original.call(instance, screen);
		}
	}

	@WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
	private void mialib$holding(MinecraftClient instance, boolean bl, Operation<Void> original) {
		var attacking = this.options.attackKey.isPressed();
		if (attacking != this.mialib$attacking) {
			this.mialib$attacking = attacking;
			NetworkingClientModule.sendAttacking(attacking);
		}
		var using = this.options.useKey.isPressed();
		if (using != this.mialib$using) {
			this.mialib$using = using;
			NetworkingClientModule.sendUsing(using);
		}
		original.call(instance, bl);
	}
}