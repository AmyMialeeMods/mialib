package xyz.amymialee.mialib.util;

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
		var range = max - min + 1;
		while (value < min) {
			value += range;
		}
		while (value > max) {
			value -= range;
		}
		return value;
	}

	static long clampLoop(long value, long min, long max) {
		var range = max - min + 1;
		while (value < min) {
			value += range;
		}
		while (value > max) {
			value -= range;
		}
		return value;
	}

	static float clampLoop(float value, float min, float max) {
		var range = max - min + 1;
		while (value < min) {
			value += range;
		}
		while (value > max) {
			value -= range;
		}
		return value;
	}

	static double clampLoop(double value, double min, double max) {
		var range = max - min + 1;
		while (value < min) {
			value += range;
		}
		while (value > max) {
			value -= range;
		}
		return value;
	}

	static boolean getByteFlag(byte data, int flag) {
		if (flag < 0 || flag > 8) {
//			MiaLib.LOGGER.warn("Invalid byte flag index: " + flag);
			return false;
		}
		return (data >> flag & 0x01) == 1;
	}

	static byte setByteFlag(byte data, int flag, boolean value) {
		if (flag < 0 || flag > 8) {
//			MiaLib.LOGGER.warn("Invalid byte flag index: " + flag);
			return data;
		}
		if (value) {
			return (byte) (data | 1 << flag);
		} else {
			return (byte) (data & ~(1 << flag));
		}
	}
}