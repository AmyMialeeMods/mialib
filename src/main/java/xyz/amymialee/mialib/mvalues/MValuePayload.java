package xyz.amymialee.mialib.mvalues;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

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
			var value = MVServerManager.get(payload.id);
            if (value == null) return;
            value.readNbt(payload.compound);
            if (!(context.client().currentScreen instanceof MValueScreen screen)) return;
            screen.clearAndInit();
        }
	}

	public static class ServerReceiver implements ServerPlayNetworking.PlayPayloadHandler<MValuePayload> {
		@Override
		public void receive(@NotNull MValuePayload payload, ServerPlayNetworking.@NotNull Context context) {
			var value = MVServerManager.get(payload.id);
            if (value == null || !context.player().hasPermissionLevel(value.permissionLevel) || !value.canChange.test(context.player())) return;
            value.readNbt(payload.compound);
			MVServerManager.INSTANCE.onChange(value);
			Text text = Text.translatable("chat.type.admin", context.player().getDisplayName(), Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString())).formatted(Formatting.GRAY, Formatting.ITALIC);
			if (context.server().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
				for (var serverPlayerEntity : context.server().getPlayerManager().getPlayerList()) {
					if (context.server().getPlayerManager().isOperator(serverPlayerEntity.getGameProfile()))
						serverPlayerEntity.sendMessage(text);
				}
			}
			if (context.server().getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS))
				context.server().sendMessage(text);
        }
	}
}