package com.nobullet.stack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Stack tests.
 *
 * @author nikuliak
 */
public class StackTest {

    static final Logger logger = Logger.getLogger(StackTest.class.getName());
    static final Integer[] EMPTY = new Integer[0];
    static final Integer[] TEST_DATA = new Integer[]{1, 2, 3, 4, 5, 10, 5, 4, 3, 2, 1, 0};
    static final Integer[] TEST_DATA_SORTED = Arrays.copyOf(TEST_DATA, TEST_DATA.length);

    static {
        Arrays.sort(TEST_DATA_SORTED);
    }

    static <T> LinkedList<T> drainAsReversedLinkedList(Stack<T> sti) {
        LinkedList<T> popped = new LinkedList<>();
        while (!sti.isEmpty()) {
            popped.push(sti.pop());
        }
        return popped;
    }

    static <T> LinkedList<T> drainAsLinkedList(Stack<T> sti) {
        LinkedList<T> popped = new LinkedList<>();
        while (!sti.isEmpty()) {
            popped.add(sti.pop());
        }
        return popped;
    }

    @Test
    public void testStackOperations() {
        Stack<Integer> sti = new Stack<>();
        int size = 0;
        Assert.assertEquals("Size should be the same.", size, sti.size());
        for (int number : TEST_DATA) {
            sti.push(number);
            size++;
            Assert.assertEquals("Size should be the same.", size, sti.size());
        }
        Assert.assertArrayEquals("Arrays should be equal after simple test.",
                TEST_DATA, drainAsReversedLinkedList(sti).toArray(EMPTY));

        size = 0;
        Assert.assertEquals("Size should be the same.", size, sti.size());
        for (int number : TEST_DATA) {
            sti.push(number);
            size++;
            Assert.assertEquals("Size should be the same.", size, sti.size());
        }

        Stack<Integer> copy = new Stack<>();
        Stack.drain(sti, copy);
        Assert.assertEquals("Size should be the same.", size, copy.size());
        Assert.assertArrayEquals("Arrays should be equal after stack drain.",
                TEST_DATA, drainAsLinkedList(copy).toArray(EMPTY));
    }

    @Test
    public void testNaturalOrderSort() {
        SortableStack<Integer> sti = new SortableStack<>();
        for (int number : TEST_DATA) {
            sti.push(number);
        }
        sti.sort();
        Assert.assertArrayEquals(TEST_DATA_SORTED, drainAsLinkedList(sti).toArray(EMPTY));
    }

    @Test
    public void testReverseNaturalOrderSort() {
        SortableStack<Integer> sti = new SortableStack<>();
        for (int number : TEST_DATA) {
            sti.push(number);
        }
        sti.sort(Comparator.<Integer>reverseOrder());
        Assert.assertArrayEquals(TEST_DATA_SORTED, drainAsReversedLinkedList(sti).toArray(EMPTY));
    }
}
