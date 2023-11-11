package xyz.amymialee.mialib.values;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.tooltip.Tooltip;
import static net.minecraft.client.option.GameOptions.getGenericValueText;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.TriConsumer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class MValueManager {
	private static final Map<Identifier, MValue<?>> VALUES = new HashMap<>();

	public static MValue<?> register(Identifier id, MValue<?> mValue) {
		VALUES.put(id, mValue);
		return mValue;
	}

	public static void loadConfig() {
		var gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			var json = gson.<JsonObject>fromJson(new JsonReader(new FileReader(FabricLoader.getInstance().getConfigDir().resolve("mialib.json").toFile())), JsonObject.class);
			for (var entry : VALUES.entrySet()) {
				var id = entry.getKey();
				var value = entry.getValue();
				value.readFromJson(json, id);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveConfig() {
		var gson = new GsonBuilder().setPrettyPrinting().create();
		var json = new JsonObject();
		for (var entry : VALUES.entrySet()) {
			var id = entry.getKey();
			var value = entry.getValue();
			value.addToJson(json, id);
		}
		try {
			gson.toJson(json, new JsonWriter(new FileWriter(FabricLoader.getInstance().getConfigDir().resolve("mialib.json").toFile())));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static MValue<Integer> ofInteger(String translationKey, int defaultValue, int min, int max) {
		return ofIntegerBuilder(translationKey, defaultValue, min, max).build();
	}

	public static MValue<Integer> ofInteger(String translationKey, int defaultValue, int min, int max, ItemStack displayStack) {
		return ofIntegerBuilder(translationKey, defaultValue, min, max, displayStack).build();
	}

	public static MValue<Integer> ofInteger(String translationKey, int defaultValue, int min, int max, ItemStack displayStack, Consumer<Integer> changedCallback) {
		return ofIntegerBuilder(translationKey, defaultValue, min, max, displayStack, changedCallback).build();
	}

	public static MValueBuilder<Integer> ofIntegerBuilder(String translationKey, int defaultValue, int min, int max) {
		return ofIntegerBuilder(translationKey, defaultValue, min, max, ItemStack.EMPTY);
	}

	public static MValueBuilder<Integer> ofIntegerBuilder(String translationKey, int defaultValue, int min, int max, ItemStack displayStack) {
		return ofIntegerBuilder(translationKey, defaultValue, min, max, displayStack, value -> {});
	}

	public static MValueBuilder<Integer> ofIntegerBuilder(String translationKey, int defaultValue, int min, int max, ItemStack displayStack, Consumer<Integer> changedCallback) {
		var callbacks = new SimpleOption.ValidatingIntSliderCallbacks(min, max);
		return new MValueBuilder<Integer>()
				.translationKey(translationKey)
				.valueTextGetter((optionText, value) -> getGenericValueText(optionText, Text.translatable(translationKey).append(": " + value)))
				.callbacks(callbacks)
				.codec(callbacks.codec())
				.defaultValue(defaultValue)
				.changedCallback(changedCallback)
				.codec(Codec.INT)
				.displayStack(displayStack)
				.addToJson((json, identifier, value) -> json.addProperty(identifier.toString(), value))
				.readFromJson((json, identifier) -> {
					if (json.has(identifier.toString())) {
						return json.get(identifier.toString()).getAsInt();
					}
					return defaultValue;
				});
	}

	public static class MValueBuilder<T> {
		private String translationKey = "";
		private SimpleOption.TooltipFactory<T> tooltip = SimpleOption.emptyTooltip();
		private SimpleOption.ValueTextGetter<T> valueTextGetter = (optionText, value) -> getGenericValueText(optionText, Text.translatable(this.translationKey));
		private SimpleOption.Callbacks<T> callbacks = null;
		private Codec<T> codec;
		private T defaultValue;
		private Consumer<T> changedCallback = value -> {};
		private ItemStack displayStack = ItemStack.EMPTY;
		private TriConsumer<JsonObject, Identifier, T> addToJson = (json, identifier, value) -> {};
		private BiFunction<JsonObject, Identifier, T> readFromJson = (json, identifier) -> this.defaultValue;

		private MValueBuilder() {}

		public MValueBuilder<T> translationKey(String translationKey) {
			this.translationKey = translationKey;
			return this;
		}

		public MValueBuilder<T> tooltip(SimpleOption.TooltipFactory<T> tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public MValueBuilder<T> valueTextGetter(SimpleOption.ValueTextGetter<T> valueTextGetter) {
			this.valueTextGetter = valueTextGetter;
			return this;
		}

		public MValueBuilder<T> callbacks(SimpleOption.Callbacks<T> callbacks) {
			this.callbacks = callbacks;
			return this;
		}

		public MValueBuilder<T> codec(Codec<T> codec) {
			this.codec = codec;
			return this;
		}

		public MValueBuilder<T> defaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public MValueBuilder<T> changedCallback(Consumer<T> changedCallback) {
			this.changedCallback = changedCallback;
			return this;
		}

		public MValueBuilder<T> displayStack(ItemStack displayStack) {
			this.displayStack = displayStack;
			return this;
		}

		public MValueBuilder<T> addToJson(TriConsumer<JsonObject, Identifier, T> addToJson) {
			this.addToJson = addToJson;
			return this;
		}

		public MValueBuilder<T> readFromJson(BiFunction<JsonObject, Identifier, T> readFromJson) {
			this.readFromJson = readFromJson;
			return this;
		}

		public MValue<T> build() {
			if (this.callbacks == null) {
				var exception = new RuntimeException("MValueBuilder: callbacks cannot be null");
				MiaLib.LOGGER.error("MValueBuilder: callbacks cannot be null", exception);
				throw exception;
			}
			var option = new SimpleOption<>(this.translationKey, this.tooltip, this.valueTextGetter, this.callbacks, this.codec, this.defaultValue, this.changedCallback);
			return new MValue<>(option, this.displayStack, this.addToJson, this.readFromJson);
		}
	}

	public static class MValue<T> {
		private final SimpleOption<T> option;
		private final ItemStack displayStack;
		private final TriConsumer<JsonObject, Identifier, T> addToJson;
		private final BiFunction<JsonObject, Identifier, T> readFromJson;

		public MValue(SimpleOption<T> option, ItemStack displayStack, TriConsumer<JsonObject, Identifier, T> addToJson, BiFunction<JsonObject, Identifier, T> readFromJson) {
			this.option = option;
			this.displayStack = displayStack;
			this.addToJson = addToJson;
			this.readFromJson = readFromJson;
		}

		protected void addToJson(JsonObject json, Identifier identifier) {
			this.addToJson.accept(json, identifier, this.option.getValue());
		}

		protected void readFromJson(JsonObject json, Identifier identifier) {
			this.option.setValue(this.readFromJson.apply(json, identifier));
		}

		public T getValue() {
			return this.option.getValue();
		}

		public ItemStack getDisplayStack() {
			return this.displayStack;
		}
	}
}