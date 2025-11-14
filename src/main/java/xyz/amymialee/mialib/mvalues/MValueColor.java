package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.HexColorArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.util.HexFormat;

public class MValueColor extends MValueType<Integer> {
	public MValueColor(int defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	@Override
	public boolean set(@NotNull MValue<Integer> mValue, Integer value) {
		mValue.value = value;
		return true;
	}

	@Override
	public NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
		compound.putInt("value", value.get());
		return compound;
	}

	@Override
	public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
		value.value = compound.getInt("value", this.defaultValue);
	}

	@Override
	public JsonElement writeJson(@NotNull MValue<Integer> value) {
		return new JsonPrimitive(Integer.toHexString(value.get()));
	}

	@Override
	public void readJson(@NotNull JsonElement json, @NotNull MValue<Integer> value) {
		try {
			value.set(HexFormat.fromHexDigits(json.getAsString()));
		} catch(IllegalArgumentException e) {
			Mialib.LOGGER.error("Failed to read MValue Color {}; '{}' is not a valid Hex String.", value.id, json.getAsString());
		}
	}

	@Override
	protected void registerServerCommand(MValue<Integer> value) {
		CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
				.then(CommandManager.literal(value.id.toString())
						.then(CommandManager.argument("value", HexColorArgumentType.hexColor()))
						.executes(ctx -> {
							value.set(ctx.getArgument("value", Integer.class));
							MVServerManager.INSTANCE.onChange(value);
							ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
							return 1;
						})
				).executes(ctx -> {
					ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
					return 1;
				})));
	}

	@Override
	protected void registerClientCommand(MValue<Integer> value) {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.getPlayer().hasPermissionLevel(value.permissionLevel))
				.then(ClientCommandManager.literal(value.id.toString())
						.then(ClientCommandManager.argument("value", HexColorArgumentType.hexColor())
								.executes(ctx -> {
									//HexColorArgumentType$getArgbColor takes specifically ServerCommandSource Context... thus this quick workaround
									value.set(ctx.getArgument("value", Integer.class));
									ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
									return 1;
								})
						).executes(ctx -> {
							ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
							return 1;
						}))));
	}

	@Override
	public String getValueAsString(@NotNull MValue<Integer> value)
	{
		return Integer.toHexString(value.value);
	}

	@Override
	public Object getWidget(int x, int y, MValue<Integer> mValue)
	{
		return new MValueColorWidget(x, y, mValue);
	}
}