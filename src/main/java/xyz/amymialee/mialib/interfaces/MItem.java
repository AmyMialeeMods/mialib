package xyz.amymialee.mialib.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MItem {
    boolean mialib$shouldSmelt(World world, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack);
    boolean mialib$attack(World world, ItemStack stack, LivingEntity attacker, Entity target);
    void mialib$killEntity(World world, ItemStack stack, LivingEntity user, LivingEntity victim);
    Identifier[] mialib$cooldownDisplays();
    @Environment(EnvType.CLIENT)
    BipedEntityModel.ArmPose mialib$pose(LivingEntity entity, Hand hand, ItemStack stack);
}