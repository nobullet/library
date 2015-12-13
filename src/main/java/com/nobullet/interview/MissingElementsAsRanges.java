package com.nobullet.interview;

import java.util.function.BiConsumer;

/**
 * Find all missing elements from the array of integers that are in a given range X-Y. Process missing elements as
 * ranges. <p>
 * (0, 99, new int[]{88, 105, 3, 2, 4, 200, 0, 10} -> "1,5-9,11-87,89-99"
 * </p>
 */
public class MissingElementsAsRanges {

    /**
     * Solves the missing elements problem.
     *
     * @param from           Range from, including.
     * @param to             Range to, excluding.
     * @param numbers        Numbers.
     * @param rangesConsumer Ranges consumer. Parameters may be the same, indicating the number.
     */
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
