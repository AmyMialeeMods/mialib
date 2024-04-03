package xyz.amymialee.mialib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.amymialee.mialib.detonations.Detonation;

import java.util.Map;

public class DetonationEvents {
    public static final Event<DetonationDestructionCallback> DETONATION_DESTRUCTION = EventFactory.createArrayBacked(DetonationDestructionCallback.class, callbacks -> (world, pos, detonation, blocks) -> {
        for (var callback : callbacks) callback.modifyInteractions(world, pos, detonation, blocks);
    });

    @FunctionalInterface
    public interface DetonationDestructionCallback {
        void modifyInteractions(ServerWorld world, Vec3d pos, Detonation detonation, Map<BlockPos, BlockState> blocks);
    }
}