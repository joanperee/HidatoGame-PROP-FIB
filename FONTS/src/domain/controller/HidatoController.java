package domain.controller;

import java.io.*;
import domain.model.Hidato;
import domain.model.GameSession;
import domain.enumeration.Adjacency;
import domain.enumeration.Difficulty;
import domain.controller.solvingAlgorithm.*;
import domain.controller.generationAlgorithm.*;

/**
 * Controller class for managing Hidato puzzle solving and generation
 * operations.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>Creating and managing the graph representation of a Hidato puzzle</li>
 * <li>Checking if a Hidato puzzle has a solution</li>
 * <li>Solving Hidato puzzles</li>
 * <li>Generating new Hidato puzzles and producing solvable puzzles according to
 * difficulty</li>
 * </ul>
 *
 */
public class HidatoController {
    /**
     * The solving algorithm implementation used by this controller
     */
    private SolvingAlgorithm solver_;

    /**
     * The generation algorithm implementation used by this controller
     */
    private GenerationAlgorithm generator_;

    /**
     * Default private constructor to enforce singleton usage
     */
    private HidatoController() {
    }

    /**
     * Holder class to provide thread-safe initialization of the singleton instance.
     */
    private static class SingletonHolder {
        private static final HidatoController instance = new HidatoController();
    }

    /**
     * Returns the singleton instance of HidatoController.
     *
     * @return the singleton HidatoController instance
     */
    public static HidatoController getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Returns a string representation of the given Hidato board.
     *
     * @param hidato the Hidato instance to format
     * @return a formatted multi-line string representing the board, or "No board
     *         available." if hidato is null
     */
    public String getBoardAsString(Hidato hidato) {
        if (hidato == null)
            return "No board available.";
        int[][] board = hidato.getRepresentation();
        StringBuilder sb = new StringBuilder();

        for (int[] row : board) {
            for (int val : row) {
                if (val == -1)
                    sb.append(" * \t");
                else if (val == 0)
                    sb.append(" ? \t");
                else
                    sb.append(" ").append(val).append(" \t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Loads a Hidato from a CSV file path using the static loader on Hidato.
     *
     * @param filepath the path to the file that contains the Hidato representation
     * @return the loaded Hidato instance, or null if loading failed
     */
    public Hidato loadHidatoFromFile(String filepath) {
        return Hidato.loadFromFile(filepath);
    }

    /**
     * Checks if the provided Hidato has at least one valid solution.
     *
     * @param hidato the Hidato to check
     * @return true if a solution exists, false otherwise
     */
    public boolean hasSolution(Hidato hidato) {
        if (solver_ == null)
            solver_ = new Backtraquing();
        return solver_.hasSolution(hidato);
    }

    /**
     * Checks whether the provided Hidato is a complete and valid solution.
     *
     * @param hidato the Hidato to validate as a solution
     * @return true if the Hidato represents a valid solution, false otherwise
     */
    public boolean isSolution(Hidato hidato) {
        if (solver_ == null)
            solver_ = new Backtraquing();
        return solver_.isSolution(hidato);
    }

    /**
     * Solves the given Hidato and returns a GameSession with the solution details.
     *
     * @param hidato the Hidato to solve
     * @return a GameSession containing the solved board and metadata, or null if no
     *         solution
     */
    public GameSession solve(Hidato hidato) {
        if (solver_ == null)
            solver_ = new Backtraquing();
        return solver_.solve(hidato);
    }

    /**
     * Generates a Hidato puzzle according to the provided topology, adjacency, cell
     * count and difficulty.
     *
     * @param topology topology code (e.g. "Q", "T", "H")
     * @param ad       adjacency mode to use for generation
     * @param nC       approximate number of cells desired in the generated puzzle
     * @param dif      difficulty to apply when generating the puzzle
     * @return a string representation of the generated Hidato (topology + content)
     *         suitable for later parsing or display
     */
    public String generateHidato(String topology, Adjacency ad, int nC, Difficulty dif) {
        if (generator_ == null)
            generator_ = new BasicGenerator();
        return generator_.generateHidato(topology, ad, nC, dif);
    }

    /**
     * Produces a solvable puzzle version from a full solution string according to
     * the requested difficulty
     * and number of cells.
     *
     * @param Solution the full solution string (complete Hidato representation)
     * @param dif      the difficulty level to apply when hiding cells
     * @param nC       the number of cells
     * @return a string representing the puzzle version with hidden cells, or null
     *         if conversion failed
     */
    public String makeSolvable(String Solution, Difficulty dif, int nC) {
        if (generator_ == null)
            generator_ = new BasicGenerator();
        return generator_.makeSolvable(Solution, dif, nC);
    }

    /**
     * Convenience overload that accepts the difficulty as a string ("EASY",
     * "MEDIUM", "HARD").
     * Maps the string to the Difficulty enum and delegates to makeSolvable(String,
     * Solution, Difficulty dif, int nC).
     *
     * @param Solution  the full solution string
     * @param difficuly difficulty name (case-sensitive expected values: "EASY",
     *                  "MEDIUM", "HARD")
     * @param nC        number of cells to hide
     * @return a string representing the puzzle with hidden cells, or {@code null}
     *         on failure
     */
    public String makeSolvable(String Solution, String difficuly, int nC) {
        Difficulty dif = Difficulty.HARD;
        if (difficuly.equals("EASY")) {
            dif = Difficulty.EASY;
        } else if (difficuly.equals("MEDIUM")) {
            dif = Difficulty.MEDIUM;
        }
        return makeSolvable(Solution, dif, nC);
    }

    /**
     * Writes the content of the Hidato string to a CSV file.
     * If an I/O error occurs, the method prints an error message to System.err.
     *
     * @param Hidato the string content to be saved (expected CSV formatted or plain
     *               text representation)
     * @param path   the file path (including filename.csv) where the file will be
     *               created or overwritten
     */
    public void str2csv(String Hidato, String path) {
        // We use try-with-resources to ensure the writer closes automatically
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(Hidato);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the CSV file: " + e.getMessage());
        }
    }
}
