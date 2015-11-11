package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Trie (a prefix tree).
 */
public class Trie {

    private final char startCharacter;
    private final char endCharacter;
    private Node rootNode;

    /**
     * Constructs trie data structure that takes characters in the given range.
     *
     * @param startCharacter Start character.
     * @param endCharacter End character.
     */
    public Trie(char startCharacter, char endCharacter) {
        if (startCharacter >= endCharacter) {
            throw new IllegalArgumentException("startCharacter is expected to be less than endCharacter.");
        }
        this.startCharacter = startCharacter;
        this.endCharacter = endCharacter;
        this.rootNode = new Node(null, ' ', ((int) endCharacter - (int) startCharacter));
    }

    /**
     * Default with English alphabet and 0-9.
     */
    public Trie() {
        this('0', 'z');
    }

    /**
     * Adds all the given words into the trie.
     *
     * @param words Words.
     * @return If all the words were added successfully.
     */
    public boolean addAll(Collection<String> words) {
        for (String src : words) {
            if (!add(src)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds all the given words into the trie.
     *
     * @param words Words.
     * @return If all the words were added successfully.
     */
    public boolean addAll(String... words) {
        for (String src : words) {
            if (!add(src)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a word into the current trie. If the character is not from alphabet, returns false.
     *
     * @param word Source of the word.
     * @return Whether the word was successfully added.
     */
    public boolean add(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        Node currentNode = rootNode;
        Node nextNode = null;
        int characterCodeStart = (int) this.startCharacter;
        int size = ((int) endCharacter - (int) startCharacter);
        char currentChar;
        for (int i = 0; i < word.length(); i++) {
            currentChar = word.charAt(i);
            if (currentChar < this.startCharacter || currentChar > this.endCharacter) {
                return false;
            }
            nextNode = currentNode.getChild(currentChar, characterCodeStart);
            if (nextNode == null) {
                nextNode = currentNode.addChild(currentChar, characterCodeStart, size);
            }
            if (word.length() == i + 1) {
                nextNode.terminalNode = true;
            }
            currentNode = nextNode;
        }
        return true;
    }

    /**
     * Finds a list of words that can be completed by prefix.
     *
     * @param prefix Prefix to search for.
     * @return List of words that start with prefix.
     */
    public List<String> find(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }
        Node lastPrefixNode = rootNode;
        StringBuilder prefixBuilder = null;
        Node nextNode = null;
        int characterCodeStart = (int) this.startCharacter;
        char currentChar;

        for (int level = 0; level < prefix.length(); level++) {
            currentChar = prefix.charAt(level);
            if (currentChar < this.startCharacter || currentChar > this.endCharacter) {
                break;
            }
            nextNode = lastPrefixNode.getChild(currentChar, characterCodeStart);
            if (nextNode == null) {
                return Collections.emptyList();
            }
            if (prefixBuilder == null) {
                prefixBuilder = new StringBuilder(prefix.length());
            }
            prefixBuilder.append(nextNode.currentChar);
            lastPrefixNode = nextNode;
        }

        if (lastPrefixNode == rootNode) {
            return Collections.emptyList();
        }

        if (!lastPrefixNode.hasChildren()) {
            return Collections.singletonList(prefixBuilder.toString());
        }

        List<String> result = new ArrayList<>();
        Deque<Node> frontier = new LinkedList<>();
        frontier.addLast(lastPrefixNode);
        while (!frontier.isEmpty()) {
            Node current = frontier.removeFirst();
            if (current.hasChildren()) {
                current.getChildren().stream().forEach(frontier::addLast);
            }
            if (current.terminalNode) {
                appendToResult(current, lastPrefixNode, new StringBuilder(prefixBuilder), result);
            }
        }
        return result;
    }

    /**
     * Appends a word to result.
     *
     * @param current Current node.
     * @param prefixEnding Last node of the prefix.
     * @param prefixStringBuilder Builder with prefix.
     * @param result List of results.
     */
    void appendToResult(Node current, Node prefixEnding, StringBuilder prefixStringBuilder, List<String> result) {
        StringBuilder sb = new StringBuilder();
        while (current != prefixEnding) {
            sb.append(current.currentChar);
            current = current.parent;
        }
        sb.reverse();
        prefixStringBuilder.append(sb);
        result.add(prefixStringBuilder.toString());
    }

    /**
     * Node in trie.
     */
    private static class Node {

        Node parent;
        Node[] nodes;
        Node singleChild;
        int level;
        int childrenCount;
        char currentChar;
        boolean terminalNode;

        Node(Node parent, char currentChar) {
            this.parent = parent;
            this.currentChar = currentChar;
            this.childrenCount = 0;
            this.singleChild = null;
            this.nodes = null;
            this.level = parent != null ? parent.level + 1 : 0;
            this.terminalNode = false;
        }

        Node(Node parent, char currentChar, int alphabetSize) {
            this(parent, currentChar);
            this.nodes = new Node[alphabetSize + 1];
        }

        Node getChild(char character, int characterCodeStart) {
            if (singleChild != null && singleChild.currentChar == character) {
                return singleChild;
            } else if (nodes != null) {
                int index = (int) character - characterCodeStart;
                return nodes[index];
            }
            return null;
        }

        boolean hasChildren() {
            return childrenCount > 0;
        }

        Set<Node> getChildren() {
            if (singleChild != null) {
                return Collections.singleton(singleChild);
            }
            Set<Node> result = new HashSet<>(childrenCount);
            for (Node node : nodes) {
                if (node != null) {
                    result.add(node);
                }
            }
            return result;
        }

        Node addChild(char character, int characterCodeStart, int alphabetSize) {
            if (childrenCount == 0) {
                childrenCount++;
                return (singleChild = new Node(this, character));
            } else if (childrenCount == 1) {
                if (singleChild.currentChar == character) {
                    return singleChild;
                }
                this.nodes = new Node[alphabetSize + 1];
                // Move single chile into array.
                int index = (int) singleChild.currentChar - characterCodeStart;
                this.nodes[index] = singleChild;
                singleChild = null;
                // Add new node into array.
                childrenCount++;
                int newNodeIndex = (int) character - characterCodeStart;
                return (this.nodes[newNodeIndex] = new Node(this, character));
            } else {
                int index = (int) character - characterCodeStart;
                Node existingNode = this.nodes[index];
                if (existingNode != null) {
                    return existingNode;
                }
                childrenCount++;
                return (this.nodes[index] = new Node(this, character));
            }
        }

        @Override
        public String toString() {
            return String.format("{char: '%s', childs: %d, level: %d}", currentChar, childrenCount, level);
        }
    }
}
