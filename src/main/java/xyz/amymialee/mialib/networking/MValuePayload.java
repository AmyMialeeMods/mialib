package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.mvalues.MValueScreen;

public record MValuePayload(Identifier id, NbtCompound compound) implements CustomPayload {
	public static final Id<MValuePayload> ID = new CustomPayload.Id<>(Mialib.id("mvalue"));
	public static final PacketCodec<RegistryByteBuf, MValuePayload> CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, MValuePayload::id, PacketCodecs.NBT_COMPOUND, MValuePayload::compound, MValuePayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static class ClientReceiver implements ClientPlayNetworking.PlayPayloadHandler<MValuePayload> {
		@Override
		public void receive(@NotNull MValuePayload payload, ClientPlayNetworking.@NotNull Context context) {
			var mValue = MValueManager.get(payload.id);
			if (mValue != null) {
				mValue.readNbt(payload.compound);
				if (context.client().currentScreen instanceof MValueScreen screen) {
					screen.refreshWidgets();
				}
			}
		}
	}

	public static class ServerReceiver implements ServerPlayNetworking.PlayPayloadHandler<MValuePayload> {
		@Override
		public void receive(@NotNull MValuePayload payload, ServerPlayNetworking.@NotNull Context context) {
			if (!context.player().hasPermissionLevel(4)) return;
			var mValue = MValueManager.get(payload.id);
			if (mValue != null) {
				mValue.readNbt(payload.compound);
				mValue.syncAll();
				MValueManager.saveConfig();
			}
		}
	}
}