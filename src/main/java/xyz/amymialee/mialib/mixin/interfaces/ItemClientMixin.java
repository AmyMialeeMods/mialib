package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.util.interfaces.MItemClient;

@Mixin(Item.class)
public class ItemClientMixin implements MItemClient {}