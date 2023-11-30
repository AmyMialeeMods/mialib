package xyz.amymialee.mialib.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.MiaLib;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Shadow @Final protected MinecraftClient client;

	@Inject(method = "onGameModeChanged", at = @At("HEAD"))
	private void mialib$landingTest(CallbackInfo ci) {
		System.out.println("ClientGameMode: " + MiaLib.TEST_FLOAT.getValue() + " " + MiaLib.TEST_DOUBLE.getValue() + " " + MiaLib.TEST_INTEGER.getValue() + " " + MiaLib.TEST_LONG.getValue() + " " + MiaLib.TEST_BOOLEAN.getValue());
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void mialib$raycasting(CallbackInfo ci) {
//		var player = (ClientPlayerEntity) (Object) this;
//		if (this.client.player == player) {
//			MiaLibClient.raycastedEntities = MRaycasting.raycast((ClientPlayerEntity) (Object) this, 128f, (playerEntity, entity) -> playerEntity != entity, 99, 1);
//		}
	}
}