package com.nobullet.algo;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for BST.
 */
public class BinarySearchTreeTest {

    List<Integer> numbers;
    List<Integer> numbersSmallerSet;
    BinarySearchTree<Integer> bst;

    @Before
    public void setUp() {
        numbersSmallerSet = Lists.newArrayList(10, 20, 15, 5, 7, 1, 25);
        numbers = Lists.newArrayList(10, 1, 19, 2, 18, 8, 4, 7, 3, 5, 6, 17, 11, 12, 16, 14, 13, 15);
        bst = new BinarySearchTree<>();
    }

    @Test
    public void testAddMinMaxInOrder() {
        bst.addAll(numbers);

        assertEquals(Integer.valueOf(1), bst.findMin());
        assertEquals(Integer.valueOf(19), bst.findMax());

        List<Integer> traverseResult = Lists.newArrayListWithCapacity(numbers.size());
        bst.inOrder((number, level) -> traverseResult.add(number));
        List<Integer> sorted = numbers.stream().sorted().collect(Collectors.toList());
        assertListsEqual(sorted, traverseResult);
    }

    @Test
    public void testDuplicates() {
        bst.addAll(numbers);
        bst.add(3).add(3);

        List<Integer> traverseResult = Lists.newArrayListWithCapacity(numbers.size());
        bst.inOrder((number, level) -> traverseResult.add(number));
        List<Integer> sorted = Stream.concat(Stream.of(3, 3), numbers.stream()).sorted().collect(Collectors.toList());
        assertListsEqual(sorted, traverseResult);
        assertEquals("Must contain 2 additional 3's.", numbers.size() + 2, traverseResult.size());

        bst.remove(3).remove(3);

        List<Integer> traverseResult2 = Lists.newArrayListWithCapacity(numbers.size());
        bst.inOrder((number, level) -> traverseResult2.add(number));
        sorted = numbers.stream().sorted().collect(Collectors.toList());
        assertListsEqual(sorted, traverseResult2);
        assertEquals("Must contain single 3.", numbers.size(), traverseResult2.size());
    }

    @Test
    public void testDelete() {
        bst.addAll(numbersSmallerSet);

        bst.remove(10);
        bst.remove(20);
        bst.remove(15);

        List<Integer> traverseResult = Lists.newArrayListWithCapacity(numbers.size());
        bst.inOrder((number, level) -> traverseResult.add(number));
        assertListsEqual(Lists.newArrayList(1, 5, 7, 25), traverseResult);
    }

    @Test
    public void testDeleteLargeStepByStep() {
        bst.addAll(numbers);

        List<Integer> copy = new ArrayList<>(numbers);
        Collections.sort(copy);
        for (Integer i : numbers) {
            bst.remove(i);
            copy.remove(i);
            Collections.sort(copy);

            List<Integer> traverseResult = Lists.newArrayListWithCapacity(numbers.size());
            bst.inOrder((number, level) -> traverseResult.add(number));
            assertListsEqual(copy, traverseResult);
        }
    }

    @Test
    public void testDeleteLarge() {
        bst.addAll(numbers);

        for (Integer number : numbers) {
            bst.remove(number);
        }
        List<Integer> traverseResult = Lists.newArrayListWithCapacity(numbers.size());
        bst.inOrder((number, level) -> traverseResult.add(number));
        assertListsEqual(Collections.emptyList(), traverseResult);
    }

    @Test
    public void testHasNoTwoLeafNodesDifferInDistanceByMoreThanOne() {
        assertTrue(bst.hasNoTwoLeafNodesDifferInDistanceByMoreThanOne());

        bst.addAll(numbers);
        assertFalse(bst.hasNoTwoLeafNodesDifferInDistanceByMoreThanOne());

        bst = new BinarySearchTree<>();
        bst.addAll(numbersSmallerSet);
        assertTrue(bst.hasNoTwoLeafNodesDifferInDistanceByMoreThanOne());

        bst = new BinarySearchTree<>();
        bst.addAll(Lists.newArrayList(2, 1, 3));
        assertTrue(bst.hasNoTwoLeafNodesDifferInDistanceByMoreThanOne());
    }

    @Test
    public void testIsBalanced() {
        assertTrue(bst.isBalanced());

        bst.addAll(numbers);
        assertFalse(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAll(numbersSmallerSet);
        assertTrue(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAll(Lists.newArrayList(2, 1, 3));
        assertTrue(bst.isBalanced());
    }

    @Test
    public void testCreateBalancedFromSortedList() {
        bst.addAll(numbers);
        assertFalse(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(Lists.newArrayList(1, 3));
        assertTrue(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(Lists.newArrayList(1, 2, 3));
        assertTrue(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(Lists.newArrayList(1, 2, 3, 4));
        assertTrue(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(Lists.newArrayList(1, 2, 3, 4, 5));
        assertTrue(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(Lists.newArrayList(1, 2, 2, 3, 3));
        assertTrue(bst.isBalanced());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(numbers.stream().sorted().collect(Collectors.toCollection(ArrayList::new)));
        assertTrue(bst.isBalanced());

        Logger.getLogger(this.getClass().getName()).info(bst.toString());

        bst = new BinarySearchTree<>();
        bst.addAllSorted(numbersSmallerSet.stream().sorted().collect(Collectors.toCollection(ArrayList::new)));
        assertTrue(bst.isBalanced());
    }
}
