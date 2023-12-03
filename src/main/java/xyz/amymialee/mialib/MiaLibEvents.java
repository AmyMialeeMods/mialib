package xyz.amymialee.mialib;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MiaLibEvents {
    Event<SmeltBrokenBlockCallback> SMELT_BROKEN_BLOCK = EventFactory.createArrayBacked(SmeltBrokenBlockCallback.class, callbacks -> (world, state, pos, blockEntity, entity, stack) -> {
        for (var callback : callbacks) {
            var result = callback.shouldSmeltBlock(world, state, pos, blockEntity, entity, stack);
            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    @FunctionalInterface
    interface SmeltBrokenBlockCallback {
        ActionResult shouldSmeltBlock(World world, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack);
    }
}