package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

public record AttackingPayload(boolean attacking) implements CustomPayload {
	public static final Id<AttackingPayload> ID = new CustomPayload.Id<>(Mialib.id("attacking"));
    public static final PacketCodec<PacketByteBuf, AttackingPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, AttackingPayload::attacking, AttackingPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<AttackingPayload> {
		@Override
		public void receive(@NotNull AttackingPayload payload, ServerPlayNetworking.@NotNull Context context) {
			context.player().mialib$setHoldingAttack(payload.attacking());
		}
	}
}