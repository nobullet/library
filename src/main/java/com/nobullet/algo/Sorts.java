package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts.
 */
public final class Sorts {

    /**
     * Performs merge sort of the given list. Uses
     *
     * @param <T> Type of the element.
     * @param list List to sort.
     */
    public static <T> void mergeSort(List<T> list) {
        mergeSort(list, Collections.reverseOrder().reversed());
    }

    /**
     * Performs merge sort of the given list.
     *
     * @param <T> Type of the element.
     * @param list List to sort.
     * @param comparator Comparator for the elements.
     */
    public static <T> void mergeSort(List<T> list, Comparator<? super T> comparator) {
        List<T> temp = new ArrayList<>(list.size());
        while (temp.size() < list.size()) {
            temp.add(null);
        }
        mergeSort(list, temp, comparator, 0, list.size() - 1);
    }

    static <T> void mergeSort(List<T> src, List<T> temp, Comparator<? super T> comparator, int from, int to) {
        if (from >= to) {
            return;
        }
        int mid = (from + to) >>> 1;
        mergeSort(src, temp, comparator, from, mid);
        mergeSort(src, temp, comparator, mid + 1, to);
        merge(src, temp, comparator, from, mid + 1, to);
    }

    static <T> void merge(List<T> src, List<T> temp, Comparator<? super T> comparator, int leftPosition,
            int rightPosition, int rightEnd) {
        int leftEnd = rightPosition - 1;
        int tempPosition = leftPosition;
        int numberOfElements = rightEnd - leftPosition + 1;

        T left, right;
        // Main loop.
        while (leftPosition <= leftEnd && rightPosition <= rightEnd) {
            left = src.get(leftPosition);
            right = src.get(rightPosition);
            if (comparator.compare(left, right) <= 0) {
                temp.set(tempPosition, left);
                leftPosition++;
            } else {
                temp.set(tempPosition, right);
                rightPosition++;
            }
            tempPosition++;
        }
        // Leftovers: left.
        while (leftPosition <= leftEnd) {
            temp.set(tempPosition++, src.get(leftPosition++));
        }
        // Leftovers: right.
        while (rightPosition <= rightEnd) {
            temp.set(tempPosition++, src.get(rightPosition++));
        }
        // Copy from temp back.
        for (int i = 0; i < numberOfElements; i++, rightEnd--) {
            src.set(rightEnd, temp.get(rightEnd));
        }
    }

    private Sorts() {
    }
}
