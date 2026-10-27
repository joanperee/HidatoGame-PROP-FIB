package domain.controller.solvingAlgorithm;

import domain.model.Hidato;
import domain.model.GameSession;
import domain.model.User;

import java.util.*;
import java.time.Duration;

/**
 * Backtracking solver implementation for Hidato puzzles. Converts a Hidato
 * instance into an internal graph representation and attempts to find a valid
 * path that assigns sequential numbers to non-hole cells according to the
 * adjacency
 * rules. It provides utilities to check for solvability, validate complete
 * solutions and to
 * produce a GameSession when a solution is found.
 */
public class Backtraquing extends SolvingAlgorithm {

    /**
     * Adjacency list representation of the puzzle graph.
     * graph_[v] contains the list of node identifiers adjacent to node v.
     * The graph uses 1-based indexing: valid indices are 1..V inclusive.
     */
    private ArrayList<Integer>[] graph_;

    /**
     * Array storing values assigned to each cell.
     * values_[v] == 0 means node v has no assigned value;
     * otherwise values_[v] == x indicates node v currently holds value x.
     * The array is sized V+1 and uses 1-based indexing.
     */
    private int[] values_;

    /**
     * Array storing the path sequence of the puzzle solution.
     * The length of path_ equals the number of non-hole cells. Each
     * entry path_[i] contains the node id placed at position i+1 of the
     * sequence (i.e. path_[0] holds the node containing value 1).
     */
    private int[] path_;

    /**
     * Default constructor referencing its parent SolvingAlgorithm.
     */
    public Backtraquing() {
        super();
    }

    /**
     * Checks if the given Hidato puzzle has a solution.
     *
     * @param hidato The object Hidato puzzle to check.
     * @return True if the puzzle has at least one valid solution, false otherwise.
     */
    public boolean hasSolution(Hidato hidato) {
        if (createGraph(hidato)) {
            return solveRec(0);
        }
        return false;
    }

    /**
     * <p>
     * Initializes adjacency lists for each node iterating over the
     * adjacency matrix provided by the Hidato instance and fills graph_.
     *
     * @param hidato The Hidato instance from which to obtain adjacencies.
     * @return False if an invalid adjacency count is detected for any node,
     *         true otherwise.
     */
    private boolean defineAdjacencies(Hidato hidato) {
        int AdjacencyMatrix[][] = hidato.getAdjacencyMatrix();
        // Change i = 0 to i = 1
        int V = hidato.getNumCells();
        for (int i = 1; i <= V; i++) {
            if (!hidato.isHole(i)) {
                for (int j = 1; j <= V; j++) {
                    // We use i-1 and j-1 to access the Matrix (which is 0-indexed)
                    if (AdjacencyMatrix[i - 1][j - 1] == 1 && !hidato.isHole(j)) {
                        graph_[i].add(j);
                    }
                }
                // Now graph_[i] will NOT be null because i starts at 1
                if (graph_[i].size() > hidato.maxAdjacencies()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a graph representation of the Hidato puzzle. This method initializes
     * internal structures graph_, values_ and path_ based on the given Hidato.
     * It populates path_ with any pre-filled values from the Hidato and performs
     * several validation checks: no pre-filled value outranks the number of path
     * positions,
     * no duplicate placements for the same numeric value and the first and last
     * positions
     * in the sequence must be defined.
     *
     * @param hidato The Hidato puzzle to convert into a graph.
     * @return True if the graph was created and validated successfully,
     *         false if an invalid state is detected.
     */
    @SuppressWarnings("unchecked")
    private boolean createGraph(Hidato hidato) {
        // Init vars
        int V = hidato.getNumCells();
        graph_ = new ArrayList[V + 1];
        values_ = new int[V + 1];
        int nh = hidato.getNumHoles();
        path_ = new int[V - nh];
        // Init graph adjacency lists and defined path nodes
        for (int i = 1; i <= V; i++) {
            graph_[i] = new ArrayList<Integer>();
            if (!hidato.isHole(i)) {
                if (hidato.getValue(i) > V - nh) {
                    return false;
                } // Check no outranks
                if (hidato.getValue(i) != 0) {
                    if (path_[hidato.getValue(i) - 1] != 0) {
                        return false;
                    } // Check no overlaps
                    path_[hidato.getValue(i) - 1] = i;
                }
                values_[i] = hidato.getValue(i);
            }
        }

        if (path_[0] == 0)
            return false; // Check first defined
        if (path_[path_.length - 1] == 0)
            return false; // Check last defined
        // Define Adjacencies
        if (!defineAdjacencies(hidato)) {
            return false;
        }
        // Check that the already defined path does not cause incoherence
        return checkCorrectPath();
    }

    /**
     * Validates that consecutive positions already defined in path_
     * are adjacent according to graph_.
     *
     * @return True if all defined consecutive positions are adjacent,
     *         false otherwise.
     */
    private boolean checkCorrectPath() {
        for (int i = 0; i < path_.length - 1; i++) {
            if (path_[i] != 0 && path_[i + 1] != 0 && !graph_[path_[i]].contains(path_[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the current state represents a complete solution to the puzzle.
     * A puzzle is considered solved when no empty (0) entries remain in path_.
     *
     * @param hidato The Hidato puzzle to check.
     * @return True if the puzzle is correctly solved, false otherwise.
     */
    public boolean isSolution(Hidato hidato) {
        if (createGraph(hidato)) {
            boolean corrPath = checkCorrectPath();
            int lp = getFirstEmpty();
            return lp == -1 && corrPath;
        }
        return false;
    }

    /**
     * Finds the first empty position in the path that needs to be filled.
     * An empty position is indicated by a 0 in path_.
     *
     * @return The index of the first empty position in path_, or -1 if none exist.
     */
    private int getFirstEmpty() {
        for (int i = 0; i < path_.length; i++) {
            if (path_[i] == 0)
                return i;
        }
        return -1;
    }

    /**
     * Solves the Hidato puzzle using a backtracking algorithm and returns a
     * GameSession
     * describing the solution and elapsed time. If a solution is found the method
     * updates the given hidato by setting each cell value according to the
     * discovered path.
     *
     * @param hidato The Hidato puzzle to solve.
     * @return A GameSession containing the solved puzzle and timing metadata,
     *         or null if no solution exists.
     */
    public GameSession solve(Hidato hidato) {
        long start = System.nanoTime();
        if (createGraph(hidato)) {
            if (solveRec(1)) { // Check if a solution was actually found
                // Update the original hidato object directly
                checkCorrectPath();
                for (int i = 0; i < path_.length; i++) {
                    hidato.setCell(path_[i], i + 1);
                }

                long end = System.nanoTime();
                long et = Duration.ofNanos(end - start).getSeconds();
                // Now currentHidato in the Driver will reflect these changes
                return new GameSession(UUID.randomUUID(), hidato.getIdH(), hidato,
                        new User("SYSTEM_CPU", true).getUsername(), et);
            }
        }
        return null;
    }

    /**
     * Recursive backtracking routine that attempts to fill the sequence position k
     * and beyond.
     * The method enforces constraints and backtracks when assignments lead to
     * dead-ends.
     * It assumes createGraph(Hidato) has been called and that path_ and
     * values_ are properly initialized.
     *
     * @param k Index (0-based) of the position in path_ to fill next. For a full
     *          solve
     *          call, clients typically start with k = 1 since path_[0] must be
     *          predefined.
     * @return True if a valid complete assignment from position k onwards is found,
     *         false otherwise.
     */
    private boolean solveRec(int k) {
        // Base case: check if all positions in the path are filled
        if (k == path_.length) {
            // All positions are filled - solution found!
            return true;
        }
        // node already filled
        if (path_[k] != 0 && values_[path_[k]] == k + 1 && graph_[path_[k - 1]].contains(path_[k]))
            return solveRec(k + 1);
        else if (path_[k] != 0 && (values_[path_[k]] != k + 1 || !graph_[path_[k - 1]].contains(path_[k])))
            return false;

        // Get the current value we need to place (1-indexed becomes 0-indexed)
        int currentValue = k + 1;

        // Get the cell containing the previous value (currentValue - 1)
        int prevCell = path_[k - 1];

        // For positions > 0, the next cell must be adjacent to the previous cell
        ArrayList<Integer> adjacentNodes = graph_[prevCell];

        for (int adjacentCell : adjacentNodes) {
            // Check constraints for the adjacent cell
            if (values_[adjacentCell] == 0) {
                // Cell is empty - we can assign the current value
                path_[k] = adjacentCell;
                values_[adjacentCell] = currentValue;

                if (solveRec(k + 1)) {
                    return true;
                }

                // Backtrack
                path_[k] = 0;
                values_[adjacentCell] = 0;
            }
            // If cell has a different non-zero value, skip it (constraint violation)
        }

        return false;
    }
}