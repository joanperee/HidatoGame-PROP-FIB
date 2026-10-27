package domain.model;

import java.util.*;
import domain.enumeration.Adjacency;

/**
 * Represents a Hidato board with square-shaped cells.
 * This class extends the abstract Hidato class and implements the logic 
 * for identifying adjacent cells based on side or side+angle connections.
 */
public class Square extends Hidato {

    /** * Constructor by ID and cell parameters.
     * * @param idH Unique identifier for the Hidato board.
     * @param numCells Total number of cells in the board.
     * @param numHoles Number of holes (non-playable cells) in the board.
     * @throws IllegalArgumentException if numCells is not between 3 and 200.
     */
    public Square(UUID idH, int numCells, int numHoles) {
        super(idH, numCells, numHoles);
    }

    /** * Constructor by string parsing.
     * @param boardData The string containing the Hidato board definition.
     */
    public Square(String boardData) {
        super(boardData);
    }

    /** * Retrieves a collection of coordinates for cells adjacent to the specified position.
     * For square cells, adjacency is determined by the board's Adjacency type:
     * - C: Only shares a side (up, down, left, right).
     * - CA: Shares a side or a corner (all 8 surrounding cells).
     * * @param r The row index of the cell.
     * @param c The column index of the cell.
     * @return A Collection of int arrays, where each array contains {row, column} of an adjacent cell.
     * @throws IndexOutOfBoundsException if the provided coordinates are outside the board dimensions.
     */
    @Override
    public Collection<int[]> getAdjacents(int r, int c) {
        int maxRow = getRows();
        int maxCol = getCols();

        // Exception treatment for input coordinates
        if (r < 0 || r >= maxRow || c < 0 || c >= maxCol) {
            throw new IndexOutOfBoundsException("Coordinates (" + r + "," + c + ") are out of board bounds.");
        }

        //AIxò s'ha de vigilar gracies a la marevellosa idea de posar una classe amb nom 'List'
        java.util.List<int[]> adjacents = new ArrayList<>();
        Adjacency adjType = getAdjacency();

        // Standard Side Adjacency (C)
        if (r - 1 >= 0) adjacents.add(new int[]{r - 1, c});
        if (r + 1 < maxRow) adjacents.add(new int[]{r + 1, c});
        if (c - 1 >= 0) adjacents.add(new int[]{r, c - 1});
        if (c + 1 < maxCol) adjacents.add(new int[]{r, c + 1});

        // Additional Corner Adjacency (CA)
        if (adjType == Adjacency.CA) {
            if (r - 1 >= 0 && c - 1 >= 0) adjacents.add(new int[]{r - 1, c - 1});
            if (r - 1 >= 0 && c + 1 < maxCol) adjacents.add(new int[]{r - 1, c + 1});
            if (r + 1 < maxRow && c - 1 >= 0) adjacents.add(new int[]{r + 1, c - 1});
            if (r + 1 < maxRow && c + 1 < maxCol) adjacents.add(new int[]{r + 1, c + 1});
        }

        return adjacents;
    }
}