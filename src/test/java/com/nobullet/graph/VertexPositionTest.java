package com.nobullet.graph;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for vertex position.
 */
public class VertexPositionTest {

    static final double DELTA = 0.00000000001D;
    static final double EARTH_RADIUS = 6371;
    static final Vertex.Position POS1 = Vertex.Position.newEarthGeographic(1.9483572D, 48.7931459D);
    static final Vertex.Position POS2 = Vertex.Position.newEarthGeographic(2.2459745D, 48.827167D);

    public double getDistanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    @Test
    public void testEarthPosition() {
        double expected = getDistanceBetween(48.7931459D, 1.9483572D, 48.827167D, 2.2459745D);
        double result = POS1.distanceTo(POS2).get();
        assertEquals(expected, result, DELTA);
        assertEquals(22.119818582759333D, result, DELTA);
    }
}
