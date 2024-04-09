package xyz.amymialee.mialib.detonations;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.events.DetonationEvents;
import xyz.amymialee.mialib.util.runnables.QuinConsumer;
import xyz.amymialee.mialib.util.runnables.TriConsumer;
import xyz.amymialee.mialib.util.runnables.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.*;

@SuppressWarnings("unused")
public class Detonation {
    public static final Detonation GHAST_FIREBALL = new Detonation().setDestructionRadius(() -> 1d).setEntityRadius(() -> 3d).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 14f).setReplacementBlock((world, blockPos, vec3d) -> (world.random.nextBoolean() ? Blocks.AIR : Blocks.FIRE).getDefaultState()).seal();
    public static final Detonation WITHER_SKULL = new Detonation().setDestructionRadius(() -> 1d).setEntityRadius(() -> 3d).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 14f).setEntityEffectAction((distance, vec3d, entity) -> {
        if (entity instanceof LivingEntity living) {
            var world = entity.getWorld();
            var i = 0;
            if (world.getDifficulty() == Difficulty.NORMAL) {
                i = 10;
            } else if (world.getDifficulty() == Difficulty.HARD) {
                i = 40;
            }
            if (i > 0) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * i, 1));
            }
        }
    }).seal();
    public static final Detonation CREEPER = new Detonation().setDestructionRadius(() -> 3d).setEntityRadius(() -> 3d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 3f * 14f).seal();
    public static final Detonation TNT = new Detonation().setDestructionRadius(() -> 4d).setEntityRadius(() -> 4d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 4f * 14f).seal();
    public static final Detonation BED = new Detonation().setDestructionRadius(() -> 5d).setEntityRadius(() -> 5d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 5f * 14f).setReplacementBlock((world, blockPos, vec3d) -> (world.random.nextBoolean() ? Blocks.AIR : Blocks.FIRE).getDefaultState()).seal();
    public static final Detonation RESPAWN_ANCHOR = new Detonation().setDestructionRadius(() -> 5d).setEntityRadius(() -> 5d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 5f * 14f).setReplacementBlock((world, blockPos, vec3d) -> (world.random.nextBoolean() ? Blocks.AIR : Blocks.FIRE).getDefaultState()).seal();
    public static final Detonation CHARGED_CREEPER = new Detonation().setDestructionRadius(() -> 6d).setEntityRadius(() -> 6d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 6f * 14f).seal();
    public static final Detonation END_CRYSTAL = new Detonation().setDestructionRadius(() -> 6d).setEntityRadius(() -> 6d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 6f * 14f).seal();
    public static final Detonation WITHER_SPAWN = new Detonation().setDestructionRadius(() -> 7d).setEntityRadius(() -> 7d * 2 + 1).setHorizontalPushback(() -> 1d).setVerticalPushback(() -> 1d).setDamage(() -> 7f * 14f).seal();
    private static final double GOLDEN_ANGLE = Math.PI * (3 - Math.sqrt(5));
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
    protected QuinConsumer<Double, Vec3d, Entity, Entity, Entity> damageEntity = (distance, pos, target, attacker, projectile) -> {
        if (!target.isImmuneToExplosion()) {
            var falloff = this.falloff.apply(distance, this.entityRadius.get()) * getExposure(pos, target);
            var damage = (float) ((falloff * falloff + falloff) / this.damage.get() + 1.0);
            target.damage(target.getDamageSources().create(this.damageType.apply(distance), attacker, projectile), damage);
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
    /**
     * If the detonation is sealed, it cannot be modified, however copies can be made.
     */
    protected boolean sealed = false;

    public BiFunction<Double, Double, Double> getFalloffFunction() {
        return this.falloff;
    }

    public double getFalloff(double distance, double maxRange) {
        return this.falloff.apply(distance, maxRange);
    }

    public Supplier<Double> getDestructionRadiusSupplier() {
        return this.destructionRadius;
    }

    public double getDestructionRadius() {
        return this.destructionRadius.get();
    }

    public Supplier<Double> getEntityRadiusSupplier() {
        return this.entityRadius;
    }

    public double getEntityRadius() {
        return this.entityRadius.get();
    }

    public Supplier<Double> getHorizontalPushbackSupplier() {
        return this.horizontalPushback;
    }

    public double getHorizontalPushback() {
        return this.horizontalPushback.get();
    }

    public Supplier<Double> getVerticalPushbackSupplier() {
        return this.verticalPushback;
    }

    public double getVerticalPushback() {
        return this.verticalPushback.get();
    }

    public Supplier<Float> getDamageSupplier() {
        return this.damage;
    }

    public float getDamage() {
        return this.damage.get();
    }

    public Function<Double, Float> getSofteningFunction() {
        return this.softening;
    }

    public float getSoftening(double distance) {
        return this.softening.apply(distance);
    }

    public Function<Double, Float> getSparsenessFunction() {
        return this.sparseness;
    }

    public float getSparseness(double distance) {
        return this.sparseness.apply(distance);
    }

    public Function<Double, RegistryKey<DamageType>> getDamageTypeFunction() {
        return this.damageType;
    }

    public RegistryKey<DamageType> getDamageType(double distance) {
        return this.damageType.apply(distance);
    }

    public Predicate<Entity> getEntityPredicate() {
        return this.entityPredicate;
    }

    public boolean shouldAffectEntity(Entity entity) {
        return this.entityPredicate.test(entity);
    }

    public QuinConsumer<Double, Vec3d, Entity, Entity, Entity> getDamageEntityAction() {
        return this.damageEntity;
    }

    public void applyDamageToEntity(double distance, Vec3d explosionPos, Entity target, Entity attacker, Entity projectile) {
        this.damageEntity.accept(distance, explosionPos, target, attacker, projectile);
    }

    public TriConsumer<Double, Vec3d, Entity> getPushbackEntityAction() {
        return this.pushbackEntity;
    }

    public void applyEntityPushback(double distance, Vec3d pos, Entity entity) {
        this.pushbackEntity.accept(distance, pos, entity);
    }

    public TriConsumer<Double, Vec3d, Entity> getEntityEffectsAction() {
        return this.entityEffects;
    }

    public void applyEntityEffects(double distance, Vec3d pos, Entity entity) {
        this.entityEffects.accept(distance, pos, entity);
    }

    public TriFunction<World, BlockPos, Vec3d, BlockState> getReplacementBlockFunction() {
        return this.replacementBlock;
    }

    public BlockState getReplacementBlock(World world, BlockPos blockpos, Vec3d pos) {
        return this.replacementBlock.apply(world, blockpos, pos);
    }

    public BiConsumer<ServerWorld, Vec3d> getDetonationEffectsAction() {
        return this.detonationEffects;
    }

    public void detonationEffects(ServerWorld world, Vec3d pos) {
        this.detonationEffects.accept(world, pos);
    }

    public boolean isSealed() {
        return this.sealed;
    }

    public Detonation setFalloff(BiFunction<Double, Double, Double> falloff) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set falloff on a sealed detonation");
            return this;
        }
        this.falloff = falloff;
        return this;
    }

    public Detonation setDestructionRadius(Supplier<Double> destructionRadius) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set destruction radius supplier on a sealed detonation");
            return this;
        }
        this.destructionRadius = destructionRadius;
        return this;
    }

    public Detonation setDestructionRadius(double destructionRadius) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set destruction radius on a sealed detonation");
            return this;
        }
        this.destructionRadius = () -> destructionRadius;
        return this;
    }

    public Detonation setEntityRadius(Supplier<Double> entityRadius) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set entity radius supplier on a sealed detonation");
            return this;
        }
        this.entityRadius = entityRadius;
        return this;
    }

    public Detonation setEntityRadius(double entityRadius) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set entity radius on a sealed detonation");
            return this;
        }
        this.entityRadius = () -> entityRadius;
        return this;
    }

    public Detonation setHorizontalPushback(Supplier<Double> horizontalPushback) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set horizontal pushback supplier on a sealed detonation");
            return this;
        }
        this.horizontalPushback = horizontalPushback;
        return this;
    }

    public Detonation setHorizontalPushback(double horizontalPushback) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set horizontal pushback on a sealed detonation");
            return this;
        }
        this.horizontalPushback = () -> horizontalPushback;
        return this;
    }

    public Detonation setVerticalPushback(Supplier<Double> verticalPushback) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set vertical pushback supplier on a sealed detonation");
            return this;
        }
        this.verticalPushback = verticalPushback;
        return this;
    }

    public Detonation setVerticalPushback(double verticalPushback) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set vertical pushback on a sealed detonation");
            return this;
        }
        this.verticalPushback = () -> verticalPushback;
        return this;
    }

    public Detonation setDamage(Supplier<Float> damage) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set damage supplier on a sealed detonation");
            return this;
        }
        this.damage = damage;
        return this;
    }

    public Detonation setDamage(double damage) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set damage on a sealed detonation");
            return this;
        }
        this.damage = () -> (float) damage;
        return this;
    }

    public Detonation setSoftening(Function<Double, Float> softening) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set softening function on a sealed detonation");
            return this;
        }
        this.softening = softening;
        return this;
    }

    public Detonation setSoftening(double softening) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set softening on a sealed detonation");
            return this;
        }
        this.softening = (distance) -> (float) softening;
        return this;
    }

    public Detonation setSparseness(Function<Double, Float> sparseness) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set sparseness function on a sealed detonation");
            return this;
        }
        this.sparseness = sparseness;
        return this;
    }

    public Detonation setSparseness(double sparseness) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set sparseness on a sealed detonation");
            return this;
        }
        this.sparseness = (distance) -> (float) sparseness;
        return this;
    }

    public Detonation setDamageType(Function<Double, RegistryKey<DamageType>> damageType) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set damage type function on a sealed detonation");
            return this;
        }
        this.damageType = damageType;
        return this;
    }

    public Detonation setDamageType(RegistryKey<DamageType> damageType) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set damage type on a sealed detonation");
            return this;
        }
        this.damageType = (distance) -> damageType;
        return this;
    }

    public Detonation setEntityPredicate(Predicate<Entity> entityPredicate) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set entity predicate on a sealed detonation");
            return this;
        }
        this.entityPredicate = entityPredicate;
        return this;
    }

    public Detonation setEntityDamageAction(QuinConsumer<Double, Vec3d, Entity, Entity, Entity> damageEntity) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set entity damage action on a sealed detonation");
            return this;
        }
        this.damageEntity = damageEntity;
        return this;
    }

    public Detonation setEntityPushbackAction(TriConsumer<Double, Vec3d, Entity> pushbackEntity) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set entity pushback on a sealed detonation");
            return this;
        }
        this.pushbackEntity = pushbackEntity;
        return this;
    }

    public Detonation setEntityEffectAction(TriConsumer<Double, Vec3d, Entity> entityEffects) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set entity effects on a sealed detonation");
            return this;
        }
        this.entityEffects = entityEffects;
        return this;
    }

    public Detonation setReplacementBlock(TriFunction<World, BlockPos, Vec3d, BlockState> replacementBlock) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set replacement blocks on a sealed detonation");
            return this;
        }
        this.replacementBlock = replacementBlock;
        return this;
    }

    public Detonation setDetonationEffects(BiConsumer<ServerWorld, Vec3d> detonationEffects) {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to set detonation effects on a sealed detonation");
            return this;
        }
        this.detonationEffects = detonationEffects;
        return this;
    }

    public Detonation seal() {
        if (this.sealed) {
            MiaLib.LOGGER.error("Tried to seal an already sealed detonation");
            return this;
        }
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
        this.detonationEffects(world, pos);
        var destructionRadius = this.getDestructionRadius();
        if (destructionRadius > 0) {
            var affectedBlocks = new HashMap<BlockPos, BlockState>();
            var rays = distributeRays(destructionRadius);
            var rayPotency = rays.size() / 1408f;
            for (var ray : rays) {
                var m = pos.x;
                var n = pos.y;
                var o = pos.z;
                for (var h = destructionRadius * (0.7f + world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.225f) {
                    var blockPos = new BlockPos((int) m, (int) n, (int) o);
                    if (!world.isInBuildLimit(blockPos)) break;
                    var blockState = world.getBlockState(blockPos);
                    var fluidState = world.getFluidState(blockPos);
                    if (!blockState.isAir() || !fluidState.isEmpty()) {
                        h -= (Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()) * (1 - this.getSoftening(pos.distanceTo(Vec3d.ofCenter(blockPos)))) + 0.3f) * rayPotency * 0.3f;
                    }
                    if (h > 0.0f && !affectedBlocks.containsKey(blockPos)) {
                        affectedBlocks.put(blockPos, this.getReplacementBlock(world, blockPos, pos));
                    }
                    m += ray.x * (double) 0.3f;
                    n += ray.y * (double) 0.3f;
                    o += ray.z * (double) 0.3f;
                }
            }
            DetonationEvents.DETONATION_DESTRUCTION.invoker().modifyInteractions(world, pos, this, affectedBlocks);
            var tool = owner instanceof LivingEntity living ? living.getMainHandStack() : ItemStack.EMPTY;
            for (var pair : affectedBlocks.entrySet()) {
                var blockPos = pair.getKey();
                var distance = pos.distanceTo(Vec3d.ofCenter(blockPos));
                var replacement = pair.getValue();
                var blockState = world.getBlockState(blockPos);
                if (!blockState.equals(replacement) && world.random.nextFloat() > this.getSparseness(distance)) {
                    var blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
                    var builder = new LootContextParameterSet.Builder(world)
                            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                            .addOptional(LootContextParameters.TOOL, tool)
                            .addOptional(LootContextParameters.THIS_ENTITY, owner)
                            .addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity);
                    blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY, owner instanceof PlayerEntity);
                    blockState.getDroppedStacks(builder).forEach(stack -> Block.dropStack(world, blockPos, stack));
                    world.setBlockState(blockPos, replacement, Block.NOTIFY_ALL);
                }
            }
        }
        var entityRadius = this.getEntityRadius();
        var entities = world.getOtherEntities(null, Box.of(pos, entityRadius, entityRadius, entityRadius), this.getEntityPredicate());
        for (var entity : entities) {
            var distance = pos.distanceTo(entity.getPos());
            this.applyDamageToEntity(distance, pos, entity, owner, projectile);
            this.applyEntityPushback(distance, pos, entity);
            this.applyEntityEffects(distance, pos, entity);
        }
    }

    public static @NotNull List<Vec3d> distributeRays(double explosionRadius) {
        var numberOfRays = 256 * explosionRadius;
        var rays = new ArrayList<Vec3d>();
        for (var i = 0; i < numberOfRays; i++) {
            var y = 1 - (i / (numberOfRays - 1)) * 2;
            var radius = Math.sqrt(1 - y * y);
            var theta = GOLDEN_ANGLE * i;
            var x = Math.cos(theta) * radius;
            var z = Math.sin(theta) * radius;
            rays.add(new Vec3d(x, y, z).normalize());
        }
        return rays;
    }

    public static float getExposure(Vec3d source, @NotNull Entity entity) {
        var box = entity.getBoundingBox();
        var d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        var e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        var f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        var g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        var h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
            var i = 0;
            var j = 0;
            for (var k = 0.0; k <= 1.0; k += d) {
                for (var l = 0.0; l <= 1.0; l += e) {
                    for (var m = 0.0; m <= 1.0; m += f) {
                        var n = MathHelper.lerp(k, box.minX, box.maxX);
                        var o = MathHelper.lerp(l, box.minY, box.maxY);
                        var p = MathHelper.lerp(m, box.minZ, box.maxZ);
                        var vec3d = new Vec3d(n + g, o, p + h);
                        if (entity.getWorld().raycast(new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS) {
                            i++;
                        }
                        j++;
                    }
                }
            }
            return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }
}