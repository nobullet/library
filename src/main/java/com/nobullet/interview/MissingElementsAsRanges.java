package com.nobullet.interview;

import java.util.function.BiConsumer;

/**
 * Find all missing elements from the array of integers that are in a given range X-Y. Process missing elements as
 * ranges.
 */
public class MissingElementsAsRanges {

    public static void find(int from, int to, int[] numbers, BiConsumer<Integer, Integer> rangesConsumer) {
        int numbersRangeSize = to - from;
        if (numbersRangeSize < 1) {
            throw new IllegalArgumentException("From must be less than to.");
        }
        int[] bucket = new int[numbersRangeSize];
        for (Integer number : numbers) {
            if (from <= number && number < to) {
                bucket[number - from]++;
            }
        }
        int rangeStart = from;
        int currentValue = -1;
        int i = 0;
        for (; i < bucket.length; i++) {
            if (bucket[i] > 0) {
                currentValue = from + i - 1;
                if (currentValue >= rangeStart) {
                    rangesConsumer.accept(rangeStart, currentValue);
                }
                rangeStart = from + i + 1; // Next after current.
            }
        }
        if (rangeStart < to) {
            rangesConsumer.accept(rangeStart, to);
        }
    }

    private MissingElementsAsRanges() {
    }
}
