package xyz.amymialee.mialib.util;

import java.util.Random;

public interface MRandom {
	Random RANDOM = new Random();

	static int getRandomBetween(int min, int max) {
		return RANDOM.nextInt(max - min + 1) + min;
	}

	static long getRandomBetween(long min, long max) {
		return RANDOM.nextLong() % (max - min + 1) + min;
	}

	static float getRandomBetween(float min, float max) {
		return RANDOM.nextFloat() % (max - min + 1) + min;
	}

	static double getRandomBetween(double min, double max) {
		return RANDOM.nextDouble() % (max - min + 1) + min;
	}
}