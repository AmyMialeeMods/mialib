package xyz.amymialee.mialib.util;

@SuppressWarnings("unused")
public interface MMath {
	static int lerp(int a, int b, double t) {
		return (int) Math.round(a + (b - a) * t);
	}

	static long lerp(long a, long b, double t) {
		return Math.round(a + (b - a) * t);
	}

	static float lerp(float a, float b, double t) {
		return (float) (a + (b - a) * t);
	}

	static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	static int clampLoop(int value, int min, int max) {
		if (value < min) {
			return max - (min - value) % (max - min);
		} else {
			return min + (value - min) % (max - min);
		}
	}

	static long clampLoop(long value, long min, long max) {
		if (value < min) {
			return max - (min - value) % (max - min);
		} else {
			return min + (value - min) % (max - min);
		}
	}

	static float clampLoop(float value, float min, float max) {
		if (value < min) {
			return max - (min - value) % (max - min);
		} else {
			return min + (value - min) % (max - min);
		}
	}

	static double clampLoop(double value, double min, double max) {
		if (value < min) {
			return max - (min - value) % (max - min);
		} else {
			return min + (value - min) % (max - min);
		}
	}
}
