package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.amymialee.mialib.interfaces.MItem;

@Mixin(Item.class)
public class ItemMixin implements MItem {
    @Unique
    private static final Identifier[] EMPTY = new Identifier[0];

    @Override
    public boolean mialib$shouldSmelt(World world, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack) {
        return false;
    }

    @Override
    public boolean mialib$attack(World world, ItemStack stack, LivingEntity attacker, Entity target) {
        return false;
    }

    @Override
    public void mialib$killEntity(World world, ItemStack stack, LivingEntity user, LivingEntity victim) {}

    @Override
    public Identifier[] mialib$cooldownDisplays() {
        return EMPTY;
    }
}