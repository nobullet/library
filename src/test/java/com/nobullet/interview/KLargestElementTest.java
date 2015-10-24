package com.nobullet.interview;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;

/**
 * Tests for {@link KLargestElement}.
 */
public class KLargestElementTest {

    @Test
    public void testKLargest() {
        int[] data = new int[]{105, 3, 9, 1, 5, -4, 8, 4, 300, 45, 87, 7, 99, 43, 21, 19, 8};
        int[] sorted = Arrays.copyOf(data, data.length);
        Arrays.sort(sorted);
        
        for (int i = 0; i < sorted.length; i++) {
            assertEquals(sorted[sorted.length - i - 1], KLargestElement.findKLargestElementInONlogK(data, i + 1));
        }
    }
}
