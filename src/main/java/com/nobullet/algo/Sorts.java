package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts: merge and quick sort.
 */
public final class Sorts {

    private static final int QUICK_SORT_INSERTION_SORT_THRESHOLD = 8;

    /**
     * Performs merge quickSort of the given list. Uses
     *
     * @param <T>  Type of the element.
     * @param list List to quickSort.
     */
    public static <T> void mergeSort(List<T> list) {
        mergeSort(list, null);
    }

    /**
     * Performs merge quickSort of the given list.
     *
     * @param <T>        Type of the element.
     * @param list       List to quickSort.
     * @param comparator Comparator for the elements.
     */
    public static <T> void mergeSort(List<T> list, Comparator<? super T> comparator) {
        List<T> temp = new ArrayList<>(list.size());
        while (temp.size() < list.size()) {
            temp.add(null);
        }
        mergeSort(list, temp, comparator, 0, list.size() - 1);
    }

    /**
     * Merge quickSort.
     *
     * @param src        Source.
     * @param temp       Temporary array.
     * @param comparator Comparator.
     * @param from       From index. Including.
     * @param to         To index. Including.
     * @param <T>        Type.
     */
    static <T> void mergeSort(List<T> src, List<T> temp, Comparator<? super T> comparator, int from, int to) {
        if (from >= to) {
            return;
        }
        int mid = (from + to) >>> 1;
        mergeSort(src, temp, comparator, from, mid);
        mergeSort(src, temp, comparator, mid + 1, to);
        merge(src, temp, comparator, from, mid + 1, to);
    }

    /**
     * Merge stage.
     *
     * @param src           Source.
     * @param temp          Temporary array.
     * @param comparator    Comparator.
     * @param leftPosition  Left position.
     * @param rightPosition Right position.
     * @param rightEnd      Right end  (including).
     * @param <T>           Type.
     */
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
            if (compare(comparator, left, right) <= 0) {
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

    /**
     * Sorts given collection of comparables in ascending order. Implements Quick quickSort with median-of-three
     * pivot and 'fat partition': partition separates the values into three
     * groups: values less than the pivot, values equal to the pivot, and values greater than the pivot. Partition
     * procedure returns two indices: one before the pivot(s), the second is after the pivot(s). It helps to finish
     * sorting of list of the same elements in linear time.
     *
     * @param items Items to quickSort.
     * @param <T>   Type.
     */
    public static <T extends Comparable<? super T>> void quickSort(List<T> items) {
        if (items.size() < 2) {
            return;
        }
        quickSort(items, null, 0, items.size() - 1);
    }

    /**
     * Sorts given collection of elements with comparator. Implements Quick quickSort with median-of-three
     * pivot and 'fat partition': partition separates the values into three
     * groups: values less than the pivot, values equal to the pivot, and values greater than the pivot. Partition
     * procedure returns two indices: one before the pivot(s), the second is after the pivot(s). It helps to finish
     * sorting of list of the same elements in linear time.
     *
     * @param items      Items.
     * @param comparator Comparator.
     * @param <T>        Type.
     */
    public static <T> void quickSort(List<T> items, Comparator<T> comparator) {
        if (items.size() < 2) {
            return;
        }
        quickSort(items, comparator, 0, items.size() - 1);
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
    static <T> void quickSort(List<T> items, Comparator<T> comparator, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        // For smaller sorts insertion sort to speed up.
        if (QUICK_SORT_INSERTION_SORT_THRESHOLD > 1 && hi - lo <= QUICK_SORT_INSERTION_SORT_THRESHOLD) {
            for (int i = lo; i <= hi; i++) {
                boolean swapped = false;
                for (int j = hi - 1; j >= i; j--) {
                    if (compare(comparator, items.get(j), items.get(j + 1)) > 0) {
                        swapped = true;
                        swap(items, j, j + 1);
                    }
                }
                if (!swapped) {
                    break;
                }
            }
            return;
        }
        long indices = partition(items, comparator, lo, hi);
        int leftIndex = (int) (indices >> 32);
        int rightIndex = (int) indices;
        if (lo < leftIndex) {
            quickSort(items, comparator, lo, leftIndex);
        }
        if (rightIndex < hi) {
            quickSort(items, comparator, rightIndex, hi);
        }
    }

    /**
     * Partitions elements of the list around the index.
     *
     * @param items      Items.
     * @param comparator Comparator. If null, tries to cast to comparable.
     * @param lo         Low value.
     * @param hi         High value.
     * @param <T>        Type.
     * @return Long value, containing two indices: index to the left of the pivot(s) and index to the right
     * of the pivot(s).
     */
    static <T> long partition(List<T> items, Comparator<T> comparator, int lo, int hi) {
        int pivotIndex = pivotIndex(items, comparator, lo, hi);
        T pivot = items.get(pivotIndex);
        int leftIndex = lo + 1; // See how pivot is chosen.
        int rightIndex = hi - 1; // See how pivot is chosen.

        while (leftIndex <= rightIndex) {
            while (compare(comparator, items.get(leftIndex), pivot) < 0) {
                leftIndex++;
            }
            while (compare(comparator, items.get(rightIndex), pivot) > 0) {
                rightIndex--;
            }
            if (leftIndex <= rightIndex) {
                swap(items, leftIndex, rightIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return (((long) rightIndex) << 32) | (leftIndex & 0xffffffffL);
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
        int medianIndex = loIndex + (hiIndex - loIndex) / 2;

        T loValue = items.get(loIndex);
        T medianValue = items.get(medianIndex);
        T hiValue = items.get(hiIndex);

        if (compare(comparator, loValue, hiValue) > 0) { // hi > lo
            swap(items, loIndex, hiIndex);
            loValue = items.get(loIndex);
            hiValue = items.get(hiIndex);
        }
        // hi <= lo at this point.
        // if med < lo...
        if (compare(comparator, medianValue, loValue) < 0) {
            swap(items, loIndex, medianIndex);
            medianValue = items.get(medianIndex);
        }
        // if med > hi...
        if (compare(comparator, medianValue, hiValue) > 0) {
            swap(items, hiIndex, medianIndex);
        }
        return medianIndex;
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

    private Sorts() {
    }
}
