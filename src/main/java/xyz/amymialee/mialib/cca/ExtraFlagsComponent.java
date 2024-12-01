package xyz.amymialee.mialib.cca;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.events.ExtraFlagEvents;
import xyz.amymialee.mialib.util.MMath;

/**
 * <p>
 * 1 - Indestructible: The entity cannot be damaged or destroyed.
 * </p>
 * <p>
 * 2 - Immortal: The entity cannot die, but can take damage down to 1hp.
 * </p>
 * <p>
 * 3 - Fly: Can the entity fly (only for players).
 * </p>
 */
public class ExtraFlagsComponent implements AutoSyncedComponent {
    public static final ComponentKey<ExtraFlagsComponent> KEY = ComponentRegistry.getOrCreate(Mialib.id("extra_flags"), ExtraFlagsComponent.class);
    private final Entity entity;
    private byte flags = 0;
    private byte commandFlags = 0;
    public static final AbilitySource FLY_COMMAND = Pal.getAbilitySource(Mialib.MOD_ID, "fly_command");

    public ExtraFlagsComponent(Entity entity) {
        this.entity = entity;
    }

    public void sync() {
        KEY.sync(this.entity);
    }

    public boolean isIndestructible() {
        return MMath.getByteFlag(this.flags, 1);
    }

    public boolean isImmortal() {
        return MMath.getByteFlag(this.flags, 2);
    }

    public boolean canFly() {
        return MMath.getByteFlag(this.flags, 2);
    }

    private void setIndestructible(boolean indestructible) {
        if (indestructible == this.isIndestructible()) return;
        this.flags = MMath.setByteFlag(this.flags, 1, indestructible);
        this.sync();
    }

    private void setImmortal(boolean immortal) {
        if (immortal == this.isImmortal()) return;
        this.flags = MMath.setByteFlag(this.flags, 2, immortal);
        this.sync();
    }

    private void setFly(boolean fly) {
        if (fly == this.canFly()) return;
        this.flags = MMath.setByteFlag(this.flags, 3, fly);
        this.sync();
        if (!(this.entity instanceof PlayerEntity player)) return;
        if (fly) {
            FLY_COMMAND.grantTo(player, VanillaAbilities.ALLOW_FLYING);
        } else {
            FLY_COMMAND.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
        }
    }

    public boolean hasIndestructibleCommand() {
        return MMath.getByteFlag(this.commandFlags, 1);
    }

    public boolean hasImmortalCommand() {
        return MMath.getByteFlag(this.commandFlags, 2);
    }

    public boolean hasFlyCommand() {
        return MMath.getByteFlag(this.commandFlags, 3);
    }

    public void setIndestructibleCommand(boolean indestructible) {
        if (indestructible == this.hasIndestructibleCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 1, indestructible);
        this.sync();
        this.refreshFlags();
    }

    public void setImmortalCommand(boolean immortal) {
        if (immortal == this.hasImmortalCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 2, immortal);
        this.sync();
        this.refreshFlags();
    }

    public void setFlyCommand(boolean fly) {
        if (fly == this.hasFlyCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 3, fly);
        this.sync();
        this.refreshFlags();
    }

    public void refreshFlags() {
        this.setIndestructible(ExtraFlagEvents.SHOULD_BE_INDESTRUCTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setImmortal(ExtraFlagEvents.SHOULD_BE_IMMORTAL.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setFly(ExtraFlagEvents.SHOULD_FLY.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.flags = tag.getByte("flags");
        this.commandFlags = tag.getByte("commandFlags");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putByte("flags", this.flags);
        tag.putByte("commandFlags", this.commandFlags);
    }

    @Override
    public boolean isRequiredOnClient() {
        return false;
    }
}