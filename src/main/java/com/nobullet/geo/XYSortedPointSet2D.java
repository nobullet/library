package com.nobullet.geo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A list of points where points are pre-sorted by x and y coordinates to solve nearest neighbor problem in time close
 * to O(log n).
 *
 * Operations:
 *
 * add(element) : O(n log n) - because of internal sort.<br>
 * nearestNeighbour(needle) : O(log n) on average, O(n) worst time.<br>
 * nearestNeighbourLinear(n): O(n).
 */
public class XYSortedPointSet2D {

    private static final Logger logger = Logger.getLogger(XYSortedPointSet2D.class.getName());

    List<Point2D> xSorted;
    List<Point2D> ySorted;

    public XYSortedPointSet2D(Collection<? extends Point2D> points) {
        this.xSorted = new ArrayList<>(points);
        this.ySorted = new ArrayList<>(points);
        sort();
    }

    /**
     * Searches for nearest neighbor in a smart way: finds a possible candidate index with binary search (by X and by
     * Y), and then expands search around it until it stops finding better candidates. O(log N) in average case and O(N)
     * in worst.
     *
     * @param needle Cell to search the nearest neighbor for.
     * @return Nearest neighbor or null when not points in list.
     */
    public Point2D nearestNeighbor(Point2D needle) {
        int points = xSorted.size();
        if (points <= 0) {
            return null;
        }
        if (points == 1) {
            return xSorted.get(0);
        }
        Point2D candidate;
        int insertionX = Collections.binarySearch(xSorted, needle, Point2D.xComparator()); // O (log n)
        if (insertionX >= 0) {
            candidate = xSorted.get(insertionX); // Return exact match.
            if (needle.equals(candidate)) {
                return candidate;
            }
        }
        int insertionY = Collections.binarySearch(ySorted, needle, Point2D.yComparator()); // O (log n)
        if (insertionY >= 0) {
            candidate = ySorted.get(insertionY); // Return exact match.
            if (needle.equals(candidate)) {
                return candidate;
            }
        }
        // Insertion point : 
        //   the index of the first element greater than the key, 
        //      or 
        //   list.size() if all  elements in the list are less than the specified key

        int ixDec = insertionX, ixInc = insertionX, iyDec = insertionY, iyInc = insertionY;
        // Figure out neighbourhood indices.
        if (insertionX < 0) {
            insertionX = -(insertionX + 1); // Turn to positive.
            ixDec = insertionX >= 1 ? insertionX - 1 : 0; // Check if less than zero.
            ixInc = insertionX >= points ? points - 1 : insertionX; // Check if more than size.
        }

        if (insertionY < 0) {
            insertionY = -(insertionY + 1); // Turn to positive.
            iyDec = insertionY >= 1 ? insertionY - 1 : 0;
            iyInc = insertionY >= points ? points - 1 : insertionY;
        }

        Point2D bestNeighbor = null;
        double bestSquareDistance = Double.MAX_VALUE;
        double bestDistance = bestSquareDistance;
        double candidateSquareDistance;
        boolean ixDecFinished = false, ixIncFinished = false, iyDecFinished = false, iyIncFinished = false;

        // Until all sides reached border square.
        while (!(ixDecFinished && ixIncFinished && iyDecFinished && iyIncFinished)) {
            if (!ixDecFinished) {
                candidate = xSorted.get(ixDec--);
                if (Math.abs(candidate.getX() - needle.getX()) >= bestDistance) {
                    ixDecFinished = true;
                } else {
                    candidateSquareDistance = needle.squareDistanceTo(candidate);
                    if (candidateSquareDistance < bestSquareDistance) {
                        bestNeighbor = candidate;
                        bestSquareDistance = candidateSquareDistance;
                        bestDistance = Math.sqrt(bestSquareDistance);
                    }
                }
                if (ixDec < 0) {
                    ixDecFinished = true;
                }
            }
            if (!ixIncFinished) {
                candidate = xSorted.get(ixInc++);
                if (Math.abs(candidate.getX() - needle.getX()) >= bestDistance) {
                    ixIncFinished = true;
                } else {
                    candidateSquareDistance = needle.squareDistanceTo(candidate);
                    if (candidateSquareDistance < bestSquareDistance) {
                        bestNeighbor = candidate;
                        bestSquareDistance = candidateSquareDistance;
                        bestDistance = Math.sqrt(bestSquareDistance);
                    }
                }
                if (ixInc >= points) {
                    ixIncFinished = true;
                }
            }

            if (!iyDecFinished) {
                candidate = ySorted.get(iyDec--);
                if (Math.abs(candidate.getY() - needle.getY()) >= bestDistance) {
                    iyDecFinished = true;
                } else {
                    candidateSquareDistance = needle.squareDistanceTo(candidate);
                    if (candidateSquareDistance < bestSquareDistance) {
                        bestNeighbor = candidate;
                        bestSquareDistance = candidateSquareDistance;
                        bestDistance = Math.sqrt(bestSquareDistance);
                    }
                }
                if (iyDec < 0) {
                    iyDecFinished = true;
                }
            }

            if (!iyIncFinished) {
                candidate = ySorted.get(iyInc++);
                if (Math.abs(candidate.getY() - needle.getY()) >= bestDistance) {
                    iyIncFinished = true;
                } else {
                    candidateSquareDistance = needle.squareDistanceTo(candidate);
                    if (candidateSquareDistance < bestSquareDistance) {
                        bestNeighbor = candidate;
                        bestSquareDistance = candidateSquareDistance;
                        bestDistance = Math.sqrt(bestSquareDistance);
                    }
                }
                if (iyInc >= points) {
                    iyIncFinished = true;
                }
            }
        }

        return bestNeighbor;
    }

    /**
     * Searches for nearest neighbor in linear way.
     *
     * @param needle Cell to search the nearest neighbor for.
     * @return Nearest neighbor or null when not points in list.
     */
    public Point2D nearestNeighborLinear(Point2D needle) {
        if (xSorted.isEmpty()) {
            return null;
        }

        Iterator<Point2D> it = xSorted.iterator();
        Point2D nn = it.next(); // Nearest neighbor.
        double nnDistance = nn.squareDistanceTo(needle);
        Point2D nnCandidate;
        double nnCandidateDistance;

        while (it.hasNext()) {
            nnCandidate = it.next();
            nnCandidateDistance = nnCandidate.squareDistanceTo(needle);
            if (nnCandidate.squareDistanceTo(needle) < nnDistance) {
                nn = nnCandidate;
                nnDistance = nnCandidateDistance;
            }
        }
        return nn;
    }

    private void sort() {
        Collections.sort(xSorted, Point2D.xComparator());
        Collections.sort(ySorted, Point2D.yComparator());
    }
}
