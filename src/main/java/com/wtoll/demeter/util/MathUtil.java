package com.wtoll.demeter.util;

import java.util.Random;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project demeter
 */
public class MathUtil {
    public static int binom(Random random, int n, float p) {
        int i = 0;
        for(int j = 0; j < n; j++) {
            if (random.nextFloat() < p) {
                i++;
            }
        }
        return i;
    }

    public static float map(float val, float instart, float inend, float outstart, float outend) {
        return ((val - instart) / (inend - instart)) * (outend - outstart) + outstart;
    }
}
