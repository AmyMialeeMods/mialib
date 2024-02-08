package xyz.amymialee.mialib.detonations;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.util.QuadConsumer;
import xyz.amymialee.mialib.util.TriConsumer;
import xyz.amymialee.mialib.util.TriFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public class Detonation {
    public static final Detonation CREEPER = new Detonation().setDestructionRadius(() -> 3d).setEntityRadius(() -> 3d).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 6f).seal();
    public static final Detonation TNT = new Detonation().setDestructionRadius(() -> 4d).setEntityRadius(() -> 4d).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 8f).seal();
    public static final Detonation CHARGED_CREEPER = new Detonation().setDestructionRadius(() -> 6d).setEntityRadius(() -> 6d).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 10f).seal();
    public static final Detonation END_CRYSTAL = new Detonation().setDestructionRadius(() -> 6d).setEntityRadius(() -> 6d).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 12f).seal();
    /**
     * Formula for entity interaction falloff
     * Always returns a value between 0 and 1
     */
    protected BiFunction<Double, Double, Double> falloff = (distance, maxRange) -> Math.pow(MathHelper.clamp(1 - distance / maxRange, 0, 1), 2);
    /**
     * Power for block interactions
     * Effects how tough blocks are to break
     */
    protected Supplier<Double> destructionRadius = () -> 0d;
    /**
     * Maximum distance for entity interactions
     */
    protected Supplier<Double> entityRadius = () -> 3d;
    /**
     * Calculates the horizontal pushback for an entity using distance, should use {@link #falloff}
     */
    protected Supplier<Double> horizontalPushback = () -> 0d;
    /**
     * Calculates the vertical pushback for an entity using distance, should use {@link #falloff}
     */
    protected Supplier<Double> verticalPushback = () -> 0d;
    /**
     * Calculates the damage for an entity using distance, should use {@link #falloff}
     */
    protected Supplier<Float> damage = () -> 0f;
    /**
     * Gives the detonation a softening effect for blocks.
     * Block hardness is reduced by this value as a percent, meaning a value of 1 will break any block ignoring all hardness.
     */
    protected Function<Double, Float> softening = (distance) -> 0f;
    /**
     * Gives the detonation a sparseness effect for blocks.
     * This is a percentage chance that a block will not be destroyed or changed, meaning a value of 1 will never destroy or change any blocks.
     */
    protected Function<Double, Float> sparseness = (distance) -> 0f;
    /**
     * Damage type used for entity damaging.
     */
    protected Function<Double, RegistryKey<DamageType>> damageType = (distance) -> DamageTypes.EXPLOSION;
    /**
     * Predicate to know which entities should be affected by the detonation.
     */
    protected Predicate<Entity> entityPredicate = (entity) -> true;
    /**
     * Damages entities in the detonation.
     */
    protected QuadConsumer<Double, Entity, Entity, Entity> damageEntity = (distance, target, attacker, projectile) -> {
        var damage = this.damage.get() * this.falloff.apply(distance, this.entityRadius.get());
        if (damage > 0) {
            target.damage(target.getDamageSources().create(this.damageType.apply(distance), attacker, projectile), (float) damage);
        }
    };
    /**
     * Pushes away entities in the detonation.
     */
    protected TriConsumer<Double, Vec3d, Entity> pushbackEntity = (distance, vec3d, entity) -> {
        var difference = entity.getPos().subtract(vec3d);
        var pushback = difference.multiply(new Vec3d(this.horizontalPushback.get(), this.verticalPushback.get(), this.horizontalPushback.get()).multiply(this.falloff.apply(distance, this.entityRadius.get())));
        entity.setVelocity(entity.getVelocity().add(pushback));
    };
    /**
     * Effects to apply to entities.
     * Doesn't do anything by default.
     * Takes the world, the position of the detonation, and the entity to affect.
     */
    protected TriConsumer<Double, Vec3d, Entity> entityEffects = (distance, vec3d, entity) -> {};
    /**
     * Block to replace broken blocks with.
     * Can change over distance.
     */
    protected TriFunction<World, BlockPos, Vec3d, BlockState> replacementBlock = (world, blockpos, pos) -> Blocks.AIR.getDefaultState();
    /**
     * Effects for the detonation to have.
     */
    protected BiConsumer<ServerWorld, Vec3d> detonationEffects = (world, vec3d) -> {
        world.playSound(null, vec3d.x, vec3d.y, vec3d.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, vec3d.x, vec3d.y, vec3d.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    };
    protected boolean sealed = false;

    public Detonation setFalloff(BiFunction<Double, Double, Double> falloff) {
        this.falloff = falloff;
        return this;
    }

    public Detonation setDestructionRadius(Supplier<Double> destructionRadius) {
        this.destructionRadius = destructionRadius;
        return this;
    }

    public Detonation setDestructionRadius(double destructionRadius) {
        this.destructionRadius = () -> destructionRadius;
        return this;
    }

    public Detonation setEntityRadius(Supplier<Double> entityRadius) {
        this.entityRadius = entityRadius;
        return this;
    }

    public Detonation setEntityRadius(double entityRadius) {
        this.entityRadius = () -> entityRadius;
        return this;
    }

    public Detonation setHorizontalPushback(Supplier<Double> horizontalPushback) {
        this.horizontalPushback = horizontalPushback;
        return this;
    }

    public Detonation setHorizontalPushback(double horizontalPushback) {
        this.horizontalPushback = () -> horizontalPushback;
        return this;
    }

    public Detonation setVerticalPushback(Supplier<Double> verticalPushback) {
        this.verticalPushback = verticalPushback;
        return this;
    }

    public Detonation setVerticalPushback(double verticalPushback) {
        this.verticalPushback = () -> verticalPushback;
        return this;
    }

    public Detonation setDamage(Supplier<Float> damage) {
        this.damage = damage;
        return this;
    }

    public Detonation setDamage(double damage) {
        this.damage = () -> (float) damage;
        return this;
    }

    public Detonation setSoftening(Function<Double, Float> softening) {
        this.softening = softening;
        return this;
    }

    public Detonation setSoftening(double softening) {
        this.softening = (distance) -> (float) softening;
        return this;
    }

    public Detonation setSparseness(Function<Double, Float> sparseness) {
        this.sparseness = sparseness;
        return this;
    }

    public Detonation setSparseness(double sparseness) {
        this.sparseness = (distance) -> (float) sparseness;
        return this;
    }

    public Detonation setDamageType(Function<Double, RegistryKey<DamageType>> damageType) {
        this.damageType = damageType;
        return this;
    }

    public Detonation setDamageType(RegistryKey<DamageType> damageType) {
        this.damageType = (distance) -> damageType;
        return this;
    }

    public Detonation setEntityPredicate(Predicate<Entity> entityPredicate) {
        this.entityPredicate = entityPredicate;
        return this;
    }

    public Detonation setEntityDamageAction(QuadConsumer<Double, Entity, Entity, Entity> damageEntity) {
        this.damageEntity = damageEntity;
        return this;
    }

    public Detonation setEntityPushbackAction(TriConsumer<Double, Vec3d, Entity> pushbackEntity) {
        this.pushbackEntity = pushbackEntity;
        return this;
    }

    public Detonation setEntityEffectAction(TriConsumer<Double, Vec3d, Entity> entityEffects) {
        this.entityEffects = entityEffects;
        return this;
    }

    public Detonation setReplacementBlock(TriFunction<World, BlockPos, Vec3d, BlockState> replacementBlock) {
        this.replacementBlock = replacementBlock;
        return this;
    }

    public Detonation setDetonationEffects(BiConsumer<ServerWorld, Vec3d> detonationEffects) {
        this.detonationEffects = detonationEffects;
        return this;
    }

    public Detonation seal() {
        this.sealed = true;
        return this;
    }

    public Detonation copy() {
        var copy = new Detonation();
        copy.falloff = this.falloff;
        copy.destructionRadius = this.destructionRadius;
        copy.entityRadius = this.entityRadius;
        copy.horizontalPushback = this.horizontalPushback;
        copy.verticalPushback = this.verticalPushback;
        copy.damage = this.damage;
        copy.softening = this.softening;
        copy.sparseness = this.sparseness;
        copy.damageType = this.damageType;
        copy.entityPredicate = this.entityPredicate;
        copy.damageEntity = this.damageEntity;
        copy.pushbackEntity = this.pushbackEntity;
        copy.entityEffects = this.entityEffects;
        copy.replacementBlock = this.replacementBlock;
        copy.detonationEffects = this.detonationEffects;
        return copy;
    }

    public void executeDetonation(@NotNull ServerWorld world, Vec3d pos) {
        this.executeDetonation(world, pos, null, null);
    }

    public void executeDetonation(@NotNull ServerWorld world, Vec3d pos, @Nullable Entity owner) {
        this.executeDetonation(world, pos, owner, null);
    }

    public void executeDetonation(@NotNull ServerWorld world, Vec3d pos, @Nullable Entity owner, @Nullable Entity projectile) {
        this.detonationEffects.accept(world, pos);
        double destructionRadius = this.destructionRadius.get();
        if (destructionRadius > 0) {
            var affectedBlocks = new HashMap<BlockPos, BlockState>();
            for (var i = 0; i < destructionRadius * 4; i++) {
                for (var j = 0; j < destructionRadius * 4; j++) {
                    for (var k = 0; k < destructionRadius * 4; k++) {
                        if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                            double d = (float) i / 15.0f * 2.0f - 1.0f;
                            double e = (float) j / 15.0f * 2.0f - 1.0f;
                            double f = (float) k / 15.0f * 2.0f - 1.0f;
                            var g = Math.sqrt(d * d + e * e + f * f);
                            d /= g;
                            e /= g;
                            f /= g;
                            var m = pos.x;
                            var n = pos.y;
                            var o = pos.z;
                            for (var h = destructionRadius * (0.7f + world.random.nextFloat() * 0.6f); 0.0f < h; h -= 0.225f) {
                                var blockPos = new BlockPos((int) m, (int) n, (int) o);
                                if (!world.isInBuildLimit(blockPos)) break;
                                var distance = pos.distanceTo(Vec3d.ofCenter(blockPos));
                                var blockState = world.getBlockState(blockPos);
                                var fluidState = world.getFluidState(blockPos);
                                if (!blockState.isAir() || !fluidState.isEmpty()) {
                                    h -= Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()) * (1 - this.softening.apply(distance));
                                }
                                if (h > 0.0f) {
                                    if (!affectedBlocks.containsKey(blockPos)) affectedBlocks.put(blockPos, this.replacementBlock.apply(world, blockPos, pos));
                                }
                                m += d * (double) 0.3f;
                                n += e * (double) 0.3f;
                                o += f * (double) 0.3f;
                            }
                        }
                    }
                }
            }
            DETONATION_DESTRUCTION.invoker().modifyInteractions(world, pos, this, affectedBlocks);
            for (var pair : affectedBlocks.entrySet()) {
                var blockPos = pair.getKey();
                var distance = pos.distanceTo(Vec3d.ofCenter(blockPos));
                var replacement = pair.getValue();
                var blockState = world.getBlockState(blockPos);
                if (!blockState.equals(replacement) && world.random.nextFloat() > this.sparseness.apply(distance)) {
                    var blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
                    var builder = new LootContextParameterSet.Builder(world)
                            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                            .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                            .addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity)
                            .addOptional(LootContextParameters.THIS_ENTITY, owner);
                    blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY, owner instanceof PlayerEntity);
                    blockState.getDroppedStacks(builder).forEach(stack -> Block.dropStack(world, blockPos, stack));
                    world.setBlockState(blockPos, replacement, Block.NOTIFY_ALL);
                }
            }
        }
        var entityRadius = this.entityRadius.get();
        var entities = world.getOtherEntities(null, Box.of(pos, entityRadius * 2, entityRadius * 2, entityRadius * 2), this.entityPredicate);
        for (var entity : entities) {
            var distance = pos.distanceTo(entity.getPos());
            this.damageEntity.accept(distance, entity, owner, projectile);
            this.pushbackEntity.accept(distance, pos, entity);
            this.entityEffects.accept(distance, pos, entity);
        }
    }

    public static final Event<DetonationDestructionCallback> DETONATION_DESTRUCTION = EventFactory.createArrayBacked(DetonationDestructionCallback.class, callbacks -> (world, pos, detonation, blocks) -> {
        for (var callback : callbacks) {
            callback.modifyInteractions(world, pos, detonation, blocks);
        }
    });

    @FunctionalInterface
    public interface DetonationDestructionCallback {
        void modifyInteractions(ServerWorld world, Vec3d pos, Detonation detonation, Map<BlockPos, BlockState> blocks);
    }
}