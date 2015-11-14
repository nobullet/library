package com.nobullet.interview;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Given a source word, target word and an English dictionary, transform the source word to target by
 * changing/adding/removing 1 character at a time, while all intermediate words being valid English words. Return the
 * transformation chain which has the smallest number of intermediate words.
 */
public final class WordTransformation {

    private static final String NOWHERE = "__NOWHERE__";

    /**
     * Returns shortest transformation path from one word to another within given dictionary.
     *
     * @param source Source word.
     * @param target Target word.
     * @param dictionary Dictionary to check.
     * @return Transformation path with one element (source), when there is no transformation, with several elements
     * that construct the path of transformation or empty array when given source word is not in dictionary.
     */
    public static List<String> transformationFor(String source, String target, Set<String> dictionary) {
        source = source.toLowerCase();
        target = target.toLowerCase();
        // Lower case all words.
        dictionary = dictionary.stream().map(s -> s.toLowerCase()).collect(Collectors.toSet());
        // If no word in dictionary - return empty list.
        if (!dictionary.contains(source) || !dictionary.contains(target)) {
            return Collections.emptyList();
        }
        Deque<String> frontier = new ArrayDeque<>(dictionary.size());
        Map<String, String> cameFrom = new HashMap<>();
        cameFrom.put(source, NOWHERE);
        frontier.addLast(source);
        // BFS.
        while (!frontier.isEmpty()) {
            String word = frontier.removeFirst();
            if (word.equals(target)) {
                break;
            }
            for (String dictWord : modificationsOf(word, dictionary)) {
                if (!cameFrom.containsKey(dictWord)) {
                    frontier.addLast(dictWord);
                    cameFrom.put(dictWord, word);
                }
            }
        }
        if (!cameFrom.containsKey(target)) {
            return Collections.singletonList(source);
        }
        List<String> result = new ArrayList<>();
        String current = target;
        while (current != null && !current.equals(source)) {
            result.add(current);
            current = cameFrom.get(current);
        }
        result.add(source);
        Collections.reverse(result);
        return result;
    }

    /**
     * Builds all modifications for given word that are in given dictionary.
     *
     * @param source Source for modifications.
     * @param dictionary Dictionary to check.
     * @return List of words that can be modified from given word.
     */
    private static Set<String> modificationsOf(String source, Set<String> dictionary) {
        String candidate;
        Set<String> result = new HashSet<>();
        // Changing.
        StringBuilder sb;
        for (int i = 0; i < source.length(); i++) {
            sb = new StringBuilder(source);
            for (char ch = 'a'; ch <= 'z'; ch++) {
                sb.setCharAt(i, ch);
                candidate = sb.toString();
                if (!source.equals(candidate) && dictionary.contains(candidate)) {
                    result.add(candidate);
                }
            }
        }
        // Adding (front).
        sb = new StringBuilder(" ");
        sb.append(source);
        for (char ch = 'a'; ch <= 'z'; ch++) {
            sb.setCharAt(0, ch);
            candidate = sb.toString();
            if (!source.equals(candidate) && dictionary.contains(candidate)) {
                result.add(candidate);
            }
        }
        // Adding (end).
        sb = new StringBuilder(source);
        sb.append(" ");
        for (char ch = 'a'; ch <= 'z'; ch++) {
            sb.setCharAt(source.length(), ch);
            candidate = sb.toString();
            if (!source.equals(candidate) && dictionary.contains(candidate)) {
                result.add(candidate);
            }
        }
        // Removing.
        for (int i = 0; i < source.length(); i++) {
            candidate = removeCharAt(source, i);
            if (dictionary.contains(candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    /**
     * Returns a string with one character removed at given index.
     *
     * @param source Source to remove.
     * @param index Index to remove.
     * @return A string with one character removed.
     */
    private static String removeCharAt(String source, int index) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            if (i != index) {
                sb.append(source.charAt(i));
            }
        }
        return sb.toString();
    }

    private WordTransformation() {
    }
}
