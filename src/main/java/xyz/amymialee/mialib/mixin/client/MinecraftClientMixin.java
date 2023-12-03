package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.MiaLib;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Final public GameOptions options;
	@Shadow @Nullable public ClientPlayerEntity player;

	@Unique private boolean astronomical$holding = false;

	@WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
	private void mialib$holding(MinecraftClient instance, boolean bl, Operation<Void> original) {
		var holding = this.options.attackKey.isPressed();
		if (holding != this.astronomical$holding) {
			this.astronomical$holding = holding;
			var buf = PacketByteBufs.create();
			buf.writeBoolean(holding);
			ClientPlayNetworking.send(MiaLib.id("holding"), buf);
		}
		original.call(instance, bl);
	}
}