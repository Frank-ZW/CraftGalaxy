package net.craftgalaxy.mavic.util.java;

public class MathUtil {

	public static int highestInt(Number... numbers) {
		switch (numbers.length) {
			case 0: return Integer.MAX_VALUE;
			case 1: return numbers[0].intValue();
			default:
				int highest = numbers[0].intValue();
				for (int i = 1; i < numbers.length; i++) {
					if (numbers[i].intValue() > highest) {
						highest = numbers[i].intValue();
					}
				}

				return highest;
		}
	}

	public static double highestAbsDouble(Number... numbers) {
		switch (numbers.length) {
			case 0: return 0.0D;
			case 1: return numbers[0].doubleValue();
			default:
				double value = numbers[0].doubleValue();
				for (int i = 1; i < numbers.length; i++) {
					if (Math.abs(numbers[i].doubleValue()) > Math.abs(value)) {
						value = numbers[i].doubleValue();
					}
				}

				return value;
		}
	}

	public static double clampDouble(double num, double min, double max) {
		return num < min ? min : Math.min(num, max);
	}

	public static double getDistanceBetweenAngles(double angle1, double angle2) {
		double abs = Math.abs(angle1 % 360.0D - angle2 % 360.0D);
		return Math.abs(Math.min(360.0D - abs, abs));
	}
}
