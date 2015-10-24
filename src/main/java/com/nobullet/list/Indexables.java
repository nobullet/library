package com.nobullet.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * Utility class to create indexables.
 */
public final class Indexables {

    /**
     * Creates indexable from given character sequence.
     *
     * @param seq Character sequence.
     * @return Indexable for character sequence.
     */
    public static Indexable<Character> fromChars(CharSequence seq) {
        return new IndexableCharSequence(seq);
    }

    /**
     * Creates indexable from given list.
     *
     * @param <T> Type.
     * @param src Source.
     * @return Indexable from given list.
     */
    public static <T> Indexable<T> fromList(List<T> src) {
        return new IndexableList<>(src);
    }

    /**
     * Creates indexable from array.
     *
     * @param <T> Type.
     * @param src Source.
     * @return Indexable from given array.
     */
    public static <T> Indexable<T> fromArray(T[] src) {
        return new IndexableArray<>(src);
    }

    /**
     * Indexable list.
     *
     * @param <T> Type.
     */
    private static final class IndexableList<T> implements Indexable<T> {

        private final List<T> source;

        public IndexableList(List<T> src) {
            this.source = Collections.unmodifiableList((src instanceof RandomAccess) ? src : new ArrayList<>(src));
        }

        @Override
        public T get(int index) {
            return source.get(index);
        }

        @Override
        public int size() {
            return source.size();
        }
    }

    /**
     * Indexable array.
     *
     * @param <T> Type.
     */
    private static final class IndexableArray<T> implements Indexable<T> {

        private final T[] source;

        public IndexableArray(T[] src) {
            this.source = src;
        }

        @Override
        public T get(int index) {
            return source[index];
        }

        @Override
        public int size() {
            return source.length;
        }
    }

    /**
     * Indexable for character sequence.
     */
    private static final class IndexableCharSequence implements Indexable<Character> {

        private final CharSequence source;

        public IndexableCharSequence(CharSequence seq) {
            this.source = seq;
        }

        @Override
        public Character get(int index) {
            return source.charAt(index);
        }

        @Override
        public int size() {
            return source.length();
        }
    }

    private Indexables() {
    }
}
