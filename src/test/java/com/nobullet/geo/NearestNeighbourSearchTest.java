package com.nobullet.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.nobullet.Benchmarks;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for nearest neighbor (NN) data structures: XYSet and Quad Tree. Compares it to simple linear search.
 */
public class NearestNeighbourSearchTest {

    static final Logger logger = Logger.getLogger(NearestNeighbourSearchTest.class.getName());
    static final double MAX_COORDINATE_VALUE = 99.9D;
    static final double DUPLICATES_PORTION = 0.01D;

    @Test
    public void testXYSortedPointSet2D() {
        List<Point2D> points = new LinkedList<>();
        for (double x = 0.0D; x < 1000.0D; x += 1.0D) {
            points.add(new Point2D(x, x));
        }
        // Add some duplicates.
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));

        XYSortedPointSet2D set = new XYSortedPointSet2D(points);

        assertThatXYSSFindsCorrectNN("Points are the same: ", new Point2D(49.7D, 50.2D), set);
        assertThatXYSSFindsCorrectNN("Points are the same (exact match): ", new Point2D(50.0D, 50.0D), set);
        assertThatXYSSFindsCorrectNN("Points are exact match of duplicate: ", new Point2D(49.0D, 49.0D), set);
        assertThatXYSSFindsCorrectNN("Points are the same (exact match by X): ", new Point2D(50.0D, 50.5D), set);
        assertThatXYSSFindsCorrectNN("Points are the same (exact match by Y): ", new Point2D(49.5D, 49.0D), set);
    }

    @Test
    public void testXYSortedPointSet2DCornerCase() {
        List<Point2D> points = new LinkedList<>();
        for (double x = 0.0D; x < 1000.0D; x += 1.0D) {
            points.add(new Point2D(x, 0.0D));
        }

        assertThatXYSSFindsCorrectNN("Points are the same: ", new Point2D(50.0D, 0.1D), new XYSortedPointSet2D(points));
    }

    @Test
    public void testQuadTreeBoundingBox() {
        // Bounding box tests.
        QuadTree.BoundingBox bb1 = new QuadTree.BoundingBox(new Point2D(101.71D, 100.07D), 50.50D);
        QuadTree.BoundingBox bb2 = new QuadTree.BoundingBox(new Point2D(92.97D, 25.67D), 69.57D);
        // bb1: (51.21, 49.57)  -> (152.21, 150.57)
        // bb2: (23.40, -43.89) -> (162.54, 95.42)
        assertTrue(bb1.intersects(bb2));
        assertTrue(bb2.intersects(bb1));

        QuadTree.BoundingBox bb3 = new QuadTree.BoundingBox(new Point2D(-102.76D, 202.63D), 50.90D);
        QuadTree.BoundingBox bb4 = new QuadTree.BoundingBox(new Point2D(50.14D, 49.01D), 2.40D);

        assertFalse(bb3.intersects(bb4));
        assertFalse(bb4.intersects(bb3));

        double size = 0.000000000000005606626274357041D;
        Point2D center = new Point2D(49.0D, 49.0D);
        QuadTree.BoundingBox bb = new QuadTree.BoundingBox(center, size);
        assertTrue("Bounding box must contain itself.", bb.contains(bb.getCenter()));
    }

    @Test
    public void testQuadTreeStructure() {
        List<Point2D> points = new LinkedList<>();
        points.add(new Point2D(1.0D, 1.0D));
        points.add(new Point2D(-0.5D, 0.5D));
        points.add(new Point2D(-2.0D, -2.0D));
        points.add(new Point2D(4.0D, -3.0D));

        QuadTree quadTree = new QuadTree(points);
        assertEquals(new Point2D(1.0D, -1.0D), quadTree.root.getBoundary().getCenter());
        assertEquals(32.0D, quadTree.root.getBoundary().getSize(), 0.0D);
        assertNull(quadTree.root.northWest);
        assertNull(quadTree.root.northEast);
        assertNull(quadTree.root.southWest);
        assertNull(quadTree.root.southEast);

        // New level.
        points.add(new Point2D(2.0D, -2.0D)); // SE point
        quadTree = new QuadTree(points);
        assertEquals(new Point2D(1.0D, -1.0D), quadTree.root.getBoundary().getCenter());
        assertEquals(32.0D, quadTree.root.getBoundary().getSize(), 0.0D);
        assertEquals(1, quadTree.root.northWest.points.size());
        assertEquals(1, quadTree.root.northEast.points.size());
        assertEquals(1, quadTree.root.southWest.points.size());
        assertEquals(2, quadTree.root.southEast.points.size());

        assertEquals(new Point2D(-15.0D, 15.0D), quadTree.root.northWest.getBoundary().getCenter());
        assertEquals(16.0D, quadTree.root.northWest.getBoundary().getSize(), 0.0D);

        assertEquals(new Point2D(17.0D, 15.0D), quadTree.root.northEast.getBoundary().getCenter());
        assertEquals(16.0D, quadTree.root.northEast.getBoundary().getSize(), 0.0D);

        assertEquals(new Point2D(-15.0D, -17.0D), quadTree.root.southWest.getBoundary().getCenter());
        assertEquals(16.0D, quadTree.root.southWest.getBoundary().getSize(), 0.0D);

        assertEquals(new Point2D(17.0D, -17.0D), quadTree.root.southEast.getBoundary().getCenter());
        assertEquals(16.0D, quadTree.root.southEast.getBoundary().getSize(), 0.0D);

        List<Point2D> queryResult = quadTree.queryRange(2.1D, -1.9D, 0.2D);
        assertEquals(1, queryResult.size());
        assertEquals(new Point2D(2.0D, -2.0D), queryResult.get(0));

        queryResult = quadTree.queryRange(0, 0, 0.2D);
        assertEquals(0, queryResult.size());

        queryResult = quadTree.queryRange(0, 0, 0.25D);
        assertEquals(0, queryResult.size());

        queryResult = quadTree.queryRange(0, 0, 0.71D);
        assertEquals(1, queryResult.size());
        assertEquals(new Point2D(-0.5D, 0.5D), queryResult.get(0));

        queryResult = quadTree.queryRange(0, 0, 20D);
        assertEquals(5, queryResult.size());
    }

    @Test
    public void testQuadTree() {
        List<Point2D> points = new LinkedList<>();
        // Add some duplicates.
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));

        // Diagonal points.
        for (double x = 0.0D; x < 100.0D; x += 1.0D) {
            points.add(new Point2D(x, x));
        }

        // Add some duplicates.
        points.add(new Point2D(49.0D, 49.0D));
        points.add(new Point2D(49.0D, 49.0D));

        XYSortedPointSet2D set = new XYSortedPointSet2D(points);
        QuadTree quadTree = new QuadTree(points);

        assertThatXYSSAndQTFindsCorrectNN("Points are the same: ", new Point2D(49.7D, 50.2D), set, quadTree);
        assertThatXYSSAndQTFindsCorrectNN("Points are the exact same: ", new Point2D(50.0D, 50.0D), set, quadTree);
        assertThatXYSSAndQTFindsCorrectNN("Exact match of duplicate: ", new Point2D(49.0D, 49.0D), set, quadTree);
        assertThatXYSSAndQTFindsCorrectNN("Exact match by X): ", new Point2D(50.0D, 50.5D), set, quadTree);
        assertThatXYSSAndQTFindsCorrectNN("Exact match by Y: ", new Point2D(49.5D, 49.0D), set, quadTree);
    }

    @Test
    public void testRunBenchmarks() {
        Benchmarks bms = new Benchmarks();

        benchmark(1_000_000, 20, bms);

        logger.info(bms.getStatistics().toString(5));
    }

    @Test
    @Ignore
    public void testRunBigBenchmarks() {
        Benchmarks bms = new Benchmarks();

        benchmark(10_000_000, 20, bms);

        logger.info(bms.getStatistics().toString(5));
    }

    /**
     * Benchmarks linear, XYSet and Quad Tree nearest neighbor searches.
     *
     * @param pointsToTest Number of points to create.
     * @param timesToTest Number of searches to perform.
     * @param benchmarks {@link Benchmarks} object to measure execution times.
     */
    private void benchmark(int pointsToTest, int timesToTest, Benchmarks benchmarks) {
        List<Point2D> points = new ArrayList<>(pointsToTest);
        for (int i = 0; i < pointsToTest; i++) {
            points.add(newRandomPoint());
        }
        int duplicatePoints = (int) (pointsToTest * DUPLICATES_PORTION);
        for (int i = 0; i < duplicatePoints; i++) {
            points.add(new Point2D(MAX_COORDINATE_VALUE / 2.0D, MAX_COORDINATE_VALUE / 2.0D));
        }

        XYSortedPointSet2D set = new XYSortedPointSet2D(points);
        QuadTree quadTree = new QuadTree(points);

        for (int i = 0; i < timesToTest; i++) {
            Point2D needle = newRandomPoint();
            Point2D linearNN = benchmarks.benchmark("Lin_" + pointsToTest, () -> set.nearestNeighborLinear(needle));
            Point2D xySetNN = benchmarks.benchmark("XYS_" + pointsToTest, () -> set.nearestNeighbor(needle));
            Point2D quadTreeNN = benchmarks.benchmark("QT_" + pointsToTest, () -> quadTree.nearestNeighbor(needle));

            assertEquals(linearNN, xySetNN);
            assertEquals(linearNN, quadTreeNN);
        }
        points.clear();
    }

    /**
     * Returns new random point.
     * @return Random point.
     */
    static final Point2D newRandomPoint() {
        return new Point2D(Math.random() * MAX_COORDINATE_VALUE, Math.random() * MAX_COORDINATE_VALUE);
    }

    /**
     * Asserts that {@link XYSortedPointSet2D} finds same nearest neighbor as linear search.
     *
     * @param message Message.
     * @param needle Cell to search.
     * @param set XYSortedPointSet2D.
     */
    static void assertThatXYSSFindsCorrectNN(String message, Point2D needle, XYSortedPointSet2D set) {
        assertThatXYSSAndQTFindsCorrectNN(message, needle, set, null);
    }

    /**
     * Asserts that {@link XYSortedPointSet2D} and {@link QuadTree} find same nearest neighbor as linear search.
     *
     * @param message Message.
     * @param needle Cell to search.
     * @param set XYSortedPointSet2D.
     * @param quadTree Quad tree.
     */
    static void assertThatXYSSAndQTFindsCorrectNN(String message, Point2D needle, XYSortedPointSet2D set,
            QuadTree quadTree) {
        Point2D linearNN = set.nearestNeighborLinear(needle);
        Point2D nN = set.nearestNeighbor(needle);
        assertEquals(message, linearNN, nN);
        if (quadTree != null) {
            Point2D quadTreeNN = quadTree.nearestNeighbor(needle);
            assertEquals(message, linearNN, quadTreeNN);
        }
    }
}
