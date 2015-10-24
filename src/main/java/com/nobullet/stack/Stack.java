package com.nobullet.stack;

/**
 * Stack.
 *
 * @param <T> Data type.
 */
public class Stack<T> {

    int size;
    Node<T> head;

    public T peek() {
        return head != null ? head.data : null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void push(T newData) {
        size++;
        if (head == null) {
            head = new Node<>(newData);
            return;
        }
        Node<T> newNode = new Node<>(newData);
        newNode.next = head;
        head = newNode;
    }

    public T pop() {
        if (head == null) {
            return null;
        }
        size--;
        Node<T> h = head;
        head = head.next;
        h.next = null; // Remove reference to head.
        T data = h.data; // Remove reference to data.
        h.data = null;
        return data;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> node = head;
        while(node != null) {
            sb.append(node.toString());
            sb.append(", ");
            node = node.next;
        }
        return "Stack{size=" + size + ", nodes=" + sb + "}";
    }

    /**
     * Drains one stack to another.
     *
     * @param <T> Data type.
     * @param from From stack.
     * @param to To stack.
     */
    public static <T> void drain(Stack<T> from, Stack<T> to) {
        while (!from.isEmpty()) {
            to.push(from.pop());
        }
    }

    public static class Node<T> {

        T data;
        Node<T> next;

        public Node(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "{" + data + "}";
        }
    }
}
