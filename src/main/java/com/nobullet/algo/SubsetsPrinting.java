package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SubsetsPrinting {
    public static void main(String... args) {
        List<Character> input = Arrays.asList('a', 'b', 'c');

        System.out.println("\nSubsets of " + input + "  :");
        printSubsets(input);
    }

    public static void printSubsets(List<Character> input) {
        printSubsets(new ArrayList<>(0), true, input, 0);
    }

    private static void printSubsets(List<Character> parent, boolean shouldPrintParent, List<Character> input, int currentElementIndex) {
        if (shouldPrintParent) {
            System.out.println(parent);
        }
        // Index validness
        if (currentElementIndex >= input.size()) {
            return;
        }

        // Not taking the "currentElementIndex" into account
        printSubsets(parent, false, input, currentElementIndex + 1);

        // Taking the "currentElementIndex" into account
        List<Character> nextParent = copyAdding(parent, input.get(currentElementIndex));
        printSubsets(nextParent, true, input, currentElementIndex + 1);
    }

    private static List<Character> copyAdding(List<Character> input, Character item) {
        List<Character> result = new ArrayList<>(input.size() + 1);
        result.addAll(input);
        result.add(item);
        return result;
    }
}
