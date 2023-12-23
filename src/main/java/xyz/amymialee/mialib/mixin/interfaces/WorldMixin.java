package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.interfaces.MWorld;

@Mixin(World.class)
public class WorldMixin implements MWorld {}