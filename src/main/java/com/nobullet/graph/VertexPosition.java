package com.nobullet.graph;

import java.util.Optional;

/**
 *  Free form interface to calculate a position between self and other position.
 */
public interface VertexPosition {

    /**
     * Distance to other position.
     *
     * @param position Other position.
     * @return Distance as an {@link Optional} of double.
     */
    default Optional<Double> distanceTo(VertexPosition position) {
        return Optional.empty();
    }

    /**
     * Builds two dimensional position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Two dimensional position.
     */
    public static VertexPosition new2D(double x, double y) {
        return new TwoDimensionalPosition(x, y);
    }

    /**
     * Builds Earth geometric position.
     *
     * @param lon Longitude.
     * @param lat Latitude.
     * @return Earth geometric position.
     */
    public static VertexPosition newEarthGeographic(double lon, double lat) {
        return new EarthGeographicPosition(lon, lat);
    }
}

/**
 * Two dimensional position.
 */
class TwoDimensionalPosition implements VertexPosition, Cloneable {

    final double x;
    final double y;

    public TwoDimensionalPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public TwoDimensionalPosition(TwoDimensionalPosition source) {
        this(source.x, source.y);
    }

    /**
     * Calculates distance between two positions.
     *
     * @param position Other position.
     * @return Distance between two positions.
     */
    @Override
    public Optional<Double> distanceTo(VertexPosition position) {
        if (getClass() != position.getClass()) {
            return Optional.empty();
        }
        TwoDimensionalPosition other = (TwoDimensionalPosition) position;
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Optional.of(Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TwoDimensionalPosition other = (TwoDimensionalPosition) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    /**
     * Clones the position. Uses copy constructor.
     *
     * @return Copy of the object.
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new TwoDimensionalPosition(this);
    }
}

/**
 * Earth geographic position.
 */
class EarthGeographicPosition implements VertexPosition {

    static final double RADIUS = 6371.0d;
    final double lon;
    final double lat;

    public EarthGeographicPosition(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public EarthGeographicPosition(EarthGeographicPosition source) {
        this(source.lon, source.lat);
    }

    /**
     * Calculates distance between two positions in Kilometers.
     *
     * @param position Other position.
     * @return Distance between two positions in Kilometers.
     */
    @Override
    public Optional<Double> distanceTo(VertexPosition position) {
        if (getClass() != position.getClass()) {
            return Optional.empty();
        }
        EarthGeographicPosition other = (EarthGeographicPosition) position;
        double sinDlat = Math.sin(Math.toRadians(this.lat - other.lat) / 2);
        double sinDlon = Math.sin(Math.toRadians(this.lon - other.lon) / 2);
        double a = sinDlat * sinDlat
                + Math.cos(Math.toRadians(other.lat)) * Math.cos(Math.toRadians(this.lat)) * sinDlon * sinDlon;
        double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Optional.of(angle * RADIUS);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EarthGeographicPosition other = (EarthGeographicPosition) obj;
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        return Double.doubleToLongBits(this.lat) == Double.doubleToLongBits(other.lat);
    }

    /**
     * Clones the position. Uses copy constructor.
     *
     * @return Copy of the object.
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new EarthGeographicPosition(this);
    }
}
