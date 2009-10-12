package org.openmeetings.utils.math;
import java.util.*;

public class GenerateRandom {
    private static Random rn = new Random();
    public static int rand(int lo, int hi) {
            int n = hi - lo + 1;
            int i = rn.nextInt() % n;
            if (i < 0)
                    i = -i;
            return lo + i;
    }
    public static String randomstring(int lo, int hi) {
            int n = rand(lo, hi);
            byte b[] = new byte[n];
            for (int i = 0; i < n; i++)
                    b[i] = (byte)rand('a', 'z');
            return new String(b, 0);
    }
}
