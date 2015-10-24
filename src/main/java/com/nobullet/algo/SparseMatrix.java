package com.nobullet.algo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Sparse matrix based on array of maps.
 *
 * @param <T> Type.
 */
public class SparseMatrix<T> {

    private static final int DEFAULT_SIZE = 100;
    private final T defaultValue;
    private Map<Integer, T>[] data;
    private int width;
    private int height;

    @SuppressWarnings(value = "unchecked")
    public SparseMatrix(int defaultSize, T defaultValue) {
        this.defaultValue = defaultValue;
        this.data = new HashMap[defaultSize];
    }

    public SparseMatrix(T defaultValue) {
        this(DEFAULT_SIZE, defaultValue);
    }

    public T get(int x, int y) {
        check(x, y);
        if (x >= this.data.length) {
            return defaultValue;
        }
        Map<Integer, T> row = this.data[x];
        if (row == null) {
            return defaultValue;
        }
        return row.getOrDefault(y, defaultValue);
    }

    public SparseMatrix<T> set(int x, int y, T value) {
        check(x, y);
        resizeIfNeeded(x);
        Map<Integer, T> row = this.data[x];
        if (row == null && value != null && value.equals(defaultValue)) {
            return this;
        }
        if (row == null) {
            row = this.data[x] = new HashMap<>();
        }
        T existing = row.get(y);
        if (existing == null && value != null && value.equals(defaultValue)) {
            return this;
        }
        row.put(y, value);
        this.width = Math.max(this.width, x + 1);
        this.height = Math.max(this.height, y + 1);
        return this;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                T existing = get(x, y);
                String str = String.valueOf(existing);
                if (str.length() == 1) {
                    sb.append(" ");
                }
                sb.append(str);
                if (x != getWidth() - 1) {
                    sb.append("|");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void check(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IndexOutOfBoundsException("Indices must be >= 0: " + x + "/" + y);
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void resizeIfNeeded(int x) {
        if (x >= this.data.length) {
            Map<Integer, T>[] row = Arrays.copyOf(this.data, (this.data.length * 3) / 2);
            Map<Integer, T>[] old = this.data;
            this.data = row;
            // Clean up.
            Arrays.fill(old, null);
        }
    }
}
