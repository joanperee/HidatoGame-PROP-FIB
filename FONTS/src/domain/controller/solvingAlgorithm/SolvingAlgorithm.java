package domain.controller.solvingAlgorithm;

import domain.model.Hidato;
import domain.model.GameSession;

/**
 * Abstract base class for Hidato solving algorithms.
 * Concrete implementations of this class must provide the core solving
 * behaviors: detecting whether a puzzle has any solution, validating whether
 * a given Hidato represents a complete valid solution, and producing a
 * GameSession representing the solved puzzle. *
 */
public abstract class SolvingAlgorithm {
    /**
     * Default constructor
     */
    public SolvingAlgorithm() {
    }

    /**
     * Determines whether the provided Hidato puzzle has at least one
     * valid solution.
     *
     * @param hidato The Hidato puzzle to analyze.
     * @return True if the puzzle admits at least one valid solution,
     *         false otherwise.
     */
    public abstract boolean hasSolution(Hidato hidato);

    /**
     * Validates whether the provided Hidato instance represents a
     * complete and correct solution for the puzzle rules.
     *
     * @param hidato The Hidato instance to validate.
     * @return True if the Hidato is a complete valid solution,
     *         false otherwise.
     */
    public abstract boolean isSolution(Hidato hidato);

    /**
     * Attempts to solve the given Hidato puzzle and returns a
     * GameSession describing the outcome. Implementations may return null to
     * indicate
     * that no solution was found.
     *
     * @param hidato The Hidato puzzle to solve.
     * @return A GameSession with solution details, or null
     *         if the puzzle is unsolvable.
     */
    public abstract GameSession solve(Hidato hidato);
}