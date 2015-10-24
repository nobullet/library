package com.nobullet.geo;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Quad tree of points to solve nearest neighbor problem in time close to O(log n). Static as adding / removing elements
 * requires re-building of the tree, that takes in O(n log n) ? .
 */
public class QuadTree {

    private static final double INITIAL_QUESS_BOUNDING_BOX_SIZE = 4.0D;
    private static final double PROBING_COEFFICIENT = 1.5D;

    final QuadTreeNode root;
    private final BoundingBox treeBoundingBox;
    private int queryCalls;
    private int treeDepth;
    private int treeNodes;

    /**
     * Constructs Quad tree from given points.
     * @param points 
     */
    public QuadTree(Collection<? extends Point2D> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Quadtree expects >= 2 points");
        }
        this.treeDepth = 0;
        this.treeBoundingBox = findBoundingBoxFor(points);
        this.root = new QuadTreeNode(treeBoundingBox, this, 0);
        points.stream().forEach(this.root::add);
    }

    /**
     * Finds nearest neighbor for given point. Amortized cost time: O(log N).
     *
     * @param needle Needle.
     * @return Nearest neighbor for given point.
     */
    public Point2D nearestNeighbor(Point2D needle) {
        QuadTreeNode smallestNode = root.findSmallestNodeContaining(needle);
        if (smallestNode == null || smallestNode.points == null) {
            smallestNode = root;
        }
        Point2D closestCandidate;
        if (!smallestNode.points.isEmpty()) {
            closestCandidate = closestLinear(needle, smallestNode.points);
        } else {
            List<Point2D> probingCandidates;
            double probingSize = smallestNode.getBoundary().getSize() * PROBING_COEFFICIENT;
            do {
                probingCandidates = queryRange(needle, probingSize);
                probingSize *= PROBING_COEFFICIENT;
            } while (probingCandidates.isEmpty());
            closestCandidate = closestLinear(needle, probingCandidates);
        }
        if (closestCandidate == null) {
            throw new IllegalStateException("Expected at least one point!");
        }
        double distance = needle.distanceTo(closestCandidate);
        Point2D result;
        if (distance == 0.0D) {
            result = closestCandidate;
        } else {
            result = closestLinear(needle, queryRange(needle, distance));
        }
        return result;
    }

    public List<Point2D> queryRange(Point2D needle, double size) {
        return queryRange(new BoundingBox(needle, size));
    }

    public List<Point2D> queryRange(double xCenter, double yCenter, double size) {
        return queryRange(new Point2D(xCenter, yCenter), size);
    }

    public List<Point2D> queryRange(BoundingBox range) {
        List<Point2D> result = new LinkedList<>();
        root.queryRange(range, result);
        return result;
    }

    @Override
    public String toString() {
        return "QuadTree{" + "treeBoundingBox=" + treeBoundingBox + ", queryCalls=" + queryCalls
                + ", treeDepth=" + treeDepth + ", treeNodes=" + treeNodes + '}';
    }

    private void incrementNodes() {
        treeNodes++;
    }

    private void incrementQueries() {
        queryCalls++;
    }

    private void setMaxDepth(int depth) {
        treeDepth = Math.max(depth, treeDepth);
    }

    /**
     * Finds closest point from collection in linear time.
     *
     * @param needle Needle to search closest point to.
     * @param points Points to search in.
     * @return Closest point to needle.
     */
    private static Point2D closestLinear(Point2D needle, Collection<? extends Point2D> points) {
        double distance = Double.MAX_VALUE;
        double candidate;
        Point2D result = null;
        for (Point2D p : points) {
            candidate = p.squareDistanceTo(needle);
            if (candidate < distance) {
                distance = candidate;
                result = p;
            }
        }
        return result;
    }

    /**
     * Finds bounding box for given collection of points.
     *
     * @param points Points.
     * @return Bounding box that contains all points.
     */
    private static BoundingBox findBoundingBoxFor(Collection<? extends Point2D> points) {
        Point2D point = points.iterator().next();
        double diffx;
        double diffy;
        double left = point.getX();
        double right = point.getX();
        double top = point.getY();
        double bottom = point.getY();
        for (Point2D p : points) {
            if (left > p.getX()) {
                left = p.getX();
            }
            if (right < p.getX()) {
                right = p.getX();
            }
            if (top < p.getY()) {
                top = p.getY();
            }
            if (bottom > p.getY()) {
                bottom = p.getY();
            }
        }
        left--;
        right++;
        top++;
        bottom--;
        diffx = right - left;
        diffy = top - bottom;
        Point2D center = new Point2D(left + diffx / 2.0D, bottom + diffy / 2.0D);
        return new BoundingBox(center, INITIAL_QUESS_BOUNDING_BOX_SIZE * Math.max(Math.abs(diffx), Math.abs(diffy)));
    }

    /**
     * Quad tree node.
     * Visible for testing.
     */
    static class QuadTreeNode {

        private static final int NODE_CAPACITY = 4;

        private BoundingBox boundary;
        // To handle duplicates use map in quad tree for all QuadTree nodes and map duplicates to list of duplicate values.
        // Modify NN() and query() to return a list of values with duplicates.
        Set<Point2D> points;

        QuadTreeNode northWest;
        QuadTreeNode northEast;
        QuadTreeNode southWest;
        QuadTreeNode southEast;

        private int level;
        private QuadTree tree;

        public QuadTreeNode(BoundingBox boundary, QuadTree treeRef, int level) {
            if (boundary == null) {
                throw new NullPointerException("Expected to have boundary.");
            }
            this.tree = treeRef;
            this.boundary = boundary;
            this.points = new LinkedHashSet<>();
            this.level = level;
            this.tree.setMaxDepth(level);
            this.tree.incrementNodes();
        }

        public BoundingBox getBoundary() {
            return boundary;
        }

        public QuadTreeNode findSmallestNodeContaining(Point2D needle) {
            // Ignore objects that do not belong in this quad tree
            if (!boundary.contains(needle)) {
                return null;
            }
            if (northWest == null) {
                return this;
            }

            // Some of these nodes must have a point. If not - return self (self contains points).
            QuadTreeNode candidate = northWest.findSmallestNodeContaining(needle);
            if (candidate != null) {
                return candidate;
            }
            candidate = northEast.findSmallestNodeContaining(needle);
            if (candidate != null) {
                return candidate;
            }
            candidate = southWest.findSmallestNodeContaining(needle);
            if (candidate != null) {
                return candidate;
            }
            candidate = southEast.findSmallestNodeContaining(needle);
            if (candidate != null) {
                return candidate;
            }
            return this; // Candidates are empty - return self.
        }

        /**
         * Collects all points that fall into the given bounding box.
         *
         * @param boundingBox Bounding box.
         * @param result Result list to collect points.
         */
        public void queryRange(BoundingBox boundingBox, List<Point2D> result) {
            tree.incrementQueries();
            if (!this.boundary.intersects(boundingBox)) {
                return;
            }

            if (points != null) {
                points.stream().filter(p -> boundingBox.contains(p)).forEach(result::add);
            }

            // Terminate here, if there are no children
            if (northWest == null) {
                return;
            }

            northWest.queryRange(boundingBox, result);
            northEast.queryRange(boundingBox, result);
            southWest.queryRange(boundingBox, result);
            southEast.queryRange(boundingBox, result);
        }

        /**
         * Returns points count for this quad.
         *
         * @return Points count.
         */
        public int getPointsCount() {
            return points != null ? points.size() : 0;
        }

        @Override
        public String toString() {
            return "QTNode{" + "boundary=" + boundary + " level=" + level + " pts=" + getPointsCount() + '}';
        }

        /**
         * Adds a point to QuadTree.
         *
         * @param point Cell to add.
         * @return Whether the point was added.
         */
        public boolean add(Point2D point) {
            // Ignore objects that do not belong in this quad tree
            if (!boundary.contains(point)) {
                return false;
            }

            // If there is space in this quad tree, add the object here
            if (points != null && points.size() < NODE_CAPACITY) {
                return points.add(point);
            }

            // Otherwise, subdivide and then add the point to whichever node will accept it
            if (northWest == null) {
                subdivide();
            }

            if (northWest.add(point)) {
                return true;
            }
            if (northEast.add(point)) {
                return true;
            }
            if (southWest.add(point)) {
                return true;
            }
            return southEast.add(point);
        }

        /**
         * Creates four children that fully divide this quad into four squares of equal area.
         */
        private void subdivide() {
            double newSize = boundary.getSize() / 2.0D;
            Point2D centerPoint = boundary.getCenter();
            if (newSize == 0.0D) {
                throw new IllegalStateException("Size of the new boundary should never be 0.");
            }
            northWest = new QuadTreeNode(new BoundingBox(
                    new Point2D(centerPoint.getX() - newSize, centerPoint.getY() + newSize), newSize), tree, level + 1);
            northEast = new QuadTreeNode(new BoundingBox(
                    new Point2D(centerPoint.getX() + newSize, centerPoint.getY() + newSize), newSize), tree, level + 1);
            southWest = new QuadTreeNode(new BoundingBox(
                    new Point2D(centerPoint.getX() - newSize, centerPoint.getY() - newSize), newSize), tree, level + 1);
            southEast = new QuadTreeNode(new BoundingBox(
                    new Point2D(centerPoint.getX() + newSize, centerPoint.getY() - newSize), newSize), tree, level + 1);

            for (Point2D p : points) {
                if (northWest.add(p) || northEast.add(p) || southWest.add(p) || southEast.add(p));
            }
            // Clear and release point set.
            points.clear();
            points = null;
        }
    }

    /**
     * Boundary for QuadTree.
     */
    public static class BoundingBox {

        final Point2D center;
        final double size;

        public BoundingBox(Point2D center, double size) {
            if (center == null) {
                throw new NullPointerException();
            }
            if (size < 0) {
                throw new IllegalArgumentException("Size is expected to be bigger than 0");
            }
            this.center = center;
            this.size = size;
        }

        public Point2D getCenter() {
            return center;
        }

        public double getSize() {
            return size;
        }

        public boolean contains(Point2D point) {
            return contains(point.getX(), point.getY());
        }

        public boolean contains(double x, double y) {
            return center.getX() - size <= x && x < center.getX() + size
                    && center.getY() - size <= y && y < center.getY() + size
                    || center.getX() == x && center.getY() == y;
        }

        public static boolean contains(double x, double y, BoundingBox boundary) {
            Point2D center = boundary.getCenter();
            return center.getX() - boundary.getSize() <= x && x < center.getX() + boundary.getSize()
                    && center.getY() - boundary.getSize() <= y && y < center.getY() + boundary.getSize()
                    || center.getX() == x && center.getY() == y;
        }

        public static boolean contains(Point2D point, BoundingBox bb) {
            Point2D center = bb.getCenter();
            return center.getX() - bb.getSize() <= point.getX() && point.getX() < center.getX() + bb.getSize()
                    && center.getY() - bb.getSize() <= point.getY() && point.getY() < center.getY() + bb.getSize()
                    || center.equals(point);
        }

        public static boolean contains(double x, double y, double xCenter, double yCenter, double size) {
            return xCenter - size <= x && x < xCenter + size && yCenter - size <= y && y < yCenter + size;
        }

        public static boolean contains(Point2D point, double xCenter, double yCenter, double size) {
            return xCenter - size <= point.getX() && point.getX() < xCenter + size
                    && yCenter - size <= point.getY() && point.getY() < yCenter + size
                    || point.getX() == xCenter && point.getY() == yCenter;
        }

        /**
         * Checks if current bounding box intersects with given one.
         *
         * @param boundary Boundary.
         * @return Whether current bounding box intersects with given one.
         */
        public boolean intersects(BoundingBox boundary) {
            Point2D otherCenter = boundary.getCenter();
            return contains(otherCenter.getX() - boundary.getSize(), otherCenter.getY() + boundary.getSize())
                    || contains(otherCenter.getX() + boundary.getSize(), otherCenter.getY() - boundary.getSize())
                    || contains(otherCenter.getX() - boundary.getSize(), otherCenter.getY() - boundary.getSize())
                    || contains(otherCenter.getX() + boundary.getSize(), otherCenter.getY() + boundary.getSize())
                    || contains(center.getX() - size, center.getY() + size, boundary)
                    || contains(center.getX() + size, center.getY() - size, boundary)
                    || contains(center.getX() - size, center.getY() - size, boundary)
                    || contains(center.getX() + size, center.getY() + size, boundary);
        }

        @Override
        public String toString() {
            return "QTBB{" + "c=" + center + String.format(", size=%.3f", size) + '}';
        }
    }
}
