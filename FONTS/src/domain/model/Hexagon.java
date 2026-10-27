package domain.model;

import java.util.*;

/**
 * Represents a Hidato board with hexagonal cells.
 * This class handles the specific adjacency logic for hexagons in a vertical presentation.
 */
public class Hexagon extends Hidato {

    /**
     * Constructs a Hexagon board by parsing a raw data string.
     * * @param boardData The string containing the Hidato representation.
     */
    public Hexagon(String boardData) {
        super(boardData);
    }

    /**
     * Calculates the adjacent cells for a given hexagonal cell.
     * In a vertical presentation, even rows (0, 2, ...) and odd rows (1, 3, ...) 
     * have different adjacency patterns due to the staggered offset.
     * * @param r The row index of the cell.
     * @param c The column index of the cell.
     * @return A collection of integer arrays, where each array contains the [row, col] of an adjacent cell.
     * @throws IndexOutOfBoundsException if the provided row or column is outside the board dimensions.
     */
    @Override
    public Collection<int[]> getAdjacents(int r, int c) {
        int maxRow = getRows();
        int maxCol = getCols();

        // Exception treatment for input coordinates
        if (r < 0 || r >= maxRow || c < 0 || c >= maxCol) {
            throw new IndexOutOfBoundsException("Coordinates (" + r + "," + c + ") are out of board bounds.");
        }

        // AIxò s'ha de vigilar gracies a la marevellosa idea de posar una classe amb nom 'List'
        java.util.List<int[]> adjacents = new ArrayList<>();

        // Standard horizontal adjacents (always present if within bounds)
        if (c - 1 >= 0) adjacents.add(new int[]{r, c - 1});
        if (c + 1 < maxCol) adjacents.add(new int[]{r, c + 1});

        // Vertical and diagonal adjacents based on staggered row parity
        if (r % 2 == 0) {
            // Even row offsets
            if (r - 1 >= 0 && c - 1 >= 0) adjacents.add(new int[]{r - 1, c - 1});
            if (r - 1 >= 0) adjacents.add(new int[]{r - 1, c});
            if (r + 1 < maxRow && c - 1 >= 0) adjacents.add(new int[]{r + 1, c - 1});
            if (r + 1 < maxRow) adjacents.add(new int[]{r + 1, c});
        } else {
            // Odd row offsets
            if (r - 1 >= 0) adjacents.add(new int[]{r - 1, c});
            if (r - 1 >= 0 && c + 1 < maxCol) adjacents.add(new int[]{r - 1, c + 1});
            if (r + 1 < maxRow) adjacents.add(new int[]{r + 1, c});
            if (r + 1 < maxRow && c + 1 < maxCol) adjacents.add(new int[]{r + 1, c + 1});
        }

        return adjacents;
    }
}