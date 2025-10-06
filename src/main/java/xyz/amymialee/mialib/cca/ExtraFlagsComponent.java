package xyz.amymialee.mialib.cca;

import net.minecraft.entity.Entity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
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

    public boolean isIndestructible() {
        return MMath.getByteFlag(this.flags, 1);
    }

    public boolean isImmortal() {
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

    public boolean hasIndestructibleCommand() {
        return MMath.getByteFlag(this.commandFlags, 1);
    }

    public boolean hasImmortalCommand() {
        return MMath.getByteFlag(this.commandFlags, 2);
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

    public void refreshFlags() {
        this.setIndestructible(ExtraFlagEvents.SHOULD_BE_INDESTRUCTIBLE.invoker().shouldHaveFlag(this.entity.getEntityWorld(), this.entity).isAccepted());
        this.setImmortal(ExtraFlagEvents.SHOULD_BE_IMMORTAL.invoker().shouldHaveFlag(this.entity.getEntityWorld(), this.entity).isAccepted());
    }

    public @Override boolean isRequiredOnClient() {
        return false;
    }

    public @Override void readData(@NotNull ReadView readView) {
        this.flags = readView.getByte("flags", (byte) 0);
        this.commandFlags = readView.getByte("commandFlags", (byte) 0);
    }

    public @Override void writeData(@NotNull WriteView writeView) {
        writeView.putByte("flags", this.flags);
        writeView.putByte("commandFlags", this.commandFlags);
    }

    static {
        ExtraFlagEvents.SHOULD_BE_INDESTRUCTIBLE.register((world, entity) -> {
            var component = KEY.get(entity);
            if (component.hasIndestructibleCommand()) return ActionResult.SUCCESS;
            return ActionResult.PASS;
        });
        ExtraFlagEvents.SHOULD_BE_IMMORTAL.register((world, entity) -> {
            var component = KEY.get(entity);
            if (component.hasImmortalCommand()) return ActionResult.SUCCESS;
            return ActionResult.PASS;
        });
    }
}