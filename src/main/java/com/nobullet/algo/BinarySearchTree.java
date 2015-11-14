package com.nobullet.algo;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Simple BST.
 *
 * @param <T> Type.
 */
public class BinarySearchTree<T extends Comparable<? super T>> {

    private Node<T> root;
    private int numberOfNodes;

    /**
     * Constructs empty BST.
     */
    public BinarySearchTree() {
    }

    /**
     * Constructs BST with initial value as a root.
     *
     * @param initialValue
     */
    public BinarySearchTree(T initialValue) {
        this.root = new Node<>(initialValue);
        this.numberOfNodes++;
    }

    /**
     * Adds all elements into the BST.
     *
     * @param values Values to add.
     * @return Current BST.
     */
    public BinarySearchTree<T> addAll(Collection<T> values) {
        if (values == null) {
            throw new NullPointerException("Values is expected.");
        }
        for (T value : values) {
            if (value == null) {
                throw new NullPointerException("Value is expected.");
            }
            add(value);
        }
        return this;
    }

    /**
     * Adds a value into the BST or increases the presence of the existing element. O(log N).
     *
     * @param value Value to remove.
     * @return Current BST.
     */
    public BinarySearchTree<T> add(T value) {
        if (value == null) {
            throw new NullPointerException("Value is expected.");
        }
        if (root == null) {
            this.root = new Node<>(value);
            this.numberOfNodes++;
            return this;
        }
        Node<T> insertionPoint = findInternal(value);
        int temp = value.compareTo(insertionPoint.value);
        if (temp == 0) {
            insertionPoint.use();
        } else if (temp < 0) {
            insertionPoint.left = new Node<>(value);
            this.numberOfNodes++;
        } else {
            insertionPoint.right = new Node<>(value);
            this.numberOfNodes++;
        }
        return this;
    }

    /**
     * Removes the node. O(log N).
     *
     * @param value Value to remove.
     */
    public BinarySearchTree<T> remove(T value) {
        if (root == null) {
            return this;
        }
        remove(root, value);
        return this;
    }

    private void remove(Node<T> node, T value) {
        Node<T> current = node;
        Node<T> parent = null;
        int temp;
        while (current != null) {
            if (current.value == null) {
                throw new IllegalStateException(
                        "Expected value: " + System.identityHashCode(current) + " Node: " + node + " Value:" + value);
            }
            temp = value.compareTo(current.value);
            if (temp < 0) {
                parent = current;
                current = current.left;
            } else if (temp == 0) {
                break;
            } else {
                parent = current;
                current = current.right;
            }
        }
        if (current == null || current.unuse() > 0) {
            return;
        }
        this.numberOfNodes--;
        if (current.left == null && current.right == null) {
            // Leaf case.
            if (parent != null) {
                if (parent.right == current) {
                    parent.right = null;
                } else {
                    parent.left = null;
                }
            } else {
                root = null;
            }
            current.value = null;
        } else if (current.left != null && current.right != null) {
            // Find minimal element in the right subtree and switch, 
            // removing minimal element in the right subtree.
            Node<T> minInRight = current.right;
            Node<T> minInRightParent = current;
            while (minInRight != null && minInRight.left != null) {
                minInRightParent = minInRight;
                minInRight = minInRight.left;
            }
            current.count = minInRight.count;
            current.value = minInRight.value;
            if (minInRightParent != current) {
                minInRightParent.left = minInRight.right;
            } else {
                current.right = null;
            }
            minInRight.left = null;
            minInRight.right = null;
            minInRight.value = null;
        } else if (current.left != null && current.right == null) {
            // One child: left.
            if (parent != null) {
                if (parent.right == current) {
                    parent.right = current.left;
                } else {
                    parent.left = current.left;
                }
            } else {
                root = current.left;
            }
            current.left = null;
            current.value = null;
        } else if (current.left == null && current.right != null) {
            // One child: right.
            if (parent != null) {
                if (parent.right == current) {
                    parent.right = current.right;
                } else {
                    parent.left = current.right;
                }
            } else {
                root = current.right;
            }
            current.right = null;
            current.value = null;
        }
    }

    public boolean isBalanced() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns minimal value from the tree. O(log N).
     *
     * @return Minimal value.
     */
    public T findMin() {
        if (root == null) {
            return null;
        }
        return findMin(root).value;
    }

    /**
     * Returns maximal value from tree. O(log N).
     *
     * @return Max value.
     */
    public T findMax() {
        if (root == null) {
            return null;
        }
        return findMax(root).value;
    }

    private Node<T> findMin(Node<T> parent) {
        while (parent.left != null) {
            parent = parent.left;
        }
        return parent;
    }

    private Node<T> findMax(Node<T> parent) {
        while (parent.right != null) {
            parent = parent.right;
        }
        return parent;
    }

    public void inOrder(BiConsumer<T, Integer> visitor) {
        if (root == null) {
            return;
        }
        inOrderInternal(root, (node, level) -> visitor.accept(node.value, level), 0);
    }

    private void inOrderInternal(Node<T> parent, BiConsumer<Node<T>, Integer> visitor, int level) {
        if (parent.left != null) {
            inOrderInternal(parent.left, visitor, level + 1);
        }
        int repeats = parent.count;
        while (repeats-- > 0) {
            visitor.accept(parent, level);
        }
        if (parent.right != null) {
            inOrderInternal(parent.right, visitor, level + 1);
        }
    }

    private Node<T> findInternal(T value) {
        Node<T> current = root;
        Node<T> parent = null;
        int temp;
        while (current != null) {
            temp = value.compareTo(current.value);
            if (temp < 0) {
                parent = current;
                current = current.left;
            } else if (temp == 0) {
                return current;
            } else {
                parent = current;
                current = current.right;
            }
        }
        // If was not found - return parent (insertion point).
        return parent;
    }

    private static class Node<T> {

        int count;
        T value;
        Node<T> left;
        Node<T> right;

        public Node(T value) {
            this.value = value;
            this.count = 1;
        }

        public int use() {
            return ++count;
        }

        public int unuse() {
            return count > 0 ? --count : 0;
        }

        @Override
        public String toString() {
            return "{v: " + value + ", c=" + count + '}';
        }
    }
}
