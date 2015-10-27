package com.nobullet.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Graph vertex. Not thread safe.
 */
public class Vertex {

    final String key;
    final Map<Vertex, Edge> adjacent;
    final Map<Vertex, Edge> adjacentUnmodifieble;
    Optional<Object> data;
    Optional<Position> position;

    /**
     * Constructs vertex.
     *
     * @param key Vertex unique key.
     */
    public Vertex(String key) {
        this(key, null, null, Collections.emptyMap());
    }

    /**
     * Constructs vertex with given data.
     *
     * @param key Vertex unique key.
     * @param data Vertex data.
     */
    public Vertex(String key, Object data) {
        this(key, null, data, Collections.emptyMap());
    }

    /**
     * Constructs vertex with given data.
     *
     * @param key Vertex unique key.
     * @param position Position.
     * @param data Vertex data.
     */
    public Vertex(String key, Position position, Object data) {
        this(key, position, data, Collections.emptyMap());
    }

    /**
     * Constructs vertex with given data.
     *
     * @param key Vertex unique key.
     * @param data Vertex data.
     * @param adjacent Adjacent edges.
     */
    Vertex(String key, Position position, Object data, Map<Vertex, Edge> adjacent) {
        this.key = key;
        this.adjacent = new HashMap<>(adjacent);
        this.adjacentUnmodifieble = Collections.unmodifiableMap(this.adjacent);
        this.position = Optional.ofNullable(position);
        this.data = Optional.ofNullable(data);
    }

    public Collection<Edge> getOutgoingEdges() {
        return adjacentUnmodifieble.values();
    }

    public int getOutgoingEdgesNumber() {
        return adjacent.size();
    }

    public Set<Vertex> getAdjacentVertices() {
        return adjacentUnmodifieble.keySet();
    }

    public Edge removeAdjacent(Vertex to) {
        return this.adjacent.remove(to);
    }

    public boolean removeEdge(Edge edge) {
        return this.adjacent.remove(edge.getTo()) != null;
    }

    public boolean hasEdge(Vertex to) {
        return adjacent.containsKey(to);
    }

    public Edge getEdge(Vertex to) {
        return adjacent.get(to);
    }

    public Edge addEdge(Vertex to, double cost) {
        if (this.equals(to)) {
            return null;
        }
        Edge edge = this.adjacent.get(to);
        if (edge == null) {
            edge = new Edge(this, to, cost);
            this.adjacent.put(to, edge);
        }
        edge.setCost(cost);
        return edge;
    }

    public String getKey() {
        return key;
    }

    public Optional<Position> getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = Optional.ofNullable(position);
    }

    public Optional<Object> getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = Optional.ofNullable(data);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        return Objects.equals(this.key, other.key);
    }

    /**
     * Clears the vertex.
     */
    public void clear() {
        this.adjacent.clear();
        this.data = null;
    }

    /**
     * Calculates a distance to given vertex.
     *
     * @param vertex Other vertex.
     * @return Distance as an {@link Optional} of double.
     */
    public Optional<Double> distanceTo(Vertex vertex) {
        if (position.isPresent() && vertex.getPosition().isPresent()) {
            return position.get().distanceTo(vertex.getPosition().get());
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "{key:\"" + key + "\"}";
    }

    /**
     * Free form interface to calculate a position between self and other position.
     */
    public interface Position {

        /**
         * Distance to other position.
         *
         * @param position Other position.
         * @return Distance as an {@link Optional} of double.
         */
        default Optional<Double> distanceTo(Position position) {
            return Optional.empty();
        }

        /**
         * Builds two dimensional position.
         *
         * @param x X coordinate.
         * @param y Y coordinate.
         * @return Two dimensional position.
         */
        public static Position new2D(double x, double y) {
            return new TwoDimensionalPosition(x, y);
        }

        /**
         * Builds Earth geometric position.
         *
         * @param lon Longitude.
         * @param lat Latitude.
         * @return Earth geometric position.
         */
        public static Position newEarthGeographic(double lon, double lat) {
            return new EarthGeographicPosition(lon, lat);
        }
    }

    /**
     * Two dimensional position.
     */
    static class TwoDimensionalPosition implements Position {

        final double x;
        final double y;

        public TwoDimensionalPosition(double x, double y) {
            this.x = x;
            this.y = y;
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
         * Calculates distance between two positions.
         *
         * @param position Other position.
         * @return Distance between two positions.
         */
        @Override
        public Optional<Double> distanceTo(Position position) {
            if (getClass() != position.getClass()) {
                return Optional.empty();
            }
            TwoDimensionalPosition other = (TwoDimensionalPosition) position;
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Optional.of(Math.sqrt(dx * dx + dy * dy));
        }
    }

    /**
     * Earth geographic position.
     */
    static class EarthGeographicPosition implements Position {

        static final double RADIUS = 6371.0d;
        final double lon;
        final double lat;

        public EarthGeographicPosition(double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
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
            if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
                return false;
            }
            return true;
        }

        /**
         * Calculates distance between two positions in Kilometers.
         *
         * @param position Other position.
         * @return Distance between two positions in Kilometers.
         */
        @Override
        public Optional<Double> distanceTo(Position position) {
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
    }
}
