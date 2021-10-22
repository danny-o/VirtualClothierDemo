package com.digitalskies.virtualclothierdemo.utils;

import java.util.Collection;

public class ByteUtils {

    public static int[] toIntArray(Collection<Integer> values) {
        int[] array = new int[values.size()];
        int i = 0;
        for (int v : values) {
            array[i] = v;
            i++;
        }
        return array;
    }
}
