package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.events.MiaLibEvents;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    @Unique
    private static final SimpleInventory fakeFurnace = new SimpleInventory(3);

    @WrapOperation(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
    private static List<ItemStack> mialib$autoSmelting(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, @NotNull Operation<List<ItemStack>> original) {
        var result = MiaLibEvents.SMELT_BROKEN_BLOCK.invoker().shouldSmeltBlock(world, state, pos, blockEntity, entity, stack);
        var originalResult = original.call(state, world, pos, blockEntity, entity, stack);
        if (result == ActionResult.SUCCESS) {
            var hasSmeltable = hasSmeltable(world, originalResult);
            if (hasSmeltable) {
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
                world.spawnParticles(stack.isIn(MiaLib.SOUL_FIRE_SMELTING) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 8, 0.25D, 0.25D, 0.25D, 0.025D);
                return smeltList(world, originalResult);
            }
        }
        return originalResult;
    }

    @WrapOperation(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)Ljava/util/List;"))
    private static List<ItemStack> mialib$autoSmelting(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, @NotNull Operation<List<ItemStack>> original) {
        var result = MiaLibEvents.SMELT_BROKEN_BLOCK.invoker().shouldSmeltBlock(world, state, pos, blockEntity, null, ItemStack.EMPTY);
        var originalResult = original.call(state, world, pos, blockEntity);
        if (result == ActionResult.SUCCESS) {
            var hasSmeltable = hasSmeltable(world, originalResult);
            if (hasSmeltable) {
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
                world.spawnParticles(ParticleTypes.FLAME, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 8, 0.25D, 0.25D, 0.25D, 0.025D);
                return smeltList(world, originalResult);
            }
        }
        return originalResult;
    }

    @WrapOperation(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)Ljava/util/List;"))
    private static List<ItemStack> mialib$autoSmeltingEntityless(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Operation<List<ItemStack>> original) {
        return mialib$autoSmelting(state, world, pos, blockEntity, original);
    }

    @Unique
    private static boolean hasSmeltable(ServerWorld world, @NotNull List<ItemStack> stacks) {
        for (var stack : stacks) {
            if (simulateSmelt(world, stack) != null) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static @NotNull List<ItemStack> smeltList(ServerWorld world, @NotNull List<ItemStack> stacks) {
        var list = new ArrayList<ItemStack>();
        for (var stack : stacks) {
            var smelted = simulateSmelt(world, stack);
            if (smelted != null) {
                list.add(smelted);
            } else {
                list.add(stack);
            }
        }
        return list;
    }

    @Unique
    private static @Nullable ItemStack simulateSmelt(@NotNull World world, ItemStack input) {
        fakeFurnace.clear();
        fakeFurnace.setStack(0, input);
        var recipes = world.getRecipeManager().getAllMatches(RecipeType.SMELTING, fakeFurnace, world);
        for (var recipe : recipes) {
            var result = recipe.getOutput(world.getRegistryManager());
            result.setCount(result.getCount() * input.getCount());
            return result;
        }
        return null;
    }
}