package com.nobullet.algo;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Tests for {@link Sorts}.
 */
public class SortsTest {

    static final List<Integer> SOURCE = Lists.newArrayList(1, 34, 63, 3, 4, 7, 89, 5, 32, 2, 5, 789, 4, 2, 23, 45);

    @Test
    public void testMergeSort() {
        List<Integer> copy = Lists.newArrayList(SOURCE);
        List<Integer> custom = Lists.newArrayList(copy);
        Sorts.mergeSort(custom);
        Collections.sort(copy);
        assertListsEqual(copy, custom);

        copy = Lists.newArrayList(SOURCE);
        copy.add(-10);
        custom = Lists.newArrayList(copy);
        Sorts.mergeSort(custom);
        Collections.sort(copy);
        assertListsEqual(copy, custom);
    }

    @Test
    public void testQuickSort() {
        List<Integer> items = new ArrayList<>(SOURCE);
        List<Integer> copy = new ArrayList<>(items);
        Sorts.quickSort(items, (a, b) -> a.compareTo(b));
        Collections.sort(copy, (a, b) -> a.compareTo(b));
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort2() {
        List<Integer> items = new ArrayList<>(Arrays.asList(100, 3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        Sorts.quickSort(items, (a, b) -> a.compareTo(b));
        Collections.sort(copy, (a, b) -> a.compareTo(b));
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort_Reverse() {
        List<Integer> items = new ArrayList<>(Arrays.asList(100, 3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        Sorts.quickSort(items, (a, b) -> b.compareTo(a));
        Collections.sort(copy, (a, b) -> b.compareTo(a));
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort_Doubles() {
        List<Double> items = new ArrayList<>(Arrays.asList(Double.valueOf(100.0D), Double.valueOf(10.0D),
                Double.valueOf(100.0D), Double.valueOf(110.0D)));
        for (int i = -5; i < 10; i++) {
            items.add(Double.valueOf(1.0D * i * (i % 2 == 0 ? -1.0D : 1.0D)));
        }
        List<Double> copy = new ArrayList<>(items);
        Sorts.quickSort(items, (a, b) -> a.compareTo(b));
        Collections.sort(copy, (a, b) -> a.compareTo(b));
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort_Comparables() {
        List<Integer> items = new ArrayList<>(Arrays.asList(100, 3, 2, 1, 2, 4, 5, 6, 7, 1, 10, 9, 8));
        List<Integer> copy = new ArrayList<>(items);
        Sorts.quickSort(items);
        Collections.sort(copy);
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort_SameElements() {
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
        List<Integer> items = new ArrayList<>(Arrays.asList(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5));
        List<Integer> copy = new ArrayList<>(items);
        Sorts.quickSort(items, countingComparator);
        Collections.sort(copy, countingComparator2);
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort_almostSortedElements() {
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

        List<Integer> items = new ArrayList<>(Arrays.asList(6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7));
        List<Integer> copy = new ArrayList<>(items);
        Sorts.quickSort(items, countingComparator);
        Collections.sort(copy, countingComparator2);
        assertEquals(copy, items);
    }

    @Test
    public void testQuickSort_wellMixed() {
        AtomicInteger quickSortCounter = new AtomicInteger(0);
        Comparator<Integer> quickSortCountingComparator = (a, b) -> {
            quickSortCounter.incrementAndGet();
            return a.compareTo(b);
        };
        AtomicInteger collectionsSortCounter = new AtomicInteger(0);
        Comparator<Integer> collectionsSortCountingComparator = (a, b) -> {
            collectionsSortCounter.incrementAndGet();
            return a.compareTo(b);
        };
        AtomicInteger mergeSortCounter = new AtomicInteger(0);
        Comparator<Integer> mergeSortCountingComparator = (a, b) -> {
            mergeSortCounter.incrementAndGet();
            return a.compareTo(b);
        };

        List<Integer> quickSortData = new ArrayList<>(Arrays.asList(
                10, 5, 20, 15, 4, 1, 25, 13, 14, 2, 32, 23, 27, 7, 16, 12, 9, 8, 6, 21, -10, -5, 40, -1, 38, 42, 46));
        List<Integer> collectionsSortData = new ArrayList<>(quickSortData);
        List<Integer> mergeSortData = new ArrayList<>(quickSortData);

        Sorts.mergeSort(mergeSortData, mergeSortCountingComparator);
        Sorts.quickSort(quickSortData, quickSortCountingComparator);
        Collections.sort(collectionsSortData, collectionsSortCountingComparator);

        assertEquals(collectionsSortData, quickSortData);
        assertEquals(collectionsSortData, mergeSortData);

        assertTrue("Still worse than ComparableTimSort sort.",
                1.5 * collectionsSortCounter.get() <= quickSortCounter.get());
    }
}
