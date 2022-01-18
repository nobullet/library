package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoNumberSum {

    public static void main(String... args) {
        int[] expected = {-1, 11};
        int[] output = TwoNumberSum.twoNumberSum(new int[] {3, 5, -4, 8, 11, 1, -1, 6}, 10);
        System.out.println(Arrays.asList(output));
    }

    public static int[] twoNumberSum(int[] array, int targetSum) {
        IntegerHistogram histogram = new IntegerHistogram(array);

        List<Integer> pairs = new ArrayList<>();
        for (int number1 : array) {
            int number2 = targetSum - number1;
            int count1 = histogram.get(number1);
            int count2 = histogram.get(number2);
            if (count1 > 0 && count2 > 0) {
                if (number1 == number2 && count1 == 1) {
                    continue;
                }
                pairs.add(number1);
                pairs.add(number2);
                histogram.dec(number1);
                histogram.dec(number2);
            }
        }
        int[] result = new int[pairs.size()];
        int index = 0;
        for (Integer element : pairs) {
            result[index++] = element;
        }
        return result;
    }

    private static final class IntegerHistogram {
        private final Map<Integer, Integer> values;

        public IntegerHistogram() {
            this(new int[0]);
        }

        public IntegerHistogram(int[] initial) {
            values = new HashMap<>();
            for (int value : initial) {
                inc(value);
            }
        }

        public int add(Integer key, int value) {
            int newValue = Math.addExact(get(key), value);
            values.put(key, newValue);
            return newValue;
        }

        public int inc(Integer key) {
            return add(key, 1);
        }

        public int subtract(Integer key, int value) {
            int newValue = Math.subtractExact(get(key), value);
            values.put(key, newValue);
            return newValue;
        }

        public int dec(Integer key) {
            return subtract(key, 1);
        }

        public int get(Integer key) {
            Integer existing = values.get(key);
            return existing != null ? existing.intValue() : 0;
        }

        public Collection<Integer> values() {
            return values.values();
        }
    }
}
