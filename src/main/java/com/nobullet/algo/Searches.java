package com.nobullet.algo;

import java.util.Comparator;
import java.util.List;

/**
 * Searches.
 */
public final class Searches {

    /**
     * Binary search for the given element.
     *
     * @param <T>     Type of the element.
     * @param objects Objects to search in.
     * @param needle  Needle.
     * @return Index of the element or negative number reflecting the insertion point as in
     * {@link java.util.Collections#binarySearch(java.util.List, java.lang.Object, java.util.Comparator)}.
     */
    public static <T extends Comparable<? super T>> int binarySearch(List<T> objects, T needle) {
        return binarySearch(objects, needle, null, 0, objects.size());
    }

    /**
     * Binary search for the given element.
     *
     * @param <T>        Type of the element.
     * @param objects    Objects to search in.
     * @param needle     Needle.
     * @param comparator Comparator.
     * @return Index of the element or negative number reflecting the insertion point as in
     * {@link java.util.Collections#binarySearch(java.util.List, java.lang.Object, java.util.Comparator)}.
     */
    public static <T> int binarySearch(List<? extends T> objects, T needle, Comparator<? super T> comparator) {
        return binarySearch(objects, needle, comparator, 0, objects.size());
    }

    /**
     * Binary search for the given element.
     *
     * @param <T>        Type of the element.
     * @param objects    Objects to search in.
     * @param needle     Needle.
     * @param comparator Comparator.
     * @param fromIndex  From index.
     * @param toIndex    To index. Excluding.
     * @return Index of the element or negative number reflecting the insertion point as in
     * {@link java.util.Collections#binarySearch(java.util.List, java.lang.Object, java.util.Comparator)}.
     */
    public static <T> int binarySearch(List<? extends T> objects, T needle, Comparator<? super T> comparator,
                                       int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < fromIndex || toIndex > objects.size()) {
            throw new IllegalArgumentException("Expected 0 <= from <= to <= of a collection size.");
        }
        int lo = fromIndex;
        int hi = toIndex - 1; // hi and lo must be correct array (list) indices.
        int comparison;
        int mid = lo;
        T midValue;

        while (lo <= hi) {
            mid = lo + (hi - lo) / 2; // (a + b) >>> 1
            //midIndex = (toIndex + fromIndex) >>> 1;
            midValue = objects.get(mid);
            comparison = compare(comparator, needle, midValue);
            if (comparison == 0) {
                return mid;
            } else if (comparison < 0) {
                hi = mid - 1; // Already compared midIndex.
            } else {
                lo = mid + 1; // Already compared midIndex.
            }
        }
        // Insertion point.
        return -lo - 1;
    }

    /**
     * Counts the number of occurences of the needle in the given sorted list of comparables.
     *
     * @param objects Sorted objects list.
     * @param needle  Needle.
     * @param <T>     Type.
     * @return Number of occurences of the needle in the given sorted list.
     */
    public static <T extends Comparable<? super T>> int binaryCount(List<T> objects, T needle) {
        return binaryCount(objects, needle, null);
    }

    /**
     * Counts the number of occurences of the needle in the given sorted list.
     *
     * @param objects Sorted objects list.
     * @param needle  Needle.
     * @param comparator Comparator.
     * @param <T>     Type.
     * @return Number of occurences of the needle in the given sorted list.
     */
    public static <T> int binaryCount(List<? extends T> objects, T needle, Comparator<? super T> comparator) {
        int needleIndex = binarySearch(objects, needle, comparator);
        if (needleIndex < 0) {
            return 0;
        }

        int leftIndex = -1;
        int rightIndex = -1;

        int lo = needleIndex + 1;
        int hi = needleIndex;
        int temp;
        // Left part.
        do {
            temp = binarySearch(objects, needle, comparator, 0, hi);
            if (temp > 0) {
                hi = temp;
                leftIndex = temp;
            }
        } while (temp > 0);
        if (temp == 0) {
            leftIndex = 0;
        }
        // Right part.
        do {
            temp = binarySearch(objects, needle, comparator, lo, objects.size());
            if (temp > 0) {
                lo = temp + 1;
                rightIndex = temp;
            }
        } while (temp > 0);
        if (leftIndex < 0) {
            leftIndex = needleIndex;
        }

        if (rightIndex < 0) {
            rightIndex = needleIndex;
        }
        return rightIndex - leftIndex + 1;
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

    private Searches() {
    }
}
