package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Integers are randomly generated and stored into an (expanding) array. How would you keep track of the median?
 *
 * A heap is really good at basic ordering and keeping track of max and mins. This is actually interesting – if you had
 * two heaps, you could keep track of the biggest half and the smallest half of the elements The biggest half is kept in
 * a min heap, such that the smallest element in the biggest half is at the root The smallest half is kept in a max
 * heap, such that the biggest element of the smallest half is at the root Now, with these data structures, you have the
 * potential median elements at the roots If the heaps are no longer the same size, you can quickly “rebalance” the
 * heaps by popping an element off the one heap and pushing it onto the other.
 */
public class StreamingMedian {

    List<Integer> numbers;
    PriorityQueue<Integer> lowIntegers;
    PriorityQueue<Integer> highIntegers;

    public StreamingMedian() {
        this.numbers = new ArrayList<>();
        this.lowIntegers = new PriorityQueue<>(Comparator.reverseOrder());
        this.highIntegers = new PriorityQueue<>();
    }

    public void addAll(Integer[] numbers) {
        for (Integer i : numbers) {
            add(i);
        }
    }

    public void add(Integer number) {
        numbers.add(number);
        if (lowIntegers.isEmpty()) {
            lowIntegers.add(number);
            return;
        }
        int lowMax = lowIntegers.peek();
        if (number > lowMax) {
            highIntegers.add(number);
        } else {
            lowIntegers.add(number);
        }
        int lowSize = lowIntegers.size();
        int highSize = highIntegers.size();
        if (Math.abs(lowSize - highSize) > 1) {
            if (lowSize > highSize) {
                highIntegers.add(lowIntegers.poll());
            } else {
                lowIntegers.add(highIntegers.poll());
            }
        }
    }

    public void clear() {
        lowIntegers.clear();
        highIntegers.clear();
        numbers.clear();
    }

    public Double getMedian() {
        if (highIntegers.isEmpty() && lowIntegers.isEmpty()) {
            return null;
        }
        if (numbers.size() % 2 == 0) {
            return (lowIntegers.peek() + highIntegers.peek()) / 2.0D;
        }
        if (lowIntegers.size() > highIntegers.size()) {
            return lowIntegers.peek().doubleValue();
        }
        return highIntegers.peek().doubleValue();
    }
}
