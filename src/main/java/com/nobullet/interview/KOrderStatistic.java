package com.nobullet.interview;

import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/**
 * Finds k-largest element.
 */
public final class KOrderStatistic {

    /**
     * Finds k-largest element from given array (k-th order statistic). Uses the heap to store k maximum elements and
     * solves it in O(N * log k) - worst time, k - temporary memory.
     *
     * @param data Data to search.
     * @param k k order.
     * @return k-th order statistic.
     */
    public static int findKLargestElement(int[] data, int k) {
        return findKLargestElement(data, k, null);
    }

    /**
     * Finds k-largest element from given array (k-th order statistic). Uses the heap to store k maximum elements and
     * solves it in O(N * log k) - worst time, k - temporary memory.
     *
     * @param data Data to search.
     * @param k k order.
     * @param consumer Consumer for max elements so far. Nullable.
     * @return k-th largest order statistic.
     */
    public static int findKLargestElement(int[] data, int k, Consumer<Integer> consumer) {
        PriorityQueue<Integer> heap = new PriorityQueue<>(k);
        for (int i = 0; i < k; i++) { // O(k * log k)
            heap.add(data[i]);
        }
        int peek = heap.peek();
        for (int i = k; i < data.length; i++) { // O((N - k) * log k)
            int element = data[i];
            if (element > peek) {
                if (consumer != null) {
                    consumer.accept(element);
                }
                heap.remove();
                heap.add(element);
                peek = heap.peek();
            }
        }
        return heap.peek();
    }

    /**
     * Finds k-smallest element from given array (k-th order statistic). Uses the heap to store k maximum elements and
     * solves it in O(N * log k) - worst time, k - temporary memory.
     *
     * @param data Data to search.
     * @param k k order.
     * @return k-th smallest order statistic.
     */
    public static int findKSmallestElement(int[] data, int k) {
        return findKSmallestElement(data, k, null);
    }

    /**
     * Finds k-smallest element from given array (k-th order statistic). Uses the heap to store k maximum elements and
     * solves it in O(N * log k) - worst time, k - temporary memory.
     *
     * @param data Data to search.
     * @param k k order.
     * @param consumer Consumer for min elements so far. Nullable.
     * @return k-th smallest order statistic.
     */
    public static int findKSmallestElement(int[] data, int k, Consumer<Integer> consumer) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(k, Collections.reverseOrder());
        for (int i = 0; i < k; i++) { // O(k * log k)
            maxHeap.add(data[i]);
        }
        int peek = maxHeap.peek();
        for (int i = k; i < data.length; i++) { // O((N - k) * log k)
            int element = data[i];
            if (element < peek) {
                if (consumer != null) {
                    consumer.accept(element);
                }
                maxHeap.remove();
                maxHeap.add(element);
                peek = maxHeap.peek();
            }
        }
        return maxHeap.peek();
    }

    private KOrderStatistic() {
    }
}
