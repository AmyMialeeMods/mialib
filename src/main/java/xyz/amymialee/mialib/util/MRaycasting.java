package xyz.amymialee.mialib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public @SuppressWarnings("unused") interface MRaycasting {
	BiPredicate<PlayerEntity, Entity> ANY_PLAYER_AND_ENTITY = (p, e) -> true;
	Predicate<Entity> ANY_ENTITY = (e) -> true;

	static @NotNull List<Entity> raycast(@NotNull PlayerEntity entity, double distance) {
		return raycast(entity, distance, ANY_PLAYER_AND_ENTITY);
	}

	static @NotNull List<Entity> raycast(@NotNull PlayerEntity entity, double distance, BiPredicate<PlayerEntity, Entity> filter) {
		return raycast(entity, distance, filter, 0);
	}

	static @NotNull List<Entity> raycast(@NotNull PlayerEntity entity, double distance, BiPredicate<PlayerEntity, Entity> filter, int maxHits) {
		return raycast(entity, distance, filter, maxHits, 0);
	}

	static @NotNull List<Entity> raycast(@NotNull PlayerEntity entity, double distance, BiPredicate<PlayerEntity, Entity> filter, int maxHits, double rayRadius) {
		return raycast(entity.getWorld(), entity.getEyePos(), anglesToVector(entity.getPitch(1.0f), entity.getYaw(1.0f)), distance, (e) -> filter.test(entity, e), rayRadius, maxHits);
	}

	static @NotNull List<Entity> raycast(@NotNull World world, @NotNull Vec3d startPos, @NotNull Vec3d angle, double distance) {
		return raycast(world, startPos, angle, distance, ANY_ENTITY);
	}

	static @NotNull List<Entity> raycast(@NotNull World world, @NotNull Vec3d startPos, @NotNull Vec3d angle, double distance, @NotNull Predicate<Entity> filter) {
		return raycast(world, startPos, angle, distance, filter, 0);
	}

	static @NotNull List<Entity> raycast(@NotNull World world, @NotNull Vec3d startPos, @NotNull Vec3d angle, double distance, @NotNull Predicate<Entity> filter, double rayRadius) {
		return raycast(world, startPos, angle, distance, filter, rayRadius, 0);
	}

	static @NotNull List<Entity> raycast(@NotNull World world, @NotNull Vec3d startPos, @NotNull Vec3d angle, double distance, @NotNull Predicate<Entity> filter, double rayRadius, int maxHits) {
		var endPosition = startPos.add(angle.multiply(distance));
		List<Entity> hitEntities = new ArrayList<>();
		for (var target : world.getOtherEntities(null, Box.of(startPos, 0.1, 0.1, 0.1).expand(distance, distance, distance), filter)) {
			var intersection = intersects(startPos, endPosition, target, rayRadius);
			if (intersection) {
				var visible = false;
				for (var pos : new Vec3d[]{target.getPos(), target.getPos().add(0, target.getHeight() / 2, 0), target.getPos().add(0, target.getHeight(), 0)}) {
					if (world.raycast(new RaycastContext(startPos, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, target)).getType() == HitResult.Type.MISS) {
						visible = true;
						break;
					}
				}
				if (visible) {
					hitEntities.add(target);
					if (maxHits > 0 && hitEntities.size() >= maxHits) break;
				}
			}
		}
		return hitEntities;
	}

	static @NotNull Vec3d anglesToVector(double pitch, double yaw) {
		var x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
		var y = -Math.sin(Math.toRadians(pitch));
		var z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
		return new Vec3d(x, y, z);
	}

	static boolean intersects(Vec3d start, @NotNull Vec3d end, @NotNull Entity entity, double rayRadius) {
		var entityMin = entity.getPos().subtract(entity.getWidth() / 2 + rayRadius, rayRadius, entity.getWidth() / 2 + rayRadius);
		var entityMax = entity.getPos().add(entity.getWidth() / 2 + rayRadius, entity.getHeight() + rayRadius, entity.getWidth() / 2 + rayRadius);
		return intersects(start, end, new Box(entityMin, entityMax), rayRadius);
	}

	static boolean intersects(Vec3d start, @NotNull Vec3d end, @NotNull Box box, double rayRadius) {
		var direction = end.subtract(start).normalize();
		var boxMin = new Vec3d(box.minX - rayRadius, box.minY - rayRadius, box.minZ - rayRadius);
		var boxMax = new Vec3d(box.maxX + rayRadius, box.maxY + rayRadius, box.maxZ + rayRadius);
		var tMin = 0d;
		var tMax = Double.MAX_VALUE;
		for (var axis : Direction.Axis.values()) {
			if (Math.abs(direction.getComponentAlongAxis(axis)) < 1e-8) {
				if (start.getComponentAlongAxis(axis) < boxMin.getComponentAlongAxis(axis) || start.getComponentAlongAxis(axis) > boxMax.getComponentAlongAxis(axis)) {
					return false;
				}
			} else {
				var ood = 1.0 / direction.getComponentAlongAxis(axis);
				var t1 = (boxMin.getComponentAlongAxis(axis) - start.getComponentAlongAxis(axis)) * ood;
				var t2 = (boxMax.getComponentAlongAxis(axis) - start.getComponentAlongAxis(axis)) * ood;
				if (t1 > t2) {
					var temp = t1;
					t1 = t2;
					t2 = temp;
				}
				tMin = Math.max(tMin, t1);
				tMax = Math.min(tMax, t2);
				if (tMin > tMax) return false;
			}
		}
		return true;
	}
}