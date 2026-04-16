package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.HexColorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
	public CompoundTag writeNbt(@NotNull CompoundTag compound, @NotNull MValue<Integer> value) {
		compound.putInt("value", value.get());
		return compound;
	}

	@Override
	public void readNbt(@NotNull CompoundTag compound, @NotNull MValue<Integer> value) {
		value.value = compound.getIntOr("value", this.defaultValue);
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
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck))
				.then(Commands.literal(value.id.toString())
						.then(Commands.argument("value", HexColorArgument.hexColor()))
						.executes(ctx -> {
							value.set(ctx.getArgument("value", Integer.class));
							MVServerManager.INSTANCE.onChange(value);
							ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
							return 1;
						})
				).executes(ctx -> {
					ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
					return 1;
				})));
	}

	@Override
	public String getValueAsString(@NotNull MValue<Integer> value) {
		return Integer.toHexString(value.value);
	}
}