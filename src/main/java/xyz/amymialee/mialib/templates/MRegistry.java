package xyz.amymialee.mialib.templates;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MRegistry {
	private static final Map<Class<?>, Registry<?>> DEFAULT_REGISTRIES = new HashMap<>();
	private final Map<Class<?>, Registry<?>> registries = new HashMap<>();
	private final String namespace;

	public MRegistry(String namespace) {
		this.namespace = namespace;
		this.registries.putAll(DEFAULT_REGISTRIES);
	}

	public final @NotNull Item register(String path, Item.@NotNull Properties settings, @NotNull Function<Item.Properties, Item> function) {
		var key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.namespace, path));
		var item = function.apply(settings.setId(key));
		Registry.register(BuiltInRegistries.ITEM, key, item);
		return item;
	}

	public <T> T register(String path, T thing) {
		return this.register(Identifier.fromNamespaceAndPath(this.namespace, path), thing);
	}

	public @SuppressWarnings("unchecked") <T> T register(Identifier id, T thing) {
		var registered = false;
		for (var registry : this.registries.entrySet()) {
			if (!registry.getKey().isInstance(thing)) continue;
			Registry.register(((Registry<T>) registry.getValue()), id, thing);
			registered = true;
			break;
		}
		if (registered) return thing;
		var error = new IllegalStateException("Failed to register " + id + " to the " + this.namespace + " MRegistry!");
		Mialib.LOGGER.error("Failed to register {} to the {} MRegistry!", id, this.namespace, error);
		throw error;
	}

	static {
		DEFAULT_REGISTRIES.put(Item.class, BuiltInRegistries.ITEM);
	}
}