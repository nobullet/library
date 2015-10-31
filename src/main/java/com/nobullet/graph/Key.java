package com.nobullet.graph;

/**
 * Key for vertex in graph. Decouples a vertex in graph from its internal representation. Pair of keys defines an edge.
 */
public interface Key {

    public static Key of(String key) {
        return new StringKey(key);
    }

    public static Key of(Number key) {
        return new NumberKey(key);
    }

    public static Key of(short key) {
        return new NumberKey(key);
    }

    public static Key of(byte key) {
        return new NumberKey(key);
    }

    public static Key of(double key) {
        return new NumberKey(key);
    }

    public static Key of(long key) {
        return new NumberKey(key);
    }

    public static Key of(int key) {
        return new NumberKey(key);
    }
}

/**
 * Key defined by number.
 */
class NumberKey implements Key {

    final Number key;

    public NumberKey(Number key) {
        if (key == null) {
            throw new NullPointerException("Number value is expected.");
        }
        this.key = key;
    }

    @Override
    public String toString() {
        return key.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NumberKey other = (NumberKey) obj;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}

/**
 * Key defined by string.
 */
class StringKey implements Key {

    final String key;

    public StringKey(String key) {
        if (key == null) {
            throw new NullPointerException("String value is expected.");
        }
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StringKey other = (StringKey) obj;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
