package com.nobullet.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for {@link Searches}.
 */
public class SearchesTest {

    private static final List<Integer> SORTED_INTEGERS = Collections.unmodifiableList(
            Lists.newArrayList(1, 2, 3, 3, 33, 44, 55, 666, 777, 8888, 9999, 10000, 10100, 10200, 10300, 11111, 11111,
                    20000, 20001, 20002, 20003, 50000, 50000, 50000, 50001));

    @Test
    public void testBinarySearch() {
        List<Integer> longer = new ArrayList<>(SORTED_INTEGERS.size() + 1);
        longer.add(0);
        longer.addAll(SORTED_INTEGERS);
        compareBinarySearches(longer);
        compareBinarySearches(SORTED_INTEGERS);
    }

    @Test
    public void testBinarySearch_insertionPoint() {
        compareBinarySearch(SORTED_INTEGERS, 10101);
        compareBinarySearch(SORTED_INTEGERS, 555);
        compareBinarySearch(SORTED_INTEGERS, 667);
        compareBinarySearch(SORTED_INTEGERS, SORTED_INTEGERS.get(0) - 1);
        compareBinarySearch(SORTED_INTEGERS, SORTED_INTEGERS.get(SORTED_INTEGERS.size() - 1) + 1);
    }

    @Test
    public void testBinaryCount() {
        List<Integer> numbers = new ArrayList<>(SORTED_INTEGERS.size() + 2);
        numbers.add(1);
        numbers.addAll(SORTED_INTEGERS);
        numbers.add(50001);
        assertEquals(1, Searches.binaryCount(numbers, 8888));
        assertEquals(3, Searches.binaryCount(numbers, 50000));
        assertEquals(2, Searches.binaryCount(numbers, 3));
        assertEquals(2, Searches.binaryCount(numbers, 1));
        assertEquals(2, Searches.binaryCount(numbers, 50001));
    }

    @Test
    public void testBinaryCount_allSame() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(10);
        }
        assertEquals(10, Searches.binaryCount(numbers, 10));
        numbers.add(10);
        assertEquals(11, Searches.binaryCount(numbers, 10));
    }

    @Test
    @Ignore
    public void testShifted() {
        for (int i = 1; i <= SORTED_INTEGERS.size(); i++) {
            List<Integer> sortedShifted = shiftRight(SORTED_INTEGERS, i);
            int index = Searches.binarySearch(sortedShifted, 777);
            if (index < 0) {
                fail("Expected positive index for " + 777 + ". Got: " + index + " shift:" + i);
            }
            assertEquals(Integer.valueOf(777), sortedShifted.get(index));
        }
    }

    private static void compareBinarySearches(List<Integer> ints) {
        for (int i = 0; i < ints.size(); i++) {
            Integer integer = ints.get(i);
            compareBinarySearch(ints, integer);
        }
    }

    private static void compareBinarySearch(List<Integer> ints, Integer integer) {
        int collectionsBs = Collections.binarySearch(ints, integer);
        int customImplBs = Searches.binarySearch(ints, integer);
        String message = String.format("Search for %d must give same results: %d vs %d provided.",
                integer, collectionsBs, customImplBs);
        if (collectionsBs >= 0 && customImplBs >= 0) {
            assertEquals(message, ints.get(collectionsBs), ints.get(customImplBs));
        } else if (collectionsBs < 0 && customImplBs < 0) {
            collectionsBs += 1;
            collectionsBs *= -1;
            customImplBs += 1;
            customImplBs *= -1;
            if (customImplBs < ints.size() && collectionsBs < ints.size()) {
                message = String.format("Search for %d must give same insertion points: %d vs %d provided.",
                        integer, collectionsBs, customImplBs);
                assertEquals(message, ints.get(collectionsBs), ints.get(customImplBs));
            } else {
                assertEquals("Insertion point after all elements:", collectionsBs, customImplBs);
            }

        } else {
            fail(message + " Expected same insertion points.");
        }
    }

    private static <T> List<T> shiftRight(List<T> elements, int times) {
        times = times % elements.size();
        int pos = times;
        List<T> result = new ArrayList<>(elements.size());
        while (result.size() < elements.size()) {
            result.add(null);
        }
        for (T element : elements) {
            result.set(pos++, element);
            if (pos == elements.size()) {
                pos = 0;
            }
        }
        return result;
    }
}
