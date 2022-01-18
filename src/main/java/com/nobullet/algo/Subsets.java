package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Find a powerset (all addSubsets) for given input.
 */
public final class Subsets {

    public static void main(String... args) {
        List<Character> input = Arrays.asList('a', 'b', 'c', 'd');
        List<List<Character>> powerSet = subsets(input);

        System.out.println("\nSubsets of " + input + "  :");
        for (List<Character> subset : powerSet) {
            System.out.println(subset);
        }
    }

    public static List<List<Character>> subsets(List<Character> input) {
        List<List<Character>> subsets = new ArrayList<>((int) Math.pow(2, input.size()));
        addSubsets(subsets, new ArrayList<>(0), true, input, 0);
        subsets.sort(new Comparator<List<Character>>() {
            @Override
            public int compare(List<Character> o1, List<Character> o2) {
                if (o1.size() == o2.size()) {
                    return o1.toString().compareTo(o2.toString());
                }
                return o1.size() < o2.size() ? -1 : 1;
            }
        });
        return subsets;
    }

    private static void addSubsets(List<List<Character>> powerSet, List<Character> parent, boolean addingParent, List<Character> input, int index) {
        if (addingParent) {
            powerSet.add(copy(parent)); // This is where we print
        }
        if (index >= input.size()) {
            return;
        }
        addSubsets(powerSet, parent, false, input, index + 1);
        addSubsets(powerSet, copyAdding(parent, input.get(index)), true, input, index + 1);
    }

    private static List<Character> copy(List<Character> input) {
        return new ArrayList<>(input);
    }

    private static List<Character> copyAdding(List<Character> input, Character item) {
        List<Character> result = new ArrayList<>(input.size() + 1);
        result.addAll(input);
        result.add(item);
        return result;
    }
}
