package xyz.amymialee.mialib.mixin.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

    @Override @Environment(EnvType.CLIENT)
    public BipedEntityModel.ArmPose mialib$pose(LivingEntity entity, Hand hand, ItemStack stack) {
        return null;
    }

    @Override
    public int mialib$enchantLevel(Enchantment enchantment, ItemStack stack, int level) {
        return level;
    }

    @Override
    public ActionResult mialib$checkEnchantment(EnchantmentTarget target, Enchantment enchantment) {
        return ActionResult.PASS;
    }
}