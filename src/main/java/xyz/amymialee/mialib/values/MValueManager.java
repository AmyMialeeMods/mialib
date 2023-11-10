package xyz.amymialee.mialib.values;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.MiaLib;

import java.util.HashMap;
import java.util.Map;

public class MValueManager {
	private static final Map<Identifier, MValue<?>> values = new HashMap<>();
	public static final MValueType<Integer> INTEGER = new IntegerMValueType();

	public static MValue<?> register(Identifier id, MValue<?> mValue) {
		values.put(id, mValue);
		return mValue;
	}

	public static void loadConfig() {

	}

	public static void saveConfig() {
		var gson = new GsonBuilder().setPrettyPrinting().create();
		var json = new JsonObject();
		for (var entry : values.entrySet()) {
			var id = entry.getKey();
			var value = entry.getValue();
			value.appendJson(id, json);
		}
	}

	static {

	}

	public abstract static class MValue<T> {
		private final MValueType<T> type;
		final Text name;
		private final Text tooltip;
		private final ItemStack stack;
		T value;

		public MValue(MValueType<T> type, Text name, Text tooltip, ItemStack stack) {
			this.type = type;
			this.name = name;
			this.tooltip = tooltip;
			this.stack = stack;
		}

		public JsonObject appendJson(Identifier id, JsonObject json) {
			return this.type.toJson(this);
		}
	}

	public interface MValueType<T> {
		MValue<T> fromJson(JsonObject json, MValue<Integer> mValue, Identifier id);
		JsonObject appendToJson(JsonObject json, MValue<Integer> mValue, Identifier id);
		ClickableWidget createWidget(MValue<T> mValue, int x, int y, int width, int height);
	}
}