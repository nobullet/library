package com.nobullet.interview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Given a string with all the space/punctuation characters omitted and a dictionary, find the list of words from the
 * string. See http://www.geeksforgeeks.org/dynamic-programming-set-32-word-break-problem/ for dynamic approach.
 */
public final class WordBreak {

    /**
     * Finds the longest list of words that source contains.
     *
     * @param source     Source.
     * @param dictionary Dictionary.
     * @return the longest list of words that source contains.
     */
    public static List<String> fromString(String source, Set<String> dictionary) {
        if (dictionary == null || dictionary.isEmpty()) {
            return Collections.emptyList();
        }
        int maxDictWordLength = dictionary.stream().mapToInt(word -> word.length()).max().getAsInt();
        return fromString(source, dictionary, 0, maxDictWordLength).getWords();
    }

    private static Phrase fromString(String source, Set<String> dictionary, int startPosition, int
            maxDictWordLength) {
        Phrase bestSolution = new Phrase();
        StringBuilder currentWordBuilder = new StringBuilder();

        for (int i = startPosition; i < source.length(); i++) {
            currentWordBuilder.setLength(0);
            int maxLastIndex = Math.min(i + maxDictWordLength, source.length());

            Phrase bestPrefixSolution = null;
            String bestPrefixSolutionWord = null;

            for (int j = i; j < maxLastIndex; j++) {
                currentWordBuilder.append(source.charAt(j));
                String currentWord = currentWordBuilder.toString();
                if (dictionary.contains(currentWord)) {
                    Phrase prefixCandidate = fromString(source, dictionary, j + 1, maxDictWordLength);
                    if (bestPrefixSolution == null ||
                            prefixCandidate.getLength() + currentWord.length() >
                                    bestPrefixSolutionWord.length() + bestPrefixSolution.getLength()) {
                        bestPrefixSolution = prefixCandidate;
                        bestPrefixSolutionWord = currentWord;
                    }
                }
            }

            if (bestPrefixSolutionWord != null && 1 + bestPrefixSolution.getLength() > bestSolution.getLength()) {
                bestSolution = new Phrase();
                bestSolution.add(bestPrefixSolutionWord);
                bestSolution.addAll(bestPrefixSolution.getWords());
            }
        }
        return bestSolution;
    }

    private static class Phrase {

        List<String> words = new ArrayList<>();
        int length = 0;

        public void add(String word) {
            length += word.length();
            words.add(word);
        }

        public void addAll(Collection<String> words) {
            for (String word : words) {
                add(word);
            }
        }

        public int getLength() {
            return length;
        }

        public List<String> getWords() {
            return words;
        }
    }

    private WordBreak() {
    }
}
