package com.nobullet.algo;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
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
     * Builds balanced tree from given sorted list.
     *
     * @param <L> Type for implementation of {@link RandomAccess} {@link List}.
     * @param sortedList Sorted list.
     * @return Current BST.
     */
    public <L extends List<T> & RandomAccess> BinarySearchTree<T> addAllSorted(L sortedList) {
        addAllSorted(sortedList, 0, sortedList.size() - 1);
        return this;
    }

    private <L extends List<T> & RandomAccess> void addAllSorted(L list, int from, int to) {
        int mid = (from + to) >>> 1;
        add(list.get(mid));
        if (from < mid) {
            addAllSorted(list, from, mid - 1);
        }
        if (mid < to) {
            addAllSorted(list, mid + 1, to);
        }
    }

    /**
     * Removes the node. O(log N).
     *
     * @param value Value to remove.
     * @return Current BST.
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

    /**
     * Returns height of the tree. O(N).
     *
     * @return Height of the tree.
     */
    public int height() {
        return height(root);
    }

    private int height(Node<T> parent) {
        if (parent == null) {
            return 0;
        }
        return 1 + Math.max(height(parent.left), height(parent.right));
    }

    /**
     * Checks if the given sub-tree is balanced in O(N).
     *
     * @return Whether the given sub-tree is balanced in O(N),
     */
    public boolean isBalanced() {
        MutableInteger height = new MutableInteger();
        return isBalanced(root, height);
    }

    /**
     * Checks if the given sub-tree is balanced in O(N), using mutable int.
     *
     * @param parent Parent node.
     * @param height Current height;
     * @return Whether the given sub-tree is balanced in O(N).
     */
    private boolean isBalanced(Node<T> parent, MutableInteger height) {
        if (parent == null) {
            return true;
        }
        MutableInteger leftHeight = new MutableInteger();
        MutableInteger rightHeight = new MutableInteger();
        boolean isLeftBalanced = isBalanced(parent.left, leftHeight);
        boolean isRightBalanced = isBalanced(parent.right, rightHeight);
        height.value = 1 + Math.max(leftHeight.value, rightHeight.value);
        if (Math.abs(leftHeight.value - rightHeight.value) >= 2) {
            return false;
        }
        return isLeftBalanced && isRightBalanced;
    }

    /**
     * Checks if the current BST is balanced in O(N^2).
     *
     * @return Whether the current BST is balanced.
     */
    public boolean isBalancedNaive() {
        return isBalancedNaive(root);
    }

    private boolean isBalancedNaive(Node<T> parent) {
        if (parent == null) {
            return true;
        }
        return (Math.abs(height(parent.left) - height(parent.right)) <= 1)
                && isBalancedNaive(parent.left)
                && isBalancedNaive(parent.right);
    }

    /**
     * Checks if no two leaf nodes differ in distance from the root by more than one. O(N).
     *
     * @return Whether no two leaf nodes differ in distance from the root by more than one.
     */
    public boolean hasNoTwoLeafNodesDifferInDistanceByMoreThanOne() {
        if (root == null) {
            return true;
        }
        Set<Integer> leafLevels = new HashSet<>();
        inOrderInternal(root, (node, level) -> {
            if (node.left == null && node.right == null) {
                leafLevels.add(level);
            }
        }, 0);
        if (leafLevels.size() == 1) {
            return true;
        }
        if (leafLevels.size() != 2) {
            return false;
        }
        Iterator<Integer> it = leafLevels.iterator();
        return Math.abs(it.next() - it.next()) <= 1;
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

    private Node<T> findMin(Node<T> parent) {
        while (parent.left != null) {
            parent = parent.left;
        }
        return parent;
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

    private Node<T> findMax(Node<T> parent) {
        while (parent.right != null) {
            parent = parent.right;
        }
        return parent;
    }

    /**
     * Traverses the BST in-order.
     *
     * @param visitor Visitor that accepts the value and the level of the node.
     */
    public void inOrder(BiConsumer<T, Integer> visitor) {
        if (root == null) {
            return;
        }
        //inOrderInternal(root, (node, level) -> visitor.accept(node.value, level), 0);
        inOrderInternalIterative(root, (node, level) -> visitor.accept(node.value, level));
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

    /**
     * Traverses the BST in-order.
     *
     * @param visitor Visitor that accepts the value and the level of the node.
     */
    public void inOrderIterative(BiConsumer<T, Integer> visitor) {
        if (root == null) {
            return;
        }
        inOrderInternalIterative(root, (node, level) -> visitor.accept(node.value, level));
    }

    private void inOrderInternalIterative(Node<T> parent, BiConsumer<Node<T>, Integer> visitor) {
        Deque<Node<T>> nodesStack = new ArrayDeque<>(this.numberOfNodes);
        Deque<Integer> levelsStack = new ArrayDeque<>(this.numberOfNodes);

        Node<T> current = parent;
        int levelCurrent = 0;

        boolean done = false;
        while (!done) {
            if (current != null) {
                nodesStack.push(current);
                levelsStack.push(levelCurrent++);
                current = current.left;
            } else {
                if (!nodesStack.isEmpty()) {
                    current = nodesStack.pop();
                    levelCurrent = levelsStack.pop();
                    int repeats = current.count;
                    while (repeats-- > 0) {
                        visitor.accept(current, levelCurrent);
                    }
                    current = current.right;
                } else {
                    done = true;
                }
            }
        }
    }

    /**
     * Traverses the BST in-order.
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
        Deque<Node<T>> frontier = new ArrayDeque<>(this.numberOfNodes);
        Deque<Integer> levels = new ArrayDeque<>(this.numberOfNodes);

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

    /**
     * Returns node with value or the parent node (insertion point).
     *
     * @param value Value to find.
     * @return node with value or the parent node (insertion point).
     */
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n0: ");
        MutableInteger mutableLevel = new MutableInteger();
        breadthFirst((value, level) -> {
            if (mutableLevel.value != level) {
                sb.append("\n")
                        .append(level)
                        .append(": ");
                mutableLevel.value = level;
            }
            sb.append(value).append(" ");
        });
        sb.append("\n");
        return sb.toString();
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

    private static class MutableInteger {

        int value;
    }
}
