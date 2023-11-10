package xyz.amymialee.mialib.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

@SuppressWarnings("unused")
public interface MRandom {
	Random RANDOM = new Random();

	static int getRandomBetween(@NotNull net.minecraft.util.math.random.Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	static long getRandomBetween(@NotNull net.minecraft.util.math.random.Random random, long min, long max) {
		return random.nextLong() % (max - min + 1) + min;
	}

	static float getRandomBetween(@NotNull net.minecraft.util.math.random.Random random, float min, float max) {
		return random.nextFloat() % (max - min + 1) + min;
	}

	static double getRandomBetween(@NotNull net.minecraft.util.math.random.Random random, double min, double max) {
		return random.nextDouble() % (max - min + 1) + min;
	}
}
