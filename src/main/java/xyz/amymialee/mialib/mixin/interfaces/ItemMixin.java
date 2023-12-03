package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.interfaces.MItem;

@Mixin(Item.class)
public class ItemMixin implements MItem {}