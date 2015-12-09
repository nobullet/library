package com.nobullet.interview;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * Tests for {@link KOrderStatistic}.
 */
public class KOrderStatisticTest {

    private static final int[] data = new int[]{105, 3, 9, 1, 5, -4, 8, 4, 300, 45, 87, 7, 99, 43, 21, 19, 8};

    @Test
    public void testKLargest() {
        int[] sorted = Arrays.copyOf(data, data.length);
        Arrays.sort(sorted);

        for (int i = 0; i < sorted.length; i++) {
            assertEquals(sorted[sorted.length - i - 1], KOrderStatistic.findKLargestElement(data, i + 1));
        }
    }

    @Test
    public void testKSmallest() {
        int[] arr = Arrays.copyOf(data, data.length);
        Arrays.sort(arr);

        for (int i = 0; i < arr.length; i++) {
            assertEquals(arr[i], KOrderStatistic.findKSmallestElement(data, i + 1));
        }
    }
}
