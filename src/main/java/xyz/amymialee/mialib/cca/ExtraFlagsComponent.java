package xyz.amymialee.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.MMath;

public class ExtraFlagsComponent implements AutoSyncedComponent {
    private final Entity entity;
    private byte flags = 0;

    public ExtraFlagsComponent(Entity entity) {
        this.entity = entity;
    }

    public void sync() {
        MiaLib.FLAGS.sync(this.entity);
    }

    public boolean isImperceptible() {
        return MMath.getByteFlag(this.flags, 0);
    }

    public void setImperceptible(boolean imperceptible) {
        this.flags = MMath.setByteFlag(this.flags, 0, imperceptible);
        this.sync();
    }

    public boolean isIndestructible() {
        return MMath.getByteFlag(this.flags, 1);
    }

    public void setIndestructible(boolean indestructible) {
        this.flags = MMath.setByteFlag(this.flags, 1, indestructible);
        this.sync();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        this.flags = tag.getByte("flags");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        tag.putByte("flags", this.flags);
    }
}