package xyz.amymialee.mialib.util;

import java.util.Random;

@SuppressWarnings("unused")
public interface MRandom {
	Random RANDOM = new Random();

	static int getRandomBetween(int min, int max) {
		return RANDOM.nextInt(max - min + 1) + min;
	}

	static long getRandomBetween(long min, long max) {
		return RANDOM.nextLong(min, max + 1);
	}

	static float getRandomBetween(float min, float max) {
		return RANDOM.nextFloat(min, max);
	}

	static double getRandomBetween(double min, double max) {
		return RANDOM.nextDouble(min, max + 1);
	}
}