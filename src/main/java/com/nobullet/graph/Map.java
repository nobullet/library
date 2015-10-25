package com.nobullet.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents NxM matrix, treating 0 as empty space and values greater than 0 as walls.
 */
public class Map {

    static final BiFunction<Cell, Cell, Double> DIJKSTRA_HEURISTIC = (Cell next, Cell goal) -> 0.0D;
    static final BiFunction<Cell, Cell, Double> A_STAR_HEURISTIC = (Cell next, Cell goal) -> goal.distanceTo(next);
    static final Map EMPTY_MAP = new Map(0, 0);
    static final Cell NOWHERE = new Cell(-1, -1);
    final long[][] matrix;

    /**
     * Constructs matrix.
     *
     * @param w Width.
     * @param h Height.
     */
    public Map(int w, int h) {
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException("Expecting correct dimensions.");
        }
        this.matrix = new long[h][w];
    }

    /**
     * Returns width of the map.
     *
     * @return Map width.
     */
    public final int getWidth() {
        if (matrix.length == 0) {
            return 0;
        }
        return matrix[0].length;
    }

    /**
     * Returns height of the map.
     *
     * @return Map height.
     */
    public final int getHeight() {
        return matrix.length;
    }

    /**
     * Sets the value at the given coordinates (x, y). Uses absolute value.
     *
     * @param x X coord.
     * @param y Y coord.
     * @param value Value to set.
     * @return this.
     */
    public final Map set(int x, int y, long value) {
        this.matrix[y][x] = Math.abs(value);
        return this;
    }

    /**
     * Sets the value at the given cell. Uses absolute value.
     *
     * @param cell Cell.
     * @param value Value for cell.
     * @return Current map.
     */
    public final Map set(Cell cell, long value) {
        return set(cell.getX(), cell.getY(), value);
    }

    /**
     * Returns value at the given coordinates (x, y).
     *
     * @param x X coord.
     * @param y Y coord.
     * @return Value at coordinates.
     */
    public final long get(int x, int y) {
        return this.matrix[y][x];
    }

    /**
     * Returns value at the given cell.
     *
     * @param cell Cell.
     * @return Value at cell.
     */
    public final long get(Cell cell) {
        return get(cell.getX(), cell.getY());
    }

    /**
     * Finds shortest path with breadth-first search. Treats 0's as empty cells and other values as walls.
     *
     * @param source Source cell.
     * @param target Target cell.
     * @return Path from source cell to target cell.
     */
    public Path shortestPathBFS(Cell source, Cell target) {
        checkBounds(target);
        if (get(target) > 0) {
            return Path.emptyPath();
        }
        return shortestPathBFS(source, target, 0L, null);
    }

    /**
     * Finds shortest path with breadth-first search. Treats given value to explore as cells and other values as walls.
     *
     * @param source Source cell.
     * @param target Target cell.
     * @param exploreValue Value to explore.
     * @param area Area (set) to fill with path when exploring graph.
     * @return Path from source cell to target cell.
     */
    public Path shortestPathBFS(Cell source, Cell target, long exploreValue, Set<Cell> area) {
        exploreValue = Math.abs(exploreValue);
        checkBounds(source);
        if (get(source) != exploreValue) {
            return Path.emptyPath();
        }

        Deque<Cell> frontier = new LinkedList<>();
        frontier.addLast(source);

        java.util.Map<Cell, Cell> cameFrom = new HashMap<>();
        cameFrom.put(source, NOWHERE);

        boolean found = false;
        while (!frontier.isEmpty()) {
            Cell current = frontier.removeFirst();
            if (area != null) {
                area.add(current);
            }

            if (current.equals(target)) {
                found = true;
                break;
            }

            for (Cell next : neighbors(current)) {
                if (!cameFrom.containsKey(next)) {
                    long cellValue = get(next);
                    if (cellValue == exploreValue) {
                        frontier.addLast(next);
                        cameFrom.put(next, current);
                    }
                }
            }
        }
        if (!found) {
            cameFrom.clear();
            return Path.emptyPath();
        }
        List<Cell> path = new LinkedList<>();
        Cell current = target;
        while (current != null && !current.equals(source)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(source);
        Collections.reverse(path);
        cameFrom.clear();
        return new Path(this, path);
    }

    /**
     * Finds shortest path with Dijkstra's algorithm. Treats each cell as a price of getting into it.
     *
     * @param source Source cell.
     * @param target Target cell.
     * @return Path from source cell to target cell.
     */
    public Path shortestPathDijkstra(Cell source, Cell target) {
        return shortestPathTemplate(source, target, DIJKSTRA_HEURISTIC);
    }

    /**
     * Finds shortest path with A* algorithm. Treats each cell as a price of getting into it.
     *
     * @param source Source cell.
     * @param target Target cell.
     * @return Path from source cell to target cell.
     */
    public Path shortestPathAStar(Cell source, Cell target) {
        return shortestPathTemplate(source, target, A_STAR_HEURISTIC);
    }

    /**
     * Template for A* and Dijkstra algorithm. Treats each cell as a price of getting into it.
     *
     * @param source Source cell.
     * @param target Target cell.
     * @param heuristic Heuristic function to return heuristic between target and current cell. 0.0D function for
     * Dijkstra.
     * @return Path from source cell to target cell.
     */
    public final Path shortestPathTemplate(Cell source, Cell target, BiFunction<Cell, Cell, Double> heuristic) {
        checkBounds(source);
        checkBounds(target);

        PriorityQueue<CellWithPriority> frontier = new PriorityQueue<>();
        frontier.add(new CellWithPriority(source, 0.0D));

        java.util.Map<Cell, Long> costSoFar = new HashMap<>();
        costSoFar.put(source, 0L);

        java.util.Map<Cell, Cell> cameFrom = new HashMap<>();
        cameFrom.put(source, NOWHERE);

        boolean found = false;
        while (!frontier.isEmpty()) {
            Cell current = frontier.poll().getCell();

            if (current.equals(target)) {
                found = true;
                break;
            }

            for (Cell next : neighbors(current)) {
                long newCost = costSoFar.get(current) + cost(current, next);
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(new CellWithPriority(next, newCost + heuristic.apply(target, next)));
                    cameFrom.put(next, current);
                }
            }
        }
        if (!found) {
            cameFrom.clear();
            return Path.emptyPath();
        }
        List<Cell> path = new LinkedList<>();
        Cell current = target;
        while (current != null && !current.equals(source)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(source);
        Collections.reverse(path);
        cameFrom.clear();
        return new Path(this, path);
    }

    /**
     * Returns cost of traveling from one cell to another for Dijkstra's algorithm
     *
     * @param current Current cell.
     * @param next Next cell.
     * @return Absolute difference between cell values in this map.
     */
    public long cost(Cell current, Cell next) {
        return Math.abs(this.get(current) - this.get(next)) + current.manhattanDistanceTo(next);
    }

    /**
     * Returns neighbors for given cell.
     *
     * @param cell Cell to get neighbors of.
     * @return Neighbors of the given cell.
     */
    public final Collection<Cell> neighbors(Cell cell) {
        checkBounds(cell);
        List<Cell> neighbors = new ArrayList<>(4);
        if (withinBounds(cell.getX() + 1, cell.getY())) {
            neighbors.add(new Cell(cell.getX() + 1, cell.getY()));
        }
        if (withinBounds(cell.getX() - 1, cell.getY())) {
            neighbors.add(new Cell(cell.getX() - 1, cell.getY()));
        }
        if (withinBounds(cell.getX(), cell.getY() + 1)) {
            neighbors.add(new Cell(cell.getX(), cell.getY() + 1));
        }
        if (withinBounds(cell.getX(), cell.getY() - 1)) {
            neighbors.add(new Cell(cell.getX(), cell.getY() - 1));
        }
        return neighbors;
    }

    /**
     * Counts number of different planes excluding that are formed by 0's.
     *
     * @return Number of plains.
     */
    public final long getNumberOfPlains() {
        return getNumberOfPlains(Constants.EMPTY_CELL_VALUE);
    }
    
    /**
     * Counts number of different plains.
     *
     * @param ignoreValue Value to ignore. If null all values are considered.
     * @return Number of planes.
     */
    public final long getNumberOfPlains(Long ignoreValue) {
        Set<Cell> visitedCells = new HashSet<>(this.getWidth() * this.getHeight() * 2);
        int result = 0;
        for (int h = 0; h < this.getHeight(); h++) {
            for (int w = 0; w < this.getWidth(); w++) {
                Cell source = new Cell(w, h);
                if (!visitedCells.contains(source)) {
                    long sourceValue = get(source);
                    shortestPathBFS(source, null, sourceValue, visitedCells);
                    if (ignoreValue != null && sourceValue != ignoreValue) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks that given cell belongs to bounds of current map. If not, {@link  IllegalArgumentException} is thrown.
     *
     * @param cell Cell to check.
     */
    public final void checkBounds(Cell cell) {
        checkBounds(cell.getX(), cell.getY());
    }

    /**
     * Checks that given cell belongs to bounds of current map. If not, {@link  IllegalArgumentException} is thrown.
     *
     * @param x Cell X coordinate.
     * @param y Cell Y coordinate.
     */
    public final void checkBounds(int x, int y) {
        if (!withinBounds(x, y)) {
            throw new IllegalArgumentException(
                    String.format("Given point (%d, %d) doesn't belong to rectangle (%d, %d)",
                            x, y, getWidth(), getHeight()));
        }
    }

    /**
     * Checks whether given cell belongs to bounds of current map.
     *
     * @param cell Cell to check.
     * @return Whether given cell belongs to bounds of current map.
     */
    public final boolean withinBounds(Cell cell) {
        return withinBounds(cell.getX(), cell.getY());
    }

    /**
     * Checks whether given cell belongs to bounds of current map.
     *
     * @param x Cell X coordinate.
     * @param y Cell Y coordinate.
     * @return Whether given cell belongs to bounds of current map.
     */
    public final boolean withinBounds(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Returns the first found largest rectangle in the map. Searches non-zero cells.
     *
     * @return Largest rectangle.
     */
    public final Rectangle findLargestRectangle() {
        return findLargestRectangle(false);
    }

    /**
     * Returns the first found largest rectangle in the map.
     *
     * @param zeroArea Whether to search 0's area or areas of values > 0.
     * @return Largest rectangle for the given condition.
     */
    public final Rectangle findLargestRectangle(boolean zeroArea) {
        long[][] assistantMatrix = buildAssistantMatrix(zeroArea);
        Rectangle max = null;
        Rectangle candidate;
        for (int y = 0; y < assistantMatrix.length; y++) {
            candidate = findLargestRectangle(assistantMatrix[y], y);
            if (candidate != null && (max == null || candidate.area() > max.area())) {
                max = candidate;
            }
        }
        if (max == null) {
            return null;
        }
        // Invert y index.
        return new Rectangle(max.getX(), max.getY() - max.getHeight() + 1, max.getWidth(), max.getHeight());
    }

    /**
     * Builds assistant matrix for current map with {@link Direction.TOP2BOTTOM} direction.
     *
     * @param zeroArea Whether to search 0's area or areas of values > 0.
     * @return Assistant matrix.
     */
    public final long[][] buildAssistantMatrix(boolean zeroArea) {
        return buildAssistantMatrix(Direction.TOP2BOTTOM, zeroArea);
    }

    /**
     * Builds assistant matrix for current map with given direction.
     *
     * @param direction Direction.
     *
     * @param zeroArea Whether to search 0's area or areas of values > 0.
     * @return Assistant matrix.
     */
    public final long[][] buildAssistantMatrix(Direction direction, boolean zeroArea) {
        long[][] assistant = new long[getHeight()][getWidth()];
        long value;
        if (Direction.RIGHT2LEFT.equals(direction)) {
            for (int y = 0; y < matrix.length; y++) {
                for (int x = matrix[0].length - 1; x >= 0; x--) {
                    value = get(x, y);
                    if (!zeroArea && value > 0 || zeroArea && value == 0) {
                        assistant[y][x] = x < matrix[0].length - 1 ? assistant[y][x + 1] + 1 : 1;
                    } else {
                        assistant[y][x] = 0;
                    }
                }
            }
        } else if (Direction.LEFT2RIGHT.equals(direction)) {
            for (int y = 0; y < matrix.length; y++) {
                for (int x = 0; x < matrix[0].length; x++) {
                    value = get(x, y);
                    if (!zeroArea && value > 0 || zeroArea && value == 0) {
                        assistant[y][x] = x > 0 ? assistant[y][x - 1] + 1 : 1;
                    } else {
                        assistant[y][x] = 0;
                    }
                }
            }
        } else if (Direction.TOP2BOTTOM.equals(direction)) {
            for (int x = 0; x < matrix[0].length; x++) {
                for (int y = 0; y < matrix.length; y++) {
                    value = get(x, y);
                    if (!zeroArea && value > 0 || zeroArea && value == 0) {
                        assistant[y][x] = y > 0 ? assistant[y - 1][x] + 1 : 1;
                    } else {
                        assistant[y][x] = 0;
                    }
                }
            }
        } else {
            for (int x = 0; x < matrix[0].length; x++) {
                for (int y = matrix.length - 1; y >= 0; y--) {
                    value = get(x, y);
                    if (!zeroArea && value > 0 || zeroArea && value == 0) {
                        assistant[y][x] = y < matrix.length - 1 ? assistant[y + 1][x] + 1 : 1;
                    } else {
                        assistant[y][x] = 0;
                    }
                }
            }
        }
        return assistant;
    }

    /**
     * Creates new empty map with same dimensions as this map.
     *
     * @return New empty with same dimensions.
     */
    public final Map newOfSameDimensions() {
        return new Map(this.getWidth(), this.getHeight());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.deepHashCode(this.matrix);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Map other = (Map) obj;
        return Arrays.deepEquals(this.matrix, other.matrix);
    }

    /**
     * Creates String representation for the current map: 0s are displayed as spaces, 1-9 are displayed as it is, other
     * values displayed as '#' character.
     *
     * @return String representation for the current map.
     */
    @Override
    public String toString() {
        String newLine = Constants.NEW_LINE;
        String column = Constants.COLUMN_START_END;
        char emptyCell = Constants.EMPTY_CELL;
        char big = Constants.BIG_CELL;
        String header = StringUtils.repeat(Constants.HEADER_CHAR, getWidth() + 2);

        StringBuilder sb = new StringBuilder(header);
        sb.append(newLine);
        for (int y = 0; y < getHeight(); y++) {
            sb.append(column);
            for (int x = 0; x < getWidth(); x++) {
                long value = get(x, y);
                if (value == 0L) {
                    sb.append(emptyCell);
                } else if (value < Constants.BIG_CELL_VALUE) {
                    sb.append(value);
                } else {
                    sb.append(big);
                }
            }
            sb.append(column);
            sb.append(newLine);
        }
        sb.append(header);
        sb.append(newLine);
        return sb.toString();
    }

    /**
     * Empty map.
     *
     * @return empty map.
     */
    public static Map emptyMap() {
        return EMPTY_MAP;
    }

    /**
     * Finds largest rectangle in the given histogram of integers. Linear time.
     *
     * @param heights Heights of histogram.
     * @param yIndex y index of row. Not used in calculations but convenient to use for created rectangle object.
     * @return largest rectangle in the given histogram of integers or null if all heights are 0.
     */
    public static Rectangle findLargestRectangle(long[] heights, int yIndex) {
        Rectangle max = null;
        Rectangle rectangle = null;
        Deque<Rectangle> rectangles = null;
        for (int xIndex = 0; xIndex < heights.length; xIndex++) {
            long height = heights[xIndex];
            if (rectangles == null && height > 0) {
                // Create stack only when positive height is found.
                rectangles = new LinkedList<>();
            }
            if (height > 0 && (rectangles.isEmpty() || height > rectangles.peek().getHeight())) {
                // Initial branch when there are no rectangles in stack or new opening higher rectangle.
                rectangle = new Rectangle(xIndex, yIndex, 1, height);
                rectangles.push(rectangle);
            } else if (rectangles != null && !rectangles.isEmpty() && height < rectangles.peek().getHeight()) {
                // Lower rectangle met - need to close all opened rectangles that higher.
                while (!rectangles.isEmpty() && height < rectangles.peek().getHeight()) {
                    rectangle = rectangles.pop();
                    if (max == null || max.area() < (xIndex - rectangle.getX()) * rectangle.getHeight()) {
                        max = new Rectangle(rectangle.getX(), rectangle.getY(),
                                xIndex - rectangle.getX(), rectangle.getHeight());
                    }
                }
                // Open lower rectangle but with the farthest x index of closed ones (the one that was started by
                // higher rectangle).
                if (height > 0) {
                    rectangles.push(new Rectangle(rectangle.getX(), rectangle.getY(),
                            xIndex - rectangle.getX(), height));
                }
            }
        }
        // For all opened rectangles that end with the last index heights.length.
        while (rectangles != null && !rectangles.isEmpty()) {
            rectangle = rectangles.pop();
            if (max == null || (heights.length - rectangle.getX()) * rectangle.getHeight() > max.area()) {
                max = new Rectangle(rectangle.getX(), rectangle.getY(),
                        heights.length - rectangle.getX(), rectangle.getHeight());
            }
        }
        return max;
    }

    /**
     * Parses given string (or string) as a string representation of map. Treats each string as a row if multiple
     * strings are given or whole map as a single string with new lines.
     *
     * @param args String rows (or map as single string).
     * @return Parsed map.
     */
    public static Map fromString(String... args) {
        String newLine = Constants.NEW_LINE;
        String mapAsString = args[0];
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s);
                sb.append(newLine);
            }
            mapAsString = sb.toString();
        }

        int i = 0;
        int j = 0;
        List<String> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new StringReader(mapAsString))) {
            String row;
            while ((row = br.readLine()) != null) {
                if (row.charAt(0) == Constants.HEADER_CHAR) {
                    continue;
                }
                j = Math.max(parseRow(row, rows.size(), null), j); // Evaluate first.
                rows.add(row);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while reading string.", ex);
        }
        Map map = new Map(j, rows.size());
        for (String row : rows) {
            parseRow(row, i++, map);
        }
        return map;
    }

    /**
     * Parses given row string.
     *
     * @param row Row.
     * @param rowIndex Row index.
     * @param map Map to put the results.
     * @return Number of parsed columns.
     */
    private static int parseRow(String row, int rowIndex, Map map) {
        String[] tokens = skipEmpty(Constants.NON_DIGITS.split(row));
        int columns = tokens.length >= 1 ? tokens[0].length() : 0;
        if (tokens.length > 1) {
            columns = tokens.length;
        }
        char ch;
        if (map == null) {
            return columns;
        }
        if (tokens.length > 1) {
            for (int j = 0;
                    j < tokens.length;
                    j++) {
                String token = tokens[j];
                if (!token.isEmpty()) {
                    ch = token.charAt(0);
                    if (Character.isDigit(ch)) {
                        map.set(j, rowIndex, Integer.parseInt(tokens[j]));
                    } else if (ch == Constants.BIG_CELL) {
                        map.set(j, rowIndex, Constants.BIG_CELL_VALUE);
                    }
                }
            }
        } else {
            row = tokens[0];
            for (int j = 0;
                    j < row.length();
                    j++) {
                ch = row.charAt(j);
                if (ch == Constants.EMPTY_CELL || ch == '0') {
                    map.set(j, rowIndex, 0);
                } else if (ch == Constants.BIG_CELL) {
                    map.set(j, rowIndex, Constants.BIG_CELL_VALUE);
                } else if (Character.isDigit(ch)) {
                    map.set(j, rowIndex, ch - 48);
                }
            }
        }

        return columns;
    }

    /**
     * Skips nulls or empty strings from given array.
     *
     * @param strings Array to filter.
     * @return Filtered array.
     */
    private static String[] skipEmpty(String[] strings) {
        String[] result = strings;
        List<String> resultList = null;
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            if (s == null || s.isEmpty()) {
                result = null;
                if (resultList == null) {
                    resultList = new ArrayList<>(strings.length);
                    for (int j = 0; j < i; j++) {
                        resultList.add(strings[j]);
                    }
                }
            } else if (resultList != null) {
                resultList.add(s);
            }
        }
        if (resultList != null) {
            return resultList.toArray(Constants.EMPTY_STRINGS);
        }
        return result;
    }

    /**
     * Director for the assistant matrix.
     */
    public enum Direction {

        LEFT2RIGHT,
        RIGHT2LEFT,
        TOP2BOTTOM,
        BOTTOM2TOP
    }

    /**
     * Cell class.
     */
    public static final class Cell {

        private final int x;
        private final int y;

        /**
         * Constructs cell.
         *
         * @param x X.
         * @param y Y.
         */
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * X.
         *
         * @return X coordinate.
         */
        public int getX() {
            return x;
        }

        /**
         * Y.
         *
         * @return Y coordinate.
         */
        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Cell point = (Cell) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ')';
        }

        /**
         * Square distance to other cell.
         *
         * @param other Other cell.
         * @return Distance to cell.
         */
        public int squareDistanceTo(Cell other) {
            return squareDistanceTo(other.getX(), other.getY());
        }

        /**
         * Square distance to other cell.
         *
         * @param x X.
         * @param y Y.
         * @return Distance to cell.
         */
        public int squareDistanceTo(int x, int y) {
            return this.x * x + this.y * y;
        }

        /**
         * Distance to other cell.
         *
         * @param other Other cell.
         * @return Distance to cell.
         */
        public double distanceTo(Cell other) {
            return Math.sqrt(squareDistanceTo(other.getX(), other.getY()));
        }

        /**
         * Distance to other cell.
         *
         * @param x X.
         * @param y Y.
         * @return Distance to cell.
         */
        public double distanceTo(int x, int y) {
            return Math.sqrt(squareDistanceTo(x, y));
        }

        /**
         * Manhattan distance to other cell.
         *
         * @param x X.
         * @param y Y.
         * @return Manhattan distance to cell.
         */
        public long manhattanDistanceTo(long x, long y) {
            return Math.abs(this.x - x) + Math.abs(this.y - y);
        }

        /**
         * Manhattan distance to other cell.
         *
         * @param other Other cell.
         * @return Distance to cell.
         */
        public long manhattanDistanceTo(Cell other) {
            return manhattanDistanceTo(other.getX(), other.getY());
        }
    }

    /**
     * Cell with external priority. For Dijkstra's algorithm.
     */
    public final class CellWithPriority implements Comparable<CellWithPriority> {

        private final Cell cell;
        private final double priority;

        /**
         * Constructs prioritized cell with given arguments.
         *
         * @param cell Cell.
         * @param priority Priority.
         */
        public CellWithPriority(Cell cell, double priority) {
            this.cell = cell;
            this.priority = priority;
        }

        /**
         * Cell.
         *
         * @return Cell.
         */
        public Cell getCell() {
            return cell;
        }

        /**
         * Priority.
         *
         * @return Priority.
         */
        public double getPriority() {
            return priority;
        }

        @Override
        public int compareTo(CellWithPriority o) {
            return Double.compare(priority, o.priority);
        }
    }

    /**
     * Rectangle class.
     */
    public static final class Rectangle {

        private final long x;
        private final long y;
        private final long width;
        private final long height;

        /**
         * Constructs rectangle with given coordinates.
         *
         * @param x X.
         * @param y Y.
         * @param width Width.
         * @param height Height.
         */
        public Rectangle(long x, long y, long width, long height) {
            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("Width and height must be positive.");
            }
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * Area of the rectangle.
         *
         * @return Area of rectangle.ß
         */
        public long area() {
            return width * height;
        }

        /**
         * Y.
         *
         * @return Y.
         */
        public long getX() {
            return x;
        }

        /**
         * X.
         *
         * @return X.
         */
        public long getY() {
            return y;
        }

        /**
         * Width.
         *
         * @return Width.
         */
        public long getWidth() {
            return width;
        }

        /**
         * Height.
         *
         * @return Height.
         */
        public long getHeight() {
            return height;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Rectangle rectangle = (Rectangle) o;
            return x == rectangle.x && y == rectangle.y && width == rectangle.width && height == rectangle.height;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + (int) (this.x ^ (this.x >>> 32));
            hash = 29 * hash + (int) (this.y ^ (this.y >>> 32));
            hash = 29 * hash + (int) (this.width ^ (this.width >>> 32));
            hash = 29 * hash + (int) (this.height ^ (this.height >>> 32));
            return hash;
        }

        /**
         * Builds rectangle with given coordinates.
         *
         * @param x X.
         * @param y Y.
         * @param width Width.
         * @param height Height.
         * @return Rectangle with given coordinates.
         */
        public static Rectangle of(long x, long y, long width, long height) {
            return new Rectangle(x, y, width, height);
        }
    }

    /**
     * Path in map.
     */
    public static class Path {

        private final List<Cell> path;
        private final Map map;
        private static final Path EMPTY = new Path(EMPTY_MAP, Collections.emptyList());

        /**
         * Path in map.
         *
         * @param map Map.
         * @param path Actual path with source as the first element and target as the last elements.
         */
        public Path(Map map, List<Cell> path) {
            this.map = map;
            this.path = Collections.unmodifiableList(path);
        }

        /**
         * Creates a map drawing path in it as 1-2-3-...-8-9-1-2-3... sequence. Values >=10 are considered as walls.
         *
         * @return Map with the path.
         */
        public Map asMap() {
            if (!hasPath()) {
                return Map.emptyMap();
            }
            Map newMap = map.newOfSameDimensions();
            for (int w = 0; w < map.getWidth(); w++) {
                for (int h = 0; h < map.getHeight(); h++) {
                    long value = map.get(w, h);
                    if (value > 0) {
                        newMap.set(w, h, Constants.BIG_CELL_VALUE);
                    }
                }
            }
            int index = 1;
            for (Cell cellInPath : path) {
                newMap.set(cellInPath, index++);
                if (index >= Constants.BIG_CELL_VALUE) {
                    index = 1;
                }
            }
            return newMap;
        }

        /**
         * Returns whether the path has path to target.
         *
         * @return Whether the path has path to target.
         */
        public boolean hasPath() {
            return path != null && !path.isEmpty();
        }

        /**
         * Returns list of cells that construct the path.
         *
         * @return List of cells.
         */
        public List<Cell> asList() {
            return path;
        }

        /**
         * Cost of the path.
         *
         * @return Cost of the path.
         */
        public int getCost() {
            if (path.isEmpty()) {
                return 0;
            }
            int cost = path.size() - 1; // Number of steps.
            Cell current = path.get(0);
            for (Cell cell : path) {
                cost += Math.abs(map.get(cell) - map.get(current));
                current = cell;
            }
            return cost;
        }

        @Override
        public String toString() {
            return String.format("Path (%d, cost: %d): %s", path.size(), getCost(), path);
        }

        /**
         * Empty path when there is no path.
         *
         * @return Empty path when there is no path.
         */
        public static Path emptyPath() {
            return EMPTY;
        }
    }

    /**
     * Constants for Map.
     */
    interface Constants {

        static final long UNREACHABLE_CELL = Long.MAX_VALUE / 2;
        static final long BIG_CELL_VALUE = 10L;
        static final String NEW_LINE = "\n";
        static final String COLUMN_START_END = "|";
        static final char EMPTY_CELL = ' ';
        static final long EMPTY_CELL_VALUE = 0L;
        static final char BIG_CELL = '#';
        static final char HEADER_CHAR = '-';

        static final Pattern NON_DIGITS = Pattern.compile("[^0-9\\s#]+");
        static final String[] EMPTY_STRINGS = new String[0];
    }
}
