package com.nobullet.algo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Searches.
 */
public final class Searches {

    /**
     * Binary search for the given element.
     *  
     * @param <T> Type of the element.
     * @param objects Objects to search in.
     * @param needle Needle.
     * @return Index of the element or negative number reflecting the insertion point as in
     * {@link java.util.Collections#binarySearch(java.util.List, java.lang.Object, java.util.Comparator)}.
     */
    public static <T> int binarySearch(List<? extends T> objects, T needle) {
        return binarySearch(objects, needle, Collections.reverseOrder().reversed(), 0, objects.size());
    }

    /**
     * Binary search for the given element.
     *
     * @param <T> Type of the element.
     * @param objects Objects to search in.
     * @param needle Needle.
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
     * @param <T> Type of the element.
     * @param objects Objects to search in.
     * @param needle Needle.
     * @param comparator Comparator.
     * @param fromIndex From index.
     * @param toIndex To index.
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
            comparison = comparator.compare(needle, midValue);
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

    private Searches() {
    }
}
