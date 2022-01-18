package com.nobullet.algo;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

public class BinarySearchAVLTree<T> {

    private final Comparator<T> comparator;
    private Node<T> root;

    public BinarySearchAVLTree() {
        this.comparator = null;
    }

    public BinarySearchAVLTree(Comparator<T> comparator) {
        this.comparator = Objects.requireNonNull(comparator);
    }

    private static void testDeepRR() {
        System.out.println("\n\nDeep RR");
        BinarySearchAVLTree<Integer> tree = new BinarySearchAVLTree<>();
        tree.add(20);
        tree.add(10);
        tree.add(30);

        tree.add(40);
        tree.add(50); // Tests RR

        System.out.println("Tree: " + tree.toString());
        System.out.println("Is balanced: " + tree.isBalanced());
    }

    private static void testRootRR() {
        System.out.println("\n\nRoot RR");
        BinarySearchAVLTree<Integer> tree = new BinarySearchAVLTree<>();
        tree.add(20);
        tree.add(30);
        tree.add(40);

        System.out.println("Is balanced: " + tree.isBalanced());
        System.out.println("Tree: " + tree.toString());
    }

    private static void testDeepLL() {
        System.out.println("\n\nDeep LL");
        BinarySearchAVLTree<Integer> tree = new BinarySearchAVLTree<>();
        tree.add(20);
        tree.add(10);
        tree.add(30);

        tree.add(5);
        tree.add(2); // Tests LL

        System.out.println("Is balanced: " + tree.isBalanced());
        System.out.println("Tree: " + tree.toString());
    }

    private static void testRootLL() {
        System.out.println("\n\nRoot LL");
        BinarySearchAVLTree<Integer> tree = new BinarySearchAVLTree<>();
        tree.add(40);
        tree.add(30);
        tree.add(20);

        System.out.println("Is balanced: " + tree.isBalanced());
        System.out.println("Tree: " + tree.toString());
    }

    private static void testRandom() {
        System.out.println("\n\nTest random tree");
        BinarySearchAVLTree<Integer> tree = new BinarySearchAVLTree<>();

        System.out.print(">  ");
        Integer lastInserted = null;
        Random random = new SecureRandom();
        for (int i = 0; i < 100; i++) {
            int next = random.nextInt(1000);
            System.out.print(next + ", ");
            tree.add(next);
            lastInserted = next;
        }

        System.out.println("\nIs balanced: " + tree.isBalanced());
        System.out.println("Tree: " + tree.toString());
        System.out.println("Contains " + lastInserted + ": " + tree.contains(lastInserted));
    }

    private static void testFailure() {
        System.out.println("\n\nTest BAD");
        int[] numbers = new int[]{442, 727, 477, 652, 447, 70, 643, 389};
        BinarySearchAVLTree<Integer> tree = new BinarySearchAVLTree<>();

        for (int i = 0; i < numbers.length; i++) {
            tree.add(numbers[i]);
        }

        System.out.println("\nIs balanced: " + tree.isBalanced());
        System.out.println("Tree: " + tree.toString());
    }

    public static void main(String... args) {
        testDeepRR();
        testRootRR();
        testDeepLL();
        testRootLL();
        testRandom();
        testFailure();
    }

    private int compare(T t1, T t2) {
        if (comparator != null) {
            return comparator.compare(t1, t2);
        }
        if (!(t1 instanceof Comparable)) {
            throw new IllegalArgumentException("No Comparator was given and value is not Comparable.");
        }
        Comparable<T> t1Comparable = (Comparable<T>) t1;
        return t1Comparable.compareTo(t2);
    }

    private void requireComparableOrComparator(T value) {
        if (comparator == null && !(value instanceof Comparable)) {
            throw new IllegalArgumentException("Given value " + value + " is not a Comparable and no Comparator was given.");
        }
    }

    public boolean add(T value) {
        requireComparableOrComparator(value);

        if (root == null) {
            root = new Node<>(value);
            return true;
        }

        Node<T> current = root;
        Node<T> insertionNode = null;
        while (current != null) {
            if (current.value == value) {
                return false;
            }
            insertionNode = current;
            if (compare(value, current.value) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        Node<T> newNode = new Node<>(value);
        if (compare(value, insertionNode.value) < 0) {
            insertionNode.left = newNode;
            newNode.parent = insertionNode;
        } else {
            insertionNode.right = newNode;
            newNode.parent = insertionNode;
        }

        balance(insertionNode, newNode);

        return true;
    }

    private void balance(Node<T> insertionNode, Node<T> newNode) {
        // Balance step: the first ancestor that became imbalanced must be used to perform rotation.
        Node<T> a = insertionNode;
        Node<T> b = newNode;
        Node<T> c = null;

        // Find first unbalanced node by going up to the root
        boolean isRotationNodeBalanced = true;
        while (a != null && (isRotationNodeBalanced = isBalanced(a))) {
            c = b;
            b = a;
            a = a.parent;
        }

        if (isRotationNodeBalanced) {
            // everything is balanced, no rotation needed.
            return;
        }

        assertReferencesAreNotSame(a, b, c);

        // Check the type of rotation.
        if (a.left == b) {
            // First step: left
            if (b != null && b.left == c) {
                performLLRotation(a, b, c);
            } else if (b != null && b.right == c) {
                performLRRotation(a, b, c);
            }
        } else if (a.right == b) {
            // First step: right
            if (b != null && b.left == c) {
                performRLRotation(a, b, c);
            } else if (b != null && b.right == c) {
                performRRRotation(a, b, c);
            }
        }
    }

    private void performRLRotation(Node<T> a, Node<T> b, Node<T> c) {
        // Remember the parent. Parent may be null if root is imbalanced
        Node<T> parent = a.parent;

        if (parent != null) {
            // Need to detect who is referencing A and replace it with C
            if (parent.left == a) {
                parent.left = c;
            } else {
                parent.right = c;
            }
        } else {
            // special case: root is an imbalanced node
            root = c;
        }
        c.parent = parent;


        b.left = c.right; // B.left = CR
        if (b.left != null) {
            b.left.parent = b;
        }

        a.right = c.left; // A.right = CL
        if (a.right != null) {
            a.right.parent = a;
        }

        c.left = a; // C.left = A
        a.parent = c; // A.parent = C

        c.right = b; // C.right = B
        b.parent = c; // B.parent = C

        assertReferencesAreNotSame(a, b, c);
    }

    private void performLRRotation(Node<T> a, Node<T> b, Node<T> c) {
        // Remember the parent. Parent may be null if root is imbalanced
        Node<T> parent = a.parent;

        if (parent != null) {
            // Need to detect who is referencing A and replace it with C
            if (parent.left == a) {
                parent.left = c;
            } else {
                parent.right = c;
            }
        } else {
            // special case: root is an imbalanced node
            root = c;
        }
        c.parent = parent;

        b.right = c.left; // B.right = CL
        if (b.right != null) {
            b.right.parent = b; // CL parent is B
        }

        a.left = c.right; // A.left = CR
        if (a.left != null) {
            a.left.parent = a; // CR parent is A
        }

        c.left = b; // C.left = B
        b.parent = c; // B.parent = C

        c.right = a; // C.right = A
        a.parent = c; // A.parent = C

        assertReferencesAreNotSame(a, b, c);
    }

    private void performRRRotation(Node<T> a, Node<T> b, Node<T> c) {
        // Remember the parent of imbalanced node. Parent may be null if root is imbalanced.
        Node<T> parent = a.parent;

        if (parent != null) {
            // decide which parent leaf points to A.
            if (parent.left == a) {
                parent.left = b;
            } else {
                parent.right = b;
            }
        } else {
            // special case: root is an imbalanced node
            root = b;
        }
        b.parent = parent; // This is same for LL

        a.right = b.left; // A.right = B.left
        if (b.left != null) { // Set parent for the relationship above
            b.left.parent = a;
        }

        b.left = a; // B.left = A
        a.parent = b; // Set parent for the relationship above

        assertReferencesAreNotSame(a, b, c);
    }

    private void performLLRotation(Node<T> a, Node<T> b, Node<T> c) {
        // Remember the parent of imbalanced node. Parent may be null if root is imbalanced.
        Node<T> parent = a.parent;

        if (parent != null) {
            // decide which parent leaf points to A.
            if (parent.left == a) {
                parent.left = b;
            } else {
                parent.right = b;
            }
        } else {
            // special case: root is an imbalanced node
            root = b;
        }

        b.parent = parent; // This is same for RR

        a.left = b.right; // A.left = B.right
        if (b.right != null) { // Set parent for the relationship above
            b.right.parent = a;
        }

        b.right = a; // B.right = A
        a.parent = b;  // Set parent for the relationship above

        assertReferencesAreNotSame(a, b, c);
    }

    private void assertReferencesAreNotSame(Node<T> a, Node<T> b, Node<T> c) {
        if (a == b) {
            throw new IllegalStateException("A & B are the same: " + a + " == " + b);
        }
        if (a == c) {
            throw new IllegalStateException("A & C are the same: " + a + " == " + c);
        }
        if (c == b) {
            throw new IllegalStateException("B & C are the same: " + b + " == " + c);
        }
        assertNotCyclic(a);
        assertNotCyclic(b);
        assertNotCyclic(c);
    }

    private void assertNotCyclic(Node<T> x) {
        if (x.parent != null && (x.parent == x.left || x.parent == x.right) || (x.left != null && x.left == x.right)) {
            throw new IllegalStateException("Given node " + x + " is cyclic.");
        }
    }

    public boolean contains(T value) {
        Node<T> current = root;
        while (current != null) {
            int comparison = compare(value, current.value);
            if (comparison < 0) {
                current = current.left;
            } else if (comparison == 0) {
                return true;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> parent) {
        if (parent == null) {
            return 0;
        }
        return 1 + Math.max(height(parent.left), height(parent.right));
    }

    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(Node<T> parent) {
        if (parent == null) {
            return true;
        }
        return (Math.abs(height(parent.left) - height(parent.right)) <= 1)
                && isBalanced(parent.left)
                && isBalanced(parent.right);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n  0: ");
        MutableInteger mutableLevel = new MutableInteger();
        breadthFirst((value, level) -> {
            if (mutableLevel.value != level) {
                sb.append("\n  ")
                        .append(level)
                        .append(": ");
                mutableLevel.value = level;
            }
            sb.append(value).append(" ");
        });
        sb.append("\n");
        return sb.toString();
    }

    /**
     * BFS tree traversal (by level).
     *
     * @param visitor Visitor that accepts the value and the level of the node.
     */
    public void breadthFirst(BiConsumer<T, Integer> visitor) {
        if (root == null) {
            return;
        }
        breadthFirstInternal(root, (node, level) -> visitor.accept(node.value, level));
    }

    private void breadthFirstInternal(Node<T> parent, BiConsumer<Node<T>, Integer> visitor) {
        Deque<Node<T>> frontier = new ArrayDeque<>();
        Deque<Integer> levels = new ArrayDeque<>();

        frontier.addLast(parent);
        levels.add(0);

        while (!frontier.isEmpty()) {
            Node<T> current = frontier.removeFirst();
            int level = levels.removeFirst();
            visitor.accept(current, level);
            if (current.left != null) {
                frontier.addLast(current.left);
                levels.addLast(level + 1);
            }
            if (current.right != null) {
                frontier.addLast(current.right);
                levels.addLast(level + 1);
            }
        }
    }

    private static class MutableInteger {
        int value;
    }

    private static class Node<T> {
        final T value;
        Node<T> parent;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "{Node:value = " + value + "}";
        }
    }
}
