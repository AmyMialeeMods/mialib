package xyz.amymialee.mialib.util;

import xyz.amymialee.mialib.MiaLib;

public interface MData {
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