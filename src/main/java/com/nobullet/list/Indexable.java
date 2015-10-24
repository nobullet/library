package com.nobullet.list;

import java.util.RandomAccess;

/**
 * Indexable collection.
 *
 * @param <T> Type.
 */
public interface Indexable<T> extends RandomAccess {

    /**
     * Returns object by index.
     * @param index Index.
     * @return Object by index.
     */
    public T get(int index);

    /**
     * Returns size of the indexable.
     * @return 
     */
    public int size();
}
