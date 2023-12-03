package xyz.amymialee.mialib.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Shadow @Final protected MinecraftClient client;

//	record RaycastRecord(Map<Entity, Integer> targets) {
//		//World world, Vec3d startPos, @NotNull Vec3d angle, double distance, @NotNull Predicate<Entity> filter, double rayRadius, int maxHits
//
//		public void tick(World world) {
//			for (var entry : this.targets.entrySet()) {
//				entry.setValue(entry.getValue() - 1);
//				if (entry.getValue() <= 0) {
//					this.targets.remove(entry.getKey());
//				}
//			}
//		}
//
//		public void add(Entity entity, int ticks) {
//			this.targets.put(entity, ticks);
//			// TODO: sync removal
//		}
//
//		public void remove(Entity entity) {
//			this.targets.remove(entity);
//			// TODO: sync removal
//		}
//	}
//
//
//	@Inject(method = "tick", at = @At("TAIL"))
//	private void mialib$raycasting(CallbackInfo ci) {
//		var player = (ClientPlayerEntity) (Object) this;
//		if (this.client.player == player) {
//			for (var record : MRaycasting.RAYCASTS.values()) {
//				record.tick(player.world);
//			}
//
//			MiaLibClient.raycastedEntities = MRaycasting.raycast((ClientPlayerEntity) (Object) this, 128f, (playerEntity, entity) -> playerEntity != entity, 99, 1);
//		}
//	}
}