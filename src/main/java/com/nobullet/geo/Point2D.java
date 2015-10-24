package com.nobullet.geo;

import java.util.Comparator;

/**
 * 2-D point. Immutable.
 */
public class Point2D {

    private static final Comparator<? super Point2D> X_COMPARATOR
            = (Point2D o1, Point2D o2) -> Double.compare(o1.getX(), o2.getX());

    private static final Comparator<? super Point2D> Y_COMPARATOR
            = (Point2D o1, Point2D o2) -> Double.compare(o1.getY(), o2.getY());

    final double x;
    final double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Returns midpoint between current (this) point and given point.
     *
     * @param other Other point.
     * @return Midpoint for the segment.
     */
    public Point2D midpointOf(Point2D other) {
        if (other == null) {
            throw new NullPointerException("Cell expected.");
        }
        if (equals(other)) {
            return other;
        }
        return new Point2D(this.getX() + (other.getX() - this.getX()) / 2.0D,
                this.getY() + (other.getY() - this.getY()) / 2.0D);
    }

    /**
     * Checks if the current point (this) is within the given square.
     *
     * @param center Square center.
     * @param size Size of square.
     * @return Whether the current point is within square.
     */
    public boolean isWithinSquare(Point2D center, double size) {
        return isWithinSquare(center.getX(), center.getY(), size);
    }

    /**
     * Checks if the current point (this) is within the given square.
     *
     * @param xCenter Square center, x coordinate.
     * @param yCenter Square center, y coordinate.
     * @param size Size of square.
     * @return Whether the current point is within square.
     */
    public boolean isWithinSquare(double xCenter, double yCenter, double size) {
        if (size < 0) {
            return false;
        }
        double xdiff = Math.abs(getX() - xCenter);
        double ydiff = Math.abs(getY() - yCenter);
        return (xdiff < size || xdiff == size && getX() <= xCenter) // to the left
                && (ydiff < size || ydiff == size && getY() <= yCenter); // to the bottom
    }

    public double squareDistanceTo(Point2D other) {
        double dx = getX() - other.getX();
        double dy = getY() - other.getY();
        return dx * dx + dy * dy;
    }

    public double distanceTo(Point2D other) {
        return Math.sqrt(squareDistanceTo(other));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Point2D)) {
            return false;
        }
        final Point2D other = (Point2D) obj;
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(other.x)
                && Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
    }

    @Override
    public String toString() {
        return String.format("Point2D{x=%.3f, y=%.3f}", x, y);
    }

    public static Comparator<? super Point2D> xComparator() {
        return X_COMPARATOR;
    }

    public static Comparator<? super Point2D> yComparator() {
        return Y_COMPARATOR;
    }
}
