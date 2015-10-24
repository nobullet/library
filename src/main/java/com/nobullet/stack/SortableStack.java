package com.nobullet.stack;

import java.util.Comparator;

/**
 * Sortable stack.
 *
 * @param <T> Data type.
 */
public class SortableStack<T extends Comparable<? super T>> extends Stack<T> {

    /**
     * Sorts stack using push(), pop(), peek() only. O(N^2)
     */
    public void sort() {
        sort(Comparator.naturalOrder());
    }

    /**
     * Sort stack using push(), pop(), peek() only. O(N^2)
     *
     * @param comparator Comparator to use.
     */
    public void sort(Comparator<T> comparator) {
        if (size() < 2) {
            return;
        }
        Stack<T> buffer = new Stack<>();

        // Check if all elements in buffer are sorted (it means all elements are in buffer).
        do {
            while (buffer.isEmpty() || (!isEmpty() && comparator.compare(buffer.peek(), peek()) <= 0)) {
                buffer.push(pop());
            }
            // If original has no elements - stop, all elements are in sorted order in buffer.
            if (isEmpty()) {
                break;
            }
            // Now buffer.peek() contains the first element that has bad order with original peek().
            T elementFromOriginal = pop();
            T elementFromBuffer = buffer.pop();
            push(elementFromBuffer); // Push two elements in correct order.
            push(elementFromOriginal);
            // Drain buffer back to original and start all over.
            Stack.drain(buffer, this);
        } while(true);
        // Drain buffer back to original and stop.
        Stack.drain(buffer, this);
    }
}
