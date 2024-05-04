package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.config.MialibProperties;
import xyz.amymialee.mialib.networking.AttackingC2SPayload;
import xyz.amymialee.mialib.networking.UsingC2SPayload;

import java.util.List;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Final public GameOptions options;

	@Unique private boolean mialib$attacking = false;
	@Unique private boolean mialib$using = false;

	@WrapWithCondition(method = "createInitScreens", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
	private boolean mialib$skipNarrator(List<Function<Runnable, Screen>> instance, Object e) {
		return !MialibProperties.skipNarrator.get();
	}

	@WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
	private void mialib$holding(MinecraftClient instance, boolean bl, Operation<Void> original) {
		var attacking = this.options.attackKey.isPressed();
		if (attacking != this.mialib$attacking) {
			this.mialib$attacking = attacking;
			AttackingC2SPayload.send(attacking);
		}
		var using = this.options.useKey.isPressed();
		if (using != this.mialib$using) {
			this.mialib$using = using;
			UsingC2SPayload.send(using);
		}
		original.call(instance, bl);
	}
}