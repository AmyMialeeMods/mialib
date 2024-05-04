package xyz.amymialee.mialib.cca;

import net.minecraft.entity.Entity;
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
 * Stores an extra set of flags for entities.
 * </p>
 * <p>
 * Keeps flags stored as a byte, with each bit representing a different flag.
 * </p>
 * <p>
 * Also stores a set of command toggles for each flag.
 * </p>
 * <p>
 * Includes 3 flags:
 * </p>
 * <p>
 *  - Imperceptible: The entity is completely invisible, including feature renderers.
 * </p>
 * <p>
 *  - Indestructible: The entity cannot be damaged or destroyed.
 * </p>
 * <p>
 *  - Immortal: The entity cannot die, but can take damage down to 1hp.
 * </p>
 */
public class ExtraFlagsComponent implements AutoSyncedComponent {
    public static final ComponentKey<ExtraFlagsComponent> KEY = ComponentRegistry.getOrCreate(Mialib.id("extra_flags"), ExtraFlagsComponent.class);
    private final Entity entity;
    private byte flags = 0;
    private byte commandFlags = 0;

    public ExtraFlagsComponent(Entity entity) {
        this.entity = entity;
    }

    public void sync() {
        KEY.sync(this.entity);
    }

    public boolean isImperceptible() {
        return MMath.getByteFlag(this.flags, 0);
    }

    private void setImperceptible(boolean imperceptible) {
        if (imperceptible == this.isImperceptible()) return;
        this.flags = MMath.setByteFlag(this.flags, 0, imperceptible);
        this.sync();
    }

    public boolean hasImperceptibleCommand() {
        return MMath.getByteFlag(this.commandFlags, 0);
    }

    public void setImperceptibleCommand(boolean imperceptible) {
        if (imperceptible == this.hasImperceptibleCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 0, imperceptible);
        this.sync();
        this.refreshFlags();
    }

    public boolean isIndestructible() {
        return MMath.getByteFlag(this.flags, 1);
    }

    private void setIndestructible(boolean indestructible) {
        if (indestructible == this.isIndestructible()) return;
        this.flags = MMath.setByteFlag(this.flags, 1, indestructible);
        this.sync();
    }

    public boolean hasIndestructibleCommand() {
        return MMath.getByteFlag(this.commandFlags, 1);
    }

    public void setIndestructibleCommand(boolean indestructible) {
        if (indestructible == this.hasIndestructibleCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 1, indestructible);
        this.sync();
        this.refreshFlags();
    }

    public boolean isImmortal() {
        return MMath.getByteFlag(this.flags, 2);
    }

    private void setImmortal(boolean immortal) {
        if (immortal == this.isImmortal()) return;
        this.flags = MMath.setByteFlag(this.flags, 2, immortal);
        this.sync();
    }

    public boolean hasImmortalCommand() {
        return MMath.getByteFlag(this.commandFlags, 2);
    }

    public void setImmortalCommand(boolean immortal) {
        if (immortal == this.hasImmortalCommand()) return;
        this.commandFlags = MMath.setByteFlag(this.commandFlags, 2, immortal);
        this.sync();
        this.refreshFlags();
    }

    public void refreshFlags() {
        this.setImperceptible(ExtraFlagEvents.SHOULD_BE_IMPERCEPTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setIndestructible(ExtraFlagEvents.SHOULD_BE_INDESTRUCTIBLE.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
        this.setImmortal(ExtraFlagEvents.SHOULD_BE_IMMORTAL.invoker().shouldHaveFlag(this.entity.getWorld(), this.entity).isAccepted());
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
}