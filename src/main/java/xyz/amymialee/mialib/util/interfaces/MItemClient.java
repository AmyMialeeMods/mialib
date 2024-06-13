package xyz.amymialee.mialib.util.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface MItemClient {
    @Environment(EnvType.CLIENT)
    default BipedEntityModel.ArmPose mialib$pose(LivingEntity entity, Hand hand, ItemStack stack) {
        return null;
    }

    @Environment(EnvType.CLIENT)
    default boolean mialib$shouldHideInHand(LivingEntity entity, Hand hand, ItemStack stack) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    default void mialib$renderCustomBar(DrawContext drawContext, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {}
}