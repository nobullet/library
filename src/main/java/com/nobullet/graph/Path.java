package com.nobullet.graph;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Path in graph.
 */
public final class Path {

    final Key from;
    final Key to;
    final List<Key> path;
    final double cost;

    public Path(Key from, Key to, List<Key> path, double cost) {
        if (from == null || to == null || path == null) {
            throw new NullPointerException();
        }
        this.from = from;
        this.to = to;
        this.path = Collections.unmodifiableList(path);
        this.cost = cost;
    }

    public Key getFrom() {
        return from;
    }

    public Key getTo() {
        return to;
    }

    public List<Key> getPath() {
        return path;
    }

    public double getCost() {
        return cost;
    }
    
    public boolean isEmpty() {
        return path.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.from);
        hash = 29 * hash + Objects.hashCode(this.to);
        hash = 29 * hash + Objects.hashCode(this.path);
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.cost) ^ (Double.doubleToLongBits(this.cost) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Path other = (Path) obj;
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        if (Double.doubleToLongBits(this.cost) != Double.doubleToLongBits(other.cost)) {
            return false;
        }
        if (this.path.size() != other.path.size()) {
            return false;
        }
        Iterator<Key> it1 = this.path.iterator();
        Iterator<Key> it2 = other.path.iterator();
        while (it1.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                return false;
            }
        }
        return true;
    }
}
