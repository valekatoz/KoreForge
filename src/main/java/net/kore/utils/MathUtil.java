package net.kore.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtil
{
    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static double round(double value, int places) {
        if (places < 0 || !Double.isFinite(value)) {
            return 0;
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
    private static final Random rand;

    private boolean isEven(final double num) {
        return !this.isOdd(num);
    }

    private boolean isOdd(final double num) {
        return !this.isEven(num);
    }

    public static double getRandomInRange(final double max, final double min) {
        return min + (max - min) * MathUtil.rand.nextFloat();
    }

    public static int getRandomInRange(int bound)
    {
        return MathUtil.rand.nextInt(bound);
    }

    public static double clamp(final double num, double max, double min) {
        if (max < min) {
            final double temp = max;
            max = min;
            min = temp;
        }
        return Math.max(Math.min(max, num), min);
    }

    public static int clamp(final int num, int max, int min) {
        if (max < min) {
            final int temp = max;
            max = min;
            min = temp;
        }
        return Math.max(Math.min(max, num), min);
    }

    public static float clamp(final float num, float max, float min) {
        if (max < min) {
            final float temp = max;
            max = min;
            min = temp;
        }
        return Math.max(Math.min(max, num), min);
    }

    public static double hypot(final double a, final double b) {
        return Math.sqrt(a * a + b * b);
    }

    static {
        rand = new Random();
    }
}
