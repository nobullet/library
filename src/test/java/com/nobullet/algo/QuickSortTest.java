package com.nobullet.algo;

import com.nobullet.algo.QuickSort;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link QuickSort}.
 */
public class QuickSortTest {

    @Test
    public void test() {
        List<Integer> items = new ArrayList<>(Arrays.asList(3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        QuickSort.sort(items, (a, b) -> a.compareTo(b));
        Collections.sort(copy, (a, b) -> a.compareTo(b));
        assertEquals(copy, items);
    }

    @Test
    public void test2() {
        List<Integer> items = new ArrayList<>(Arrays.asList(100, 3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        QuickSort.sort(items, (a, b) -> a.compareTo(b));
        Collections.sort(copy, (a, b) -> a.compareTo(b));
        assertEquals(copy, items);
    }

    @Test
    public void testReverse() {
        List<Integer> items = new ArrayList<>(Arrays.asList(100, 3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        QuickSort.sort(items, (a, b) -> b.compareTo(a));
        Collections.sort(copy, (a, b) -> b.compareTo(a));
        assertEquals(copy, items);
    }

    @Test
    public void testDoubles() {
        List<Double> items = new ArrayList<>(Arrays.asList(Double.valueOf(100.0D), Double.valueOf(10.0D),
                Double.valueOf(100.0D), Double.valueOf(110.0D)));
        for (int i = -5; i < 10; i++) {
            items.add(Double.valueOf(1.0D * i * (i % 2 == 0 ? -1.0D : 1.0D)));
        }
        List<Double> copy = new ArrayList<>(items);
        QuickSort.sort(items, (a, b) -> a.compareTo(b));
        Collections.sort(copy, (a, b) -> a.compareTo(b));
        assertEquals(copy, items);
    }

    @Test
    public void testComparables() {
        List<Integer> items = new ArrayList<>(Arrays.asList(100, 3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        QuickSort.sort(items);
        Collections.sort(copy);
        assertEquals(copy, items);
    }

    @Test
    public void testSameElements() {
        AtomicInteger counter = new AtomicInteger(0);
        Comparator<Integer> countingComparator = (a, b) -> {
            counter.incrementAndGet();
            return a.compareTo(b);
        };
        List<Integer> items = new ArrayList<>(Arrays.asList(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5));
        List<Integer> copy = new ArrayList<>(items);
        QuickSort.sort(items, countingComparator);
        Collections.sort(copy);
        assertEquals(copy, items);

        String message = String.format("No more than %d (size: %d) comparisons for same elements case.",
                3 + items.size(), items.size());
        assertEquals(message, items.size() + 3, counter.get());
    }

    @Test
    public void testAlmostSortedElements() {
        AtomicInteger counter = new AtomicInteger(0);
        Comparator<Integer> countingComparator = (a, b) -> {
            counter.incrementAndGet();
            return a.compareTo(b);
        };

        AtomicInteger counter2 = new AtomicInteger(0);
        Comparator<Integer> countingComparator2 = (a, b) -> {
            counter2.incrementAndGet();
            return a.compareTo(b);
        };

        List<Integer> items = new ArrayList<>(Arrays.asList(6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6));
        List<Integer> copy = new ArrayList<>(items);
        QuickSort.sort(items, countingComparator);
        Collections.sort(copy, countingComparator2);
        assertEquals(copy, items);

        assertTrue("Still worse than ComparableTimSort sort.", counter2.get() < counter.get());
    }
}
