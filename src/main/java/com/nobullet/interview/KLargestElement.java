package com.nobullet.interview;

import java.util.PriorityQueue;

/**
 * Finds k-largest element.
 */
public final class KLargestElement {

    /**
     * Finds k-largest element from given array (k-th order statistic). Uses the heap to store k maximum elements and
     * solves it in O(N * log k) - worst time, k - temporary memory.
     *
     * @param data Data to search.
     * @param k k order.
     * @return k-th order statistic.
     */
    public static int findKLargestElementInONlogK(int[] data, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>(k);
        for (int i = 0; i < k; i++) { // O(k * log k)
            heap.add(data[i]);
        }
        int peek = heap.peek();
        for (int i = k; i < data.length; i++) { // O((N - k) * log k)
            int element = data[i];
            if (element > peek) {
                heap.add(data[i]);
                heap.remove();
                peek = heap.peek();
            }
        }
        return heap.peek();
    }

    private KLargestElement() {
    }
}
