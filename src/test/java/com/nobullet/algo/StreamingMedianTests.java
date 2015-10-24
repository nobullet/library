package com.nobullet.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.junit.Test;

/**
 * Tests for streaming median.
 */
public class StreamingMedianTests {

    @Test
    public void test() {
        assertStreamingMedianIsCorrect(new Integer[]{5, 6, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        assertStreamingMedianIsCorrect(new Integer[]{5, 6, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        assertStreamingMedianIsCorrect(new Integer[]{5, 6, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12});
        assertStreamingMedianIsCorrect(new Integer[]{3, 2, 1});
        assertStreamingMedianIsCorrect(new Integer[]{3, 2});
        assertStreamingMedianIsCorrect(new Integer[]{3});
    }

    static void assertStreamingMedianIsCorrect(Integer[] data) {
        StreamingMedian sm = new StreamingMedian();
        sm.addAll(data);
        assertFalse("Median of 0 elements is not null.", data.length == 0 && sm.getMedian() != null);
        Arrays.sort(data);
        Double median;
        if (data.length > 0 && data.length % 2 == 0) {
            int index = data.length / 2; // 8 => 3 && 4
            median = (data[index].floatValue() + data[index - 1].doubleValue()) / 2.0D;
        } else {
            median = data[data.length / 2].doubleValue();
        }
        assertEquals(median, sm.getMedian(), 0.0001D);
    }
}
