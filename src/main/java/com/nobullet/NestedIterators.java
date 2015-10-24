package com.nobullet;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterator over nested iterators, etc.
 */
public final class NestedIterators {

    /**
     * Skips nulls inside the inner iterator.
     *
     * @param <E> Type.
     */
    public static final class SkipNullIterator<E> implements Iterator<E> {

        private E prefetched;
        private boolean hasNextValue = false;
        private Iterator<E> iterator;

        public SkipNullIterator(Iterator<E> iterator) {
            this.iterator = iterator;
            this.prefetched = prefetchNextValue();
        }

        @Override
        public boolean hasNext() {
            return hasNextValue;
        }

        @Override
        public E next() {
            if (hasNextValue) {
                hasNextValue = false;
                E oldPrefetched = prefetched;
                prefetched = prefetchNextValue();
                return oldPrefetched;
            }
            throw new IllegalStateException("#hasNext() returns false.");
        }

        private E prefetchNextValue() {
            hasNextValue = false;
            while (iterator.hasNext()) {
                E next = iterator.next();
                if (next != null) {
                    hasNextValue = true;
                    return next;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Skips nulls inside the arrays.
     */
    public static final class NestedArrayIterator implements Iterator<Object> {

        private static class ArrayState {

            public Object[] array;
            public int arrayIndex;
        }
        private Object[] currentArray;
        private int currentArrayIndex;
        private final LinkedList<ArrayState> stack;
        private boolean hasNextValue = false;
        private Object prefetched;

        public NestedArrayIterator(Object[] array) {
            this.currentArray = array;
            this.stack = new LinkedList<>();
            this.prefetched = prefetchNextValue();
        }

        @Override
        public boolean hasNext() {
            return hasNextValue;
        }

        @Override
        public Object next() {
            if (hasNextValue) {
                hasNextValue = false;
                Object oldPrefetched = prefetched;
                prefetched = prefetchNextValue();
                return oldPrefetched;
            }
            throw new IllegalStateException("#hasNext() returns false.");
        }

        /**
         * Pre-fetches next value and marks internal flag if iterator has next element.
         *
         * @return Next value (might be null).
         */
        private Object prefetchNextValue() {
            hasNextValue = false;
            while (currentArray != null) {
                while (currentArrayIndex < currentArray.length) {
                    Object next = currentArray[currentArrayIndex];
                    currentArrayIndex++;
                    if (next instanceof Object[]) {
                        ArrayState st = new ArrayState();
                        st.array = currentArray;
                        st.arrayIndex = currentArrayIndex;
                        stack.addLast(st);
                        currentArray = (Object[]) next;
                        currentArrayIndex = 0;
                    } else {
                        hasNextValue = true;
                        return next;
                    }
                }
                if (!stack.isEmpty()) {
                    ArrayState st = stack.removeLast();
                    currentArray = st.array;
                    currentArrayIndex = st.arrayIndex;
                } else {
                    currentArray = null;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Respects nulls inside the collections.
     */
    public static final class NestedCollectionIterator implements Iterator<Object> {

        private final LinkedList<Iterator<Object>> stack;
        private Object prefetched;
        private Iterator<Object> currentIterator;
        private boolean hasNextValue = false;
        private final boolean deepFirst;

        public NestedCollectionIterator(Collection<Object> collection) {
            this.currentIterator = collection.iterator();
            this.stack = new LinkedList<>();
            this.prefetched = prefetchNextValue();
            this.deepFirst = true;
        }

        public NestedCollectionIterator(Collection<Object> collection, boolean deepFirst) {
            this.currentIterator = collection.iterator();
            this.stack = new LinkedList<>();
            this.prefetched = prefetchNextValue();
            this.deepFirst = deepFirst;
        }

        @Override
        public boolean hasNext() {
            return hasNextValue;
        }

        @Override
        public final Object next() {
            if (hasNextValue) {
                hasNextValue = false;
                Object oldPrefetched = prefetched;
                prefetched = prefetchNextValue();
                return oldPrefetched;
            }
            throw new IllegalStateException("#hasNext() returns false.");
        }

        /**
         * Pre-fetches next value and marks internal flag if iterator has next element.
         *
         * @return Next value (might be null).
         */
        private Object prefetchNextValue() {
            hasNextValue = false;
            while (currentIterator != null) {
                while (currentIterator.hasNext()) {
                    Object next = currentIterator.next();
                    if (next instanceof Collection) {
                        @SuppressWarnings("unchecked")
                        Iterator<Object> newIterator = ((Collection<Object>) next).iterator();
                        if (deepFirst) {
                            stack.addLast(currentIterator); // Deep first
                            currentIterator = newIterator;
                        } else {
                            stack.addLast(newIterator); // Breadth first
                        }
                    } else {
                        hasNextValue = true;
                        return next;
                    }
                }
                if (!stack.isEmpty()) {
                    currentIterator = stack.removeLast();
                } else {
                    currentIterator = null;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private NestedIterators() {
    }
}
