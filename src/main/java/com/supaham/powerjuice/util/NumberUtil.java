package com.supaham.powerjuice.util;

import java.util.Random;

import lombok.Getter;

/**
 * Contains Number related utility methods.
 */
public class NumberUtil {

    @Getter
    private static Random random = new Random();
    
    /**
     * @see Random#nextInt(int) 
     */
    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Returns a pseudo-random int between min and max, inclusive.
     *
     * @param min min range
     * @param max max range
     * @return random int in the given range.
     */
    public static int nextInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Returns a pseudo-random double between min and max, inclusive.
     *
     * @param min min range
     * @param max max range
     * @return random double in the given range.
     */
    public static double getRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
