package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Permutations generator. For academic interest. Check Guava's implementation (smart and fast) by Knuth.
 */
public class Permutations {

    /**
     * Generates a list of possible permutations for the given list.
     *
     * @param <E> Type of the element.
     * @param source Source list
     * @return Permutations.
     */
    public static <E> List<List<E>> of(List<E> source) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        // merge (a, [   ]) --> [[a]]
        // merge (b, [[a]]) --> [[ba], [ab]]
        // merge (c, [[ba], [ab]]) --> [[cba], [bca], [bac], [cab], [acb], [abc]]
        // ...

        LinkedList<List<E>> permutations = new LinkedList<>();
        LinkedList<List<E>> allPrev;
        Iterator<E> it = source.iterator();
        E first = it.next();

        permutations.add(Collections.singletonList(first));

        while (it.hasNext()) {
            E e = it.next();
            allPrev = permutations;
            permutations = new LinkedList<>();
            for (List<E> previous : allPrev) {
                permutations(e, previous, permutations);
            }
        }

        return permutations;
    }

    /**
     * Generates a next set of permutations for previous permutation and places it in common result.
     *
     * @param <E> Type.
     * @param element Element to insert.
     * @param previous Permutation from the previous state.
     * @param permutations Common result.
     */
    static <E> void permutations(E element, List<E> previous, LinkedList<List<E>> permutations) {
        for (int index = 0; index <= previous.size(); index++) {
            permutations.add(copyAndInsertAt(previous, index, element));
        }
    }

    /**
     * Copies source into the new array list and inserts given element at given index.
     *
     * @param <E> Type.
     * @param source Source list
     * @param index Index of the element to insert. Ignored if incorrect.
     * @param element Element to insert.
     * @return Copied source as an array list with element inserted.
     */
    static <E> ArrayList<E> copyAndInsertAt(List<E> source, int index, E element) {
        ArrayList<E> newPermutation = new ArrayList<>(source.size() + 1);
        int i = 0;
        for (E e : source) {
            if (i++ == index) {
                newPermutation.add(element);
            }
            newPermutation.add(e);
        }
        if (i == index) {
            newPermutation.add(element);
        }
        return newPermutation;
    }
}
