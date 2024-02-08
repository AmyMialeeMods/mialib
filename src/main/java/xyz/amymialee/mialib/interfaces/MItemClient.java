package xyz.amymialee.mialib.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public interface MItemClient {
    default BipedEntityModel.ArmPose mialib$pose(LivingEntity entity, Hand hand, ItemStack stack) {
        return null;
    }

    default boolean mialib$shouldHideInHand(LivingEntity entity, Hand hand, ItemStack stack) {
        return false;
    }

    default void mialib$renderCustomBar(DrawContext drawContext, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {}
}