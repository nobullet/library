package com.nobullet.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Disjoint set for objects.
 */
public class ObjectDisjointSet<T> {

    private Map<T, Integer> objectToIndexMapping;
    private Object[] indexToObjectMapping;
    private DisjointSet disjointSet;

    /**
     * Constructs disjoint set from given set of objects.
     *
     * @param objects Object to construct from.
     */
    public ObjectDisjointSet(Set<? extends T> objects) {
        this.disjointSet = new DisjointSet(objects.size());
        this.objectToIndexMapping = new HashMap<>(objects.size());
        this.indexToObjectMapping = new Object[objects.size()];
        int index = 0;
        for (T item : objects) {
            this.indexToObjectMapping[index] = item;
            this.objectToIndexMapping.put(item, index);
            index++;
        }
    }

    /**
     * Returns id of the union for the given object. Worst case is O(log N) before the compression, O(1) amortized. See
     * description of {@link DisjointSet#find(int)} method.
     *
     * @param object Object to find union for.
     * @return Id of the union for the given object.
     */
    public int find(T object) {
        return disjointSet.find(getObjectIndex(object));
    }

    /**
     * Combines two unions into one with union-by-size approach. Worst case execution time is O(log N) and O(1)
     * amortized. See description of {@link DisjointSet#find(int)} method.
     *
     * @param objectOne Object one.
     * @param objectTwo Object two.
     * @return Union id for combined unions.
     */
    public int union(T objectOne, T objectTwo) {
        int indexOne = getObjectIndex(objectOne);
        int indexTwo = getObjectIndex(objectTwo);
        return disjointSet.union(indexOne, indexTwo);
    }

    /**
     * Returns union size for given item. Worst case execution time is O(log N) and O(1) on average.
     *
     * @param object Item to get union size of.
     * @return Union size for given item.
     */
    public int getUnionSize(T object) {
        int index = getObjectIndex(object);
        return disjointSet.getUnionSize(index);
    }

    /**
     * Returns number of unions. O(N).
     *
     * @return Number of unions.
     */
    public int getNumberOfUnions() {
        return disjointSet.getNumberOfUnions();
    }

    /**
     * Returns a set of object for union members of the given item. Worst case time is O(N log N), O(N) amortized. See
     * {@link DisjointSet#find(int)} description for details.
     *
     * @param object Object to get union of.
     * @return Set of object that belong to union of object.
     */
    public Set<T> getUnionMembers(T object) {
        Set<Integer> indices = disjointSet.getUnionMembers(getObjectIndex(object));
        HashSet<T> result = new HashSet<>(indices.size());
        for (Integer index : indices) {
            @SuppressWarnings("unchecked")
            T item = (T) this.indexToObjectMapping[index];
            result.add(item);
        }
        return result;
    }

    /**
     * Returns number of elements in disjoint set.
     *
     * @return Number of elements in disjoint set.
     */
    public int size() {
        return this.disjointSet.size();
    }

    /**
     * Finds index for given object.
     * @param object Object.
     * @return Index for given object.
     */
    protected int getObjectIndex(T object) {
        Integer index = objectToIndexMapping.get(object);
        if (index == null) {
            throw new NullPointerException("Unable to find index for " + object);
        }
        return index;
    }
}
