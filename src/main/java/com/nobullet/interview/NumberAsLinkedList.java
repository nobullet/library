package com.nobullet.interview;

/**
 * You have two numbers represented by a linked list, where each node contains a single digit. The digits are stored in
 * reverse order, such that the 1’s digit is at the head of the list. Write a function that adds the two numbers and
 * returns the sum as a linked list.<p>
 * EXAMPLE:
 * <p>
 * Input: (3 -> 1 -> 5) + (5 -> 9 -> 2): 513 + 295 = 808.
 * <p>
 * Output: 8 -> 0 -> 8
 */
public class NumberAsLinkedList {

    private Node head;

    /**
     * Constructs linked list from number.
     *
     * @param number
     */
    public NumberAsLinkedList(long number) {
        if (number < 0L) {
            throw new IllegalArgumentException("Positive value expected: " + number);
        }
        Node current = null;
        Node next;
        do {
            long digit = number % 10L;
            number /= 10L;
            next = new Node(Long.valueOf(digit).byteValue());
            if (current == null) {
                current = head = next;
            } else {
                current.next = next;
                current = next;
            }
        } while (number > 0);
    }

    /**
     * Private constructor.
     *
     * @param head Head of the linked list.
     */
    private NumberAsLinkedList(Node head) {
        this.head = head;
    }

    /**
     * Adds other number to this number.
     *
     * @param other Other number.
     * @return New number: a result of addition.
     */
    public NumberAsLinkedList add(NumberAsLinkedList other) {
        Node sumHead = null, sumCurrent = null;
        int overflow = 0;
        int sum = 0;
        Node otherNode = other.head;
        Node thisNode = this.head;
        do {
            sum = otherNode.value + thisNode.value + overflow;
            overflow = sum / 10;
            if (sumHead == null) {
                sumCurrent = sumHead = new Node(sum % 10);
            } else {
                sumCurrent.next = new Node(sum % 10);
                sumCurrent = sumCurrent.next;
            }
            otherNode = otherNode.next;
            thisNode = thisNode.next;
        } while (otherNode != null && thisNode != null);
        while (otherNode != null) {
            sum = otherNode.value + overflow;
            overflow = sum / 10;
            sumCurrent.next = new Node(sum % 10);
            sumCurrent = sumCurrent.next;
            otherNode = otherNode.next;
        }
        while (thisNode != null) {
            sum = thisNode.value + overflow;
            overflow = sum / 10;
            sumCurrent.next = new Node(sum % 10);
            sumCurrent = sumCurrent.next;
            thisNode = thisNode.next;
        }
        if (overflow > 0) {
            sumCurrent.next = new Node(overflow);
            sumCurrent = sumCurrent.next;
        }
        return new NumberAsLinkedList(sumHead);
    }

    /**
     * Reverses the number.
     *
     * @return New number, reversed.
     */
    public NumberAsLinkedList reverse() {
        Node currentThis = head;
        Node previousCopy = null;
        Node currentCopy;
        do {
            currentCopy = new Node(currentThis.value);
            currentCopy.next = previousCopy;
            previousCopy = currentCopy;
            currentThis = currentThis.next;
        } while (currentThis != null);
        return new NumberAsLinkedList(currentCopy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            sb.append(current.value);
            current = current.next;
        } while (current != null);
        sb.reverse();
        return sb.toString();
    }

    /**
     * Node in linked list.
     */
    private static class Node {

        int value;

        Node next;

        public Node(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }
}
