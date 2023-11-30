package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.interfaces.MItemEntity;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements MItemEntity {
    @Unique private int mergeDelay = 0;

    @Shadow private int itemAge;

    @Inject(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", at = @At("HEAD"), cancellable = true)
    private void mialib$delayedMerge(ItemEntity other, CallbackInfo ci) {
        if ((Object) this instanceof ItemEntity) {
            if (this.itemAge < this.mergeDelay || other.getItemAge() < other.mialib$getMergeDelay()) {
                ci.cancel();
            }
        }
    }

    @Unique
    public int mialib$getMergeDelay() {
        return this.mergeDelay;
    }

    @Unique
    public void mialib$setMergeDelay(int mergeDelay) {
        this.mergeDelay = mergeDelay;
    }
}