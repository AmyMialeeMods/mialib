package xyz.amymialee.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class IdCooldownComponent implements AutoSyncedComponent, CommonTickingComponent {
	private final Map<Identifier, Integer> cooldowns = new HashMap<>();

	public IdCooldownComponent() {}

	public static @NotNull IdCooldownComponent get(PlayerEntity player) {
		return MiaLib.ID_COOLDOWN_COMPONENT.get(player);
	}

	@Override
	public void tick() {
		for (var id : cooldowns.keySet()) {
			var value = cooldowns.get(id) - 1;
			if (value <= 0) {
				cooldowns.remove(id);
			} else {
				cooldowns.put(id, value);
			}
		}
	}

	public boolean isCoolingDown(Identifier id) {
		return cooldowns.containsKey(id);
	}

	public void setCooldown(Identifier id, int ticks) {
		if (ticks <= 0) {
			cooldowns.remove(id);
			return;
		}
		cooldowns.put(id, ticks);
	}

	public int getCooldown(Identifier id) {
		return cooldowns.get(id);
	}

	public static boolean isCoolingDown(PlayerEntity player, Identifier id) {
		return get(player).isCoolingDown(id);
	}

	public static void setCooldown(PlayerEntity player, Identifier id, int ticks) {
		get(player).setCooldown(id, ticks);
	}

	public static int getCooldown(PlayerEntity player, Identifier id) {
		return get(player).getCooldown(id);
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag) {
		var compound = tag.getCompound("cooldowns");
		if (compound == null) return;
		cooldowns.clear();
		for (var id : compound.getKeys()) {
			cooldowns.put(new Identifier(id), compound.getInt(id));
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		var compound = new NbtCompound();
		for (var id : cooldowns.keySet()) {
			compound.putInt(id.toString(), cooldowns.get(id));
		}
		tag.put("cooldowns", compound);
	}
}
