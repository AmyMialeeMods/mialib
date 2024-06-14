package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

public record FloatyPayload(ItemStack stack) implements CustomPayload {
	public static final Id<FloatyPayload> ID = new CustomPayload.Id<>(Mialib.id("floaty"));
	public static final PacketCodec<RegistryByteBuf, FloatyPayload> CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, FloatyPayload::stack, FloatyPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<FloatyPayload> {
		@Override
		public void receive(@NotNull FloatyPayload payload, ClientPlayNetworking.@NotNull Context context) {
			context.client().gameRenderer.showFloatingItem(payload.stack);
		}
	}
}