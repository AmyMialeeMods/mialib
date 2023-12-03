package xyz.amymialee.mialib.util;

import xyz.amymialee.mialib.MiaLib;

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

	static boolean getByteFlag(byte data, int flag) {
		if (flag < 0 || flag > 8) {
			MiaLib.LOGGER.warn("Invalid byte flag index: " + flag);
			return false;
		}
		return (data >> flag & 0x01) == 1;
	}

	static byte setByteFlag(byte data, int flag, boolean value) {
		if (flag < 0 || flag > 8) {
			MiaLib.LOGGER.warn("Invalid byte flag index: " + flag);
			return data;
		}
		if (value) {
			return (byte) (data | 1 << flag);
		} else {
			return (byte) (data & ~(1 << flag));
		}
	}
}