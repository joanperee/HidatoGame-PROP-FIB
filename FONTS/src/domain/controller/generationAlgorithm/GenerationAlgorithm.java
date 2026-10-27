package domain.controller.generationAlgorithm;

import domain.enumeration.*;

/**
 * Abstract base class for Hidato puzzle generation algorithms.
 * Implementations of this class are responsible for: generating new
 * hidatos given some constraints and converting a complete hidato into
 * a solvable puzzle by hiding cells according to its difficulty.
 */
public abstract class GenerationAlgorithm {
    /**
     * Default constructor
     */
    public GenerationAlgorithm() {
    }

    /**
     * Generates a puzzle. It is a complete, solved Hidato where numbers 1 through
     * nC
     * (or fewer if generation stops early) are placed sequentially on the board.
     * The puzzle must respect the given topology, adjacency mode, and approximate
     * cell count.
     *
     * @param topology Topology code: "Q" (Square), "T" (Triangle), or "H"
     *                 (Hexagon).
     * @param ad       Adjacency mode.
     * @param nC       Requested approximate number of cells to place.
     * @param dif      Difficulty level to apply during generation.
     * @return A valid string representation of the solved hidato (header + board)
     *         or null if it was not capable to generate it.
     */
    public abstract String generateHidato(String topology, Adjacency ad, int nC, Difficulty dif);

    /**
     * Converts a complete Hidato solution into a solvable puzzle by hiding cells
     * according to the specified difficulty level.
     *
     * @param solution A string representation of the complete solved Hidato.
     * @param dif      Difficulty level. The easier, the fewer emptied cells.
     * @param nC       Total number of cells in the solution.
     * @return A string representation of the solvable puzzle.
     */
    public abstract String makeSolvable(String solution, Difficulty dif, int nC);
}
