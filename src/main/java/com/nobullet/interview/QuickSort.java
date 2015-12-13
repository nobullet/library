package com.nobullet.interview;

import java.util.Comparator;
import java.util.List;

/**
 * Implements quick sort.
 */
public final class QuickSort {

    /**
     * Sorts given collection of comparables in ascending order.
     *
     * @param items Items to sort.
     * @param <T>   Type.
     */
    public static <T extends Comparable<? super T>> void sort(List<T> items) {
        if (items.size() < 2) {
            return;
        }
        sort(items, null, 0, items.size() - 1);
    }

    /**
     * Sorts given collection of elements with comparator.
     *
     * @param items      Items.
     * @param comparator Comparator.
     * @param <T>        Type.
     */
    public static <T> void sort(List<T> items, Comparator<T> comparator) {
        if (items.size() < 2) {
            return;
        }
        sort(items, comparator, 0, items.size() - 1);
    }

    /**
     * Sorts given collection of elements with comparator.
     *
     * @param items      Items.
     * @param comparator Comparator.
     * @param lo         Low index, including.
     * @param hi         Hi index, including.
     * @param <T>        Type.
     */
    static <T> void sort(List<T> items, Comparator<T> comparator, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int pivotIndex = pivotIndex(items, comparator, lo, hi);
        long indices = partition(items, comparator, pivotIndex, lo, hi);
        int leftIndex = (int) (indices >> 32);
        int rightIndex = (int) indices;
        if (lo < leftIndex) {
            sort(items, comparator, lo, leftIndex);
        }
        if (rightIndex < hi) {
            sort(items, comparator, rightIndex, hi);
        }
    }

    /**
     * Partitions elements of the list around the index.
     *
     * @param items      Items.
     * @param comparator Comparator. If null, tries to cast to comparable.
     * @param pivotIndex Pivot index.
     * @param lo         Low value.
     * @param hi         High value.
     * @param <T>        Type.
     * @return Long value, containing two indices: index to the left of the pivot(s) and index to the right
     * of the pivot(s).
     */
    static <T> long partition(List<T> items, Comparator<T> comparator, int pivotIndex, int lo, int hi) {
        T pivot = items.get(pivotIndex);
        int groupPivotIndex = groupPivots(items, comparator, pivotIndex, lo, hi);
        int leftIndex = groupPivotIndex;
        int rightIndex = hi;
        if (leftIndex > hi) { // All elements are pivots.
            return (((long) lo) << 32) | (hi & 0xffffffffL);
        }
        // Swap elements.
        do {
            // 1. Find element at li > p
            while (leftIndex <= rightIndex && compare(comparator, items.get(leftIndex), pivot) < 0) {
                leftIndex++;
            }
            // 2. Find element at ri < p
            while (leftIndex <= rightIndex && compare(comparator, items.get(rightIndex), pivot) > 0) {
                rightIndex--;
            }
            if (leftIndex <= rightIndex) {
                swap(items, leftIndex, rightIndex);
            }
        } while (leftIndex <= rightIndex);
        // leftIndex + 1 is the index of the first element > pivot.
        // Place pivots to the right place.
        groupPivotIndex--;
        leftIndex--;
        while (groupPivotIndex >= lo && groupPivotIndex != leftIndex) {
            swap(items, groupPivotIndex, leftIndex);
            leftIndex--;
            groupPivotIndex--;
        }
        if (leftIndex == rightIndex && rightIndex < hi) {
            rightIndex++;
        }
        return (((long) leftIndex) << 32) | (rightIndex & 0xffffffffL);
    }

    /**
     * Groups pivots in the beginning of the sub list.
     *
     * @param items      Items.
     * @param comparator Comparator. If null, tries to cast to comparable.
     * @param pivotIndex Pivot index.
     * @param lo         Low index.
     * @param hi         High index.
     * @param <T>        Type.
     * @return Index of the first element not equal to pivot (after group).
     */
    static <T> int groupPivots(List<T> items, Comparator<T> comparator, int pivotIndex, int lo, int hi) {
        T pivot = items.get(pivotIndex);

        int leftIndex = lo;
        // Find the first non–pivot element.
        while (leftIndex <= hi && compare(comparator, pivot, items.get(leftIndex)) == 0) {
            leftIndex++;
        }

        int rightIndex = hi;
        while (leftIndex <= rightIndex) {
            if (compare(comparator, pivot, items.get(rightIndex)) == 0) {
                swap(items, leftIndex, rightIndex);
                leftIndex++;
            }
            rightIndex--;
        }
        return leftIndex; // Points to the first element != pivot.
    }

    /**
     * Swaps two elements in list.
     *
     * @param items Items.
     * @param i     Index 1.
     * @param j     Index 2.
     * @param <T>   Type.
     */
    static <T> void swap(List<T> items, int i, int j) {
        if (i != j) {
            T temp = items.get(i);
            items.set(i, items.get(j));
            items.set(j, temp);
        }
    }

    /**
     * Chooses a pivot using median-of-three rule.
     *
     * @param items      Items.
     * @param comparator Comparator. If null, tries to cast to comparable.
     * @param loIndex    Low index.
     * @param hiIndex    High index.
     * @param <T>        Type.
     * @return Pivot index.
     */
    static <T> int pivotIndex(List<T> items, Comparator<T> comparator, int loIndex, int hiIndex) {
        T loValue = items.get(loIndex);

        int medianIndex = loIndex + (hiIndex - loIndex) / 2;
        T medianValue = items.get(medianIndex);

        T hiValue = items.get(hiIndex);

        T tTemp;
        int temp;

        if (compare(comparator, loValue, hiValue) > 0) { // hi > lo
            temp = loIndex;
            tTemp = hiValue;

            loIndex = hiIndex;
            loValue = hiValue;

            hiIndex = temp;
            hiValue = tTemp;
        }
        // hi <= lo at this point.

        if (compare(comparator, medianValue, loValue) < 0) {
            return loIndex;
        }
        if (compare(comparator, hiValue, medianValue) > 0) {
            return medianIndex;
        }
        return hiIndex;
    }

    /**
     * Compares two given objects as comparables if there is no comparator.
     *
     * @param comparator Comparator. If null, tries to cast to comparable.
     * @param o1         Object 1.
     * @param o2         Object 2.
     * @return Result of comparison.
     */
    @SuppressWarnings("unchecked")
    static <T> int compare(Comparator<T> comparator, T o1, T o2) {
        if (comparator != null) {
            return comparator.compare(o1, o2);
        }
        Comparable c1 = (Comparable) o1;
        Comparable c2 = (Comparable) o2;
        return c1.compareTo(c2);
    }

    private QuickSort() {
    }
}
