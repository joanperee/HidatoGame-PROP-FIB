package domain.model;

import java.util.*;
import domain.enumeration.Adjacency;

/**
 * Represents a Hidato board composed of triangular cells.
 * This class handles the adjacency logic specific to triangular grids.
 */
public class Triangle extends Hidato {

    /**
     * Constructs a new Triangle Hidato with a specific ID, number of cells, and holes.
     * * @param idH Unique identifier for the Hidato.
     * @param numCells Total number of cells in the board.
     * @param numHoles Number of non-accessible cells (holes).
     * @throws IllegalArgumentException if numCells is not between 3 and 200.
     */
    public Triangle(UUID idH, int numCells, int numHoles) {
        super(idH, numCells, numHoles);
    }

    /**
     * Constructs a Triangle Hidato by parsing a raw data string.
     * @param boardData The string containing the Hidato board representation.
     */
    public Triangle(String boardData) {
        super(boardData); // Passes the raw data string to the abstract Hidato class
    }

    /**
     * Computes the adjacent cells for a given cell in a triangular grid.
     * Adjacency depends on whether the triangle's base is at the bottom or top, 
     * and whether the Hidato allows adjacency by sides (C) or sides and angles (CA).
     * * @param r The row index of the cell.
     * @param c The column index of the cell.
     * @return A collection of integer arrays, where each array represents the {row, col} of an adjacent cell.
     * @throws IndexOutOfBoundsException if the provided row or column is outside the board dimensions.
     */
    @Override
    public Collection<int[]> getAdjacents(int r, int c) {
        int maxRow = getRows();
        int maxCol = getCols();

        if (r < 0 || r >= maxRow || c < 0 || c >= maxCol) {
            throw new IndexOutOfBoundsException("Row or column index is out of bounds.");
        }

        java.util.List<int[]> adjacents = new ArrayList<>();
        Adjacency adjType = getAdjacency();

        // Determine triangle orientation: Files senars presenten els triangles començant amb la base a baix 
        // mentre que les files parells comencen amb la base a dalt.
        boolean isPointUp = ((r + c) % 2 != 0);

        if (isPointUp) {
            // Basic adjacency (Sides - C)
            if (c - 1 >= 0) adjacents.add(new int[]{r, c - 1});
            if (c + 1 < maxCol) adjacents.add(new int[]{r, c + 1});
            if (r - 1 < maxRow) adjacents.add(new int[]{r - 1, c});

            // Extended adjacency (Sides + Angles - CA)
            if (adjType == Adjacency.CA) {
                if (r - 1 >= 0 && c - 1 >= 0) adjacents.add(new int[]{r - 1, c - 1});
                if (r + 1 >= 0) adjacents.add(new int[]{r + 1, c});
                if (r - 1 >= 0 && c + 1 < maxCol) adjacents.add(new int[]{r - 1, c + 1});
                //if (c - 2 >= 0) adjacents.add(new int[]{r, c - 2});
                //if (c + 2 < maxCol) adjacents.add(new int[]{r, c + 2});
                //if (r + 1 < maxRow && c - 2 >= 0) adjacents.add(new int[]{r + 1, c - 2});
                if (r + 1 < maxRow && c - 1 >= 0) adjacents.add(new int[]{r + 1, c - 1});
                if (r + 1 < maxRow && c + 1 < maxCol) adjacents.add(new int[]{r + 1, c + 1});
                //if (r + 1 < maxRow && c + 2 < maxCol) adjacents.add(new int[]{r + 1, c + 2});
            }
        } else {
            // Basic adjacency (Sides - C)
            if (c - 1 >= 0) adjacents.add(new int[]{r, c - 1});
            if (c + 1 < maxCol) adjacents.add(new int[]{r, c + 1});
            if (r + 1 >= 0) adjacents.add(new int[]{r + 1, c});

            // Extended adjacency (Sides + Angles - CA)
            if (adjType == Adjacency.CA) {
                //if (r - 1 >= 0 && c - 2 >= 0) adjacents.add(new int[]{r - 1, c - 2});
                if (r - 1 >= 0 && c - 1 >= 0) adjacents.add(new int[]{r - 1, c - 1});
                if (r - 1 >= 0 && c + 1 < maxCol) adjacents.add(new int[]{r - 1, c + 1});
                //if (r - 1 >= 0 && c + 2 < maxCol) adjacents.add(new int[]{r - 1, c + 2});
                //if (c - 2 >= 0) adjacents.add(new int[]{r, c - 2});
                //if (c + 2 < maxCol) adjacents.add(new int[]{r, c + 2});
                if (r + 1 < maxRow && c - 1 >= 0) adjacents.add(new int[]{r + 1, c - 1});
                if (r - 1 < maxRow) adjacents.add(new int[]{r - 1, c});
                if (r + 1 < maxRow && c + 1 < maxCol) adjacents.add(new int[]{r + 1, c + 1});
            }
        }

        return adjacents;
    }
}