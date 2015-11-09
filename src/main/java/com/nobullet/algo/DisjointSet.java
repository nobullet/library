package com.nobullet.algo;

import java.util.HashSet;
import java.util.Set;

/**
 * Disjoint set. For complexity see description of {@link #find(int)} method.
 */
public class DisjointSet {

    private int[] data;

    /**
     * Constructs disjoin set with given size.
     *
     * @param size Size of the disjoint set.
     */
    public DisjointSet(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be positive.");
        }
        this.data = new int[size];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = -1; // Default union size with '-'.
        }
    }

    /**
     * Returns union index for given item index. Worst case is O(log N) before the compression, O(1) amortized: for M
     * union and find operations it has O(M * alpha(M, N)) on average, where alpha(M, N) is the inverse Ackermann’s
     * function, that converges to constant. Therefore, M operations have O(M * alpha(M, N)) complexity, converges to
     * O(M).
     *
     * @param itemIndex Item index.
     * @return Union index for given item index
     */
    public int find(int itemIndex) {
        int parentUnionIndex = findParentUnionIndex(itemIndex);
        if (parentUnionIndex != itemIndex) {
            // Path compression.
            this.data[itemIndex] = parentUnionIndex;
        }
        return parentUnionIndex;
    }

    /**
     * Combines two unions into one with union-by-size approach. Worst case execution time is O(log N) and O(1)
     * amortized. See {@link #find(int)} description for details.
     *
     * @param itemOneIndex Item one index.
     * @param itemTwoIndex Item two index.
     * @return New union id (index).
     */
    public int union(int itemOneIndex, int itemTwoIndex) {
        // Join to lesser.
        if (itemOneIndex > itemTwoIndex) {
            int temp = itemOneIndex;
            itemOneIndex = itemTwoIndex;
            itemTwoIndex = temp;
        }
        int parentUnionIndex1 = findParentUnionIndex(itemOneIndex);
        int parentUnionIndex2 = findParentUnionIndex(itemTwoIndex);
        if (parentUnionIndex1 == parentUnionIndex2) {
            return parentUnionIndex1;
        }
        int union1Size = -this.data[parentUnionIndex1];
        int union2Size = -this.data[parentUnionIndex2];
        // Link to largest.
        if (union1Size >= union2Size) {
            // Link.
            this.data[itemTwoIndex] = parentUnionIndex1;
            this.data[parentUnionIndex2] = parentUnionIndex1;
            // Update size.
            this.data[parentUnionIndex1] -= union2Size;
            return parentUnionIndex1;
        }
        // Link.
        this.data[itemOneIndex] = parentUnionIndex2;
        this.data[parentUnionIndex1] = parentUnionIndex2;
        // Update size.
        this.data[parentUnionIndex2] -= union1Size;
        return parentUnionIndex2;
    }

    /**
     * Returns number of elements in current set in constant time.
     *
     * @return Number of elements in current set.
     */
    public int size() {
        return this.data.length;
    }

    /**
     * Returns union size for given item. Worst case execution time is O(log N) and O(1) on average. See
     * {@link #find(int)} description for details.
     *
     * @param itemIndex Item index.
     * @return Union size for item index.
     */
    public int getUnionSize(int itemIndex) {
        return -this.data[findParentUnionIndex(itemIndex)];
    }

    /**
     * Returns a set of indices for union members of the given item index. Worst case time is O(N log N), O(N * alpha(N)) on
     * average. See {@link #find(int)} description for details.
     *
     * @param itemIndex Item index.
     * @return Set of item indices.
     */
    public Set<Integer> getUnionMembers(int itemIndex) {
        int parentUnionIndex = findParentUnionIndex(itemIndex);
        int unionSize = -this.data[parentUnionIndex];
        Set<Integer> unionMembers = new HashSet<>(unionSize);
        for (int i = data.length - 1; i >= 0; i--) {
            if (parentUnionIndex == findParentUnionIndex(i)) {
                unionMembers.add(i);
            }
        }
        return unionMembers;
    }

    /**
     * Returns number of unions in the current set in O(N).
     *
     * @return Number of unions in the current set in O(N).
     */
    public int getNumberOfUnions() {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                result++;
            }
        }
        return result;
    }

    /**
     * Finds parent union index for the given item index. Parent union index always points to size of the union,
     * represented by negative number. Compression performed by {@link #find(int)}. O(log N).
     *
     * @param itemIndex Item index.
     * @return Parent union index.
     */
    protected int findParentUnionIndex(int itemIndex) {
        checkIndex(itemIndex);
        int temp;
        int parentUnionIndex = itemIndex;
        while ((temp = this.data[parentUnionIndex]) >= 0) {
            parentUnionIndex = temp;
        }
        return parentUnionIndex;
    }

    /**
     * Checks the item index for validity.
     *
     * @param itemIndex Item index.
     */
    protected void checkIndex(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= this.data.length) {
            throw new IllegalStateException(String.format("Illegal item index: %d. Expected >= 0 and < %d",
                    itemIndex, this.data.length));
        }
    }
}
