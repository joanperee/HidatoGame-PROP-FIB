package domain.controller.generationAlgorithm;

import java.util.*;
import domain.enumeration.*;

import static java.lang.Math.max;

/**
 * Basic generator for creating Hidato puzzles.
 *
 * <p>
 * This class implements a randomized generation strategy. It builds
 * a board incrementally by placing sequential numbers starting from 1 on a
 * two-dimensional matrix representation. When the generator attempts to place
 * a value outside the current matrix bounds it expands the board to accommodate
 * the new position.
 *
 * <p>
 * This generator may stop
 * early if it cannot place the next number.
 * </p>
 */
public class BasicGenerator extends GenerationAlgorithm {

    /**
     * Default constructor. Delegates to the parent GenerationAlgorithm constructor.
     */
    public BasicGenerator() {
        super();
    }

    /**
     * Generates a valid hidato given four (4) constraints.
     *
     * @param topology The type of cells it will have (H, T, Q).
     * @param ad       The type of path it will allow (sides, sides+vertices).
     * @param nC       The number of filled cells.
     * @param dif      Difficulty impacts the number of cells initially solved
     *                 (EASY, MEDIUM, HARD).
     * @return The newly created valid hidato in a String format.
     */
    public String generateHidato(String topology, Adjacency ad, int nC, Difficulty dif) {
        if (nC > 200 || nC < 3)
            return null;
        if (topology.equals("H") || topology.equals("T") || topology.equals("Q")) {
            if (topology.equals("H") && ad != Adjacency.CA)
                return null;
            int MAX_ITER = 30;
            int nCbest = 0;
            String best = null;
            for (int j = 0; j < MAX_ITER && nCbest != nC; ++j) {
                ArrayList<ArrayList<Integer>> board = new ArrayList<>();
                ArrayList<Integer> firstRow = new ArrayList<>();
                ArrayList<Integer> secondRow = new ArrayList<>();
                firstRow.add(-1);
                firstRow.add(-1);
                secondRow.add(-1);
                secondRow.add(-1);
                Random rand = new Random();
                int x = rand.nextInt(2);
                int y = rand.nextInt(2);
                board.add(firstRow);
                board.add(secondRow);
                board.get(x).set(y, 1);
                int[] pos = new int[2];
                pos[0] = x;
                pos[1] = y;
                boolean res = true;
                int currnC = 0;
                for (int i = 2; i <= nC && res; ++i) {
                    res = setValue(board, i, pos, ad, topology);
                    currnC = i;
                    if (!res) {
                        currnC = i - 1; // The set value has tried to assign value i to one of the adjacent cells of pos
                                        // but there is no
                        // possible adjacency that is empty. So the number of filled cells is i-1 (and
                        // no mode cells can be put in this hidato)
                    }
                }
                // Check if it is better than current best
                if (nCbest < currnC) {
                    nCbest = currnC; // can't use nC
                    best = (nC + "\n" + topology + "," + ad.toString() + "," + board.size() + "," + board.get(0).size()
                            + "\n" +
                            convert(board));
                }
            }
            return best;
        }

        return null;
    }

    /**
     * Given a solved hidato, it creates an unsolved playable version of it
     * taking into account how many cells it has to empty given the difficulty.
     *
     * @param solution The hidato solved.
     * @param dif      The difficulty of the solved hidato (EASY, MEDIUM, HARD).
     * @param nC       The number of cells that will remain filled (thus, solved).
     * @return The playable version of the initially solved hidato.
     */
    public String makeSolvable(String solution, Difficulty dif, int nC) {
        int emptyCells;
        if (nC > 200 || nC < 3)
            return null;
        Random rand = new Random();
        if (dif == Difficulty.EASY)
            emptyCells = max(1, (nC - 2) / 4);
        else if (dif == Difficulty.MEDIUM)
            emptyCells = max(1, (nC - 2) / 2);
        else
            emptyCells = max(1, (3 * (nC - 2)) / 4);
        HashSet<Integer> hS = new HashSet<>();
        while (emptyCells != hS.size()) {
            hS.add(rand.nextInt(nC - 2) + 2);
        }

        // Format the solution to give a playable hidato
        String[] rows = solution.split("\n");
        if (!correctHeader(rows[0].split(",")))
            return null;
        for (int i = 1; i < rows.length; ++i) {
            String[] elem = rows[i].split(",");
            for (int j = 0; j < elem.length; ++j) {
                String s = elem[j];
                if (!s.equals("#") && !s.equals("*") && !s.equals("?") && !s.equals("1")) {
                    try {
                        if (hS.contains(Integer.parseInt(s))) {
                            elem[j] = "?";
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            rows[i] = String.join(",", elem);
        }
        return String.join("\n", rows);
    }

    /**
     * Checks if the vector of strings containing the first line of an hidato,
     * which is the header with information, is correct.
     *
     * @param line The header of the hidato. {(Q),(CA),(rows),(cols)}
     * @return True if line follows the correct format shown, false otherwise.
     */
    private boolean correctHeader(String[] line) {
        if (line.length < 4)
            return false;
        boolean typeOk = line[0].equals("Q") || line[0].equals("T") || line[0].equals("H");
        boolean configOk = line[1].equals("C") || line[1].equals("CA");
        try {
            Integer.parseInt(line[2]);
            Integer.parseInt(line[3]);
            return typeOk && configOk;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Computes the plausible moves given a position and taking into account
     * the adjacency and the topology of the hidato.
     *
     * @param ad       Type of adjacency (sides or sides + vertices).
     * @param topology Type of cells (Squares, Hexagons, Triangles).
     * @param pos      Current starting position from which the plausible moves are
     *                 searched.
     * @return An array of plausible positions.
     *         Each element of the array is a Integer[] with 2 elements, coordenates
     *         x and y.
     */
    private ArrayList<Integer[]> getAllPosibleNexts(Adjacency ad, String topology, int[] pos) {
        if (topology.equals("H")) {
            if (pos[0] % 2 == 0) {
                return new ArrayList<>(Arrays.asList(
                        new Integer[] { -1, -1 },
                        new Integer[] { -1, 0 },
                        new Integer[] { 0, -1 },
                        new Integer[] { 0, 1 },
                        new Integer[] { 1, -1 },
                        new Integer[] { 1, 0 }));
            } else {
                return new ArrayList<>(Arrays.asList(
                        new Integer[] { -1, 0 },
                        new Integer[] { -1, 1 },
                        new Integer[] { 0, -1 },
                        new Integer[] { 0, 1 },
                        new Integer[] { 1, 0 },
                        new Integer[] { 1, 1 }));
            }
        } else if (topology.equals("Q")) {
            if (ad == Adjacency.C) {
                return new ArrayList<>(Arrays.asList(
                        new Integer[] { -1, 0 },
                        new Integer[] { 0, -1 },
                        new Integer[] { 0, 1 },
                        new Integer[] { 1, 0 }));
            } else {
                return new ArrayList<>(Arrays.asList(
                        new Integer[] { -1, -1 },
                        new Integer[] { -1, 0 },
                        new Integer[] { -1, 1 },
                        new Integer[] { 0, -1 },
                        new Integer[] { 0, 1 },
                        new Integer[] { 1, -1 },
                        new Integer[] { 1, 0 },
                        new Integer[] { 1, 1 }));
            }
        } else {
            if (ad == Adjacency.C) {
                // The base of the triangle is up
                boolean isPointUp = ((pos[0] + pos[1]) % 2 != 0);
                if (!isPointUp) {
                    return new ArrayList<>(Arrays.asList(
                            new Integer[] { 1, 0 },
                            new Integer[] { 0, -1 },
                            new Integer[] { 0, 1 }));
                }
                // The base of the triangle is down
                else {
                    return new ArrayList<>(Arrays.asList(
                            new Integer[] { 0, -1 },
                            new Integer[] { 0, 1 },
                            new Integer[] { -1, 0 }));
                }
            } else {
                // The base of the triangle is up (5+4+3)
                boolean isPointUp = ((pos[0] + pos[1]) % 2 != 0);
                if (!isPointUp) {
                    return new ArrayList<>(Arrays.asList(
                            // new Integer[]{-1, -2},
                            new Integer[] { -1, -1 },
                            new Integer[] { -1, 0 },
                            new Integer[] { -1, 1 },
                            // new Integer[]{-1, 2},
                            // new Integer[]{0, -2},
                            new Integer[] { 0, -1 },
                            new Integer[] { 0, 1 },
                            // new Integer[]{0, 2},
                            new Integer[] { 1, -1 },
                            new Integer[] { 1, 0 },
                            new Integer[] { 1, 1 }));
                }
                // The base of the triangle is down (3+4+5)
                else {
                    return new ArrayList<>(Arrays.asList(
                            new Integer[] { -1, -1 },
                            new Integer[] { -1, 0 },
                            new Integer[] { -1, 1 },
                            // new Integer[]{0, -2},
                            new Integer[] { 0, -1 },
                            new Integer[] { 0, 1 },
                            // new Integer[]{0, 2},
                            // new Integer[]{1, -2},
                            new Integer[] { 1, -1 },
                            new Integer[] { 1, 0 },
                            new Integer[] { 1, 1 }
                    // new Integer[]{1, 2}
                    ));
                }
            }
        }
    }

    /**
     * Checks if a particular position dis (x, y) is inside the limits of the board
     * and is empty.
     *
     * @param board Current hidato board.
     * @param dis   Coordenates (x, y) representing the cell evaluated.
     * @return True if the cell in the position dis is valid, false otherwise.
     */
    private boolean ocupied(ArrayList<ArrayList<Integer>> board, int[] dis) {
        return dis[0] >= 0 && dis[1] >= 0 && dis[0] < board.size() && dis[1] < board.get(0).size()
                && board.get(dis[0]).get(dis[1]) != -1;
    }

    /**
     * Gets the possible moves (not the plausible, as getAllPossibleNexts) given a
     * position pos.
     *
     * @param board    The hidato represented as a matrix of integers.
     * @param pos      The posistion (x, y) being evaluated to know its valid
     *                 neighbors.
     * @param ad       The adjacency (sides or sides + vertices).
     * @param topology The topology (Squares, Hexagons, Triangles).
     * @return An array of possible positions.
     */
    private ArrayList<Integer[]> getPossibleNexts(ArrayList<ArrayList<Integer>> board, int[] pos, Adjacency ad,
            String topology) {
        ArrayList<Integer[]> possibleNexts = new ArrayList<>();
        ArrayList<Integer[]> allPossibleNexts = getAllPosibleNexts(ad, topology, pos);
        for (Integer[] next : allPossibleNexts) {
            int[] dis = { pos[0] + next[0], pos[1] + next[1] };
            if (!ocupied(board, dis))
                possibleNexts.add(next);
        }
        return possibleNexts;
    }

    /**
     * Attempts to place a value on the board at a random adjacent position.
     * Selects displacement directions and tries to place the given value at
     * adjacent positions.
     * If the position is out of bounds, the board is expanded to accommodate it. If
     * the position
     * already contains a value other than -1, the move is reversed and another
     * direction is tried.
     *
     * @param board    The board represented as a matrix of integers (-1 for empty
     *                 cells).
     * @param val      The random value to place.
     * @param pos      The current position array [row, col] (modified by this
     *                 method).
     * @param ad       The adjacency type (sides or sides + vertices).
     * @param topology The topology type ("H", "Q", or "T").
     * @param parity   The parity value for triangular topology.
     * @return The updated parity value (only for triangular topology)
     */
    private boolean setValue(ArrayList<ArrayList<Integer>> board, int val, int[] pos, Adjacency ad, String topology) {
        ArrayList<Integer[]> possibleNexts = getPossibleNexts(board, pos, ad, topology);
        if (possibleNexts.isEmpty()) {
            return false;
        }
        Random rand = new Random();
        int next = rand.nextInt(possibleNexts.size());
        int[] dis = { possibleNexts.get(next)[0], possibleNexts.get(next)[1] };
        pos[0] += dis[0];
        pos[1] += dis[1];
        // Check if position is within bounds
        if (pos[0] >= 0 && pos[0] < board.size() && pos[1] >= 0 && pos[1] < board.get(0).size()) {
            // Update the board with the new value
            board.get(pos[0]).set(pos[1], val);
            return true;
        } else {
            // Position is out of bounds - expand the board to accommodate it
            expandBoard(board, pos, topology); // Pass pos array directly
            board.get(pos[0]).set(pos[1], val);
            return true;
        }
    }

    /**
     * Converts the board representation to a formatted string.
     *
     * @param board Matrix of integers where -1 is an empty cell.
     * @return The hidato in a valid string format.
     */
    private String convert(ArrayList<ArrayList<Integer>> board) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < board.size(); i++) {
            ArrayList<Integer> row = board.get(i);
            for (int j = 0; j < row.size(); j++) {
                int value = row.get(j);
                if (value == -1) {
                    result.append("#");
                } else {
                    result.append(value);
                }
                if (j < row.size() - 1) {
                    result.append(",");
                }
            }
            if (i < board.size() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Expands the board to accommodate a position outside current bounds ensuring
     * that all rows have the same number of columns after the operation.
     *
     * @param board    The board matrix to expand (modified in place).
     * @param pos      The position (row, col) that triggered expansion.
     * @param topology The topology code ("T","Q","H") which affects parity rules.
     */
    private void expandBoard(ArrayList<ArrayList<Integer>> board, int[] pos, String topology) {
        int row = pos[0];
        int col = pos[1];

        // Handle row expansion
        // If row is negative we have to add 2 rows in order to maintain the parity of
        // the rows to maintain the already defined cells coherent
        if (row < 0) {
            // Add rows at the beginning
            int rowsToAdd = -row;
            if (topology.equals("T") || topology.equals("H"))
                rowsToAdd = 2;
            int currentColLength = board.isEmpty() ? 0 : board.get(0).size();

            for (int i = 0; i < rowsToAdd; i++) {
                ArrayList<Integer> newRow = new ArrayList<>();
                for (int j = 0; j < currentColLength; j++) {
                    newRow.add(-1);
                }
                board.add(0, newRow);
            }
            // Update pos[0] since we added rows at the beginning
            pos[0] = row + rowsToAdd;
        } else if (row >= board.size()) {
            // Add rows at the end
            int rowsToAdd = row - board.size() + 1;
            int currentColLength = board.isEmpty() ? 0 : board.get(0).size();

            for (int i = 0; i < rowsToAdd; i++) {
                ArrayList<Integer> newRow = new ArrayList<>();
                for (int j = 0; j < currentColLength; j++) {
                    newRow.add(-1);
                }
                board.add(newRow);
            }
        }

        // Handle column expansion
        int currentColLength = board.isEmpty() ? 0 : board.get(0).size();

        if (col < 0) {
            // Add columns at the beginning
            // If col is negative we have to add 2 cols in order to mantain the overall
            // parity
            int colsToAdd = -col;
            if (topology.equals("T"))
                colsToAdd = 2;
            for (int i = 0; i < board.size(); i++) {
                ArrayList<Integer> currentRow = board.get(i);
                for (int j = 0; j < colsToAdd; j++) {
                    currentRow.add(0, -1);
                }
                board.set(i, currentRow);
            }
            // Update pos[1] since we added columns at the beginning
            pos[1] = col + colsToAdd;

        }
        if (col >= currentColLength) {
            // Add columns at the end
            int colsToAdd = col - currentColLength + 1;
            for (int i = 0; i < board.size(); i++) {
                ArrayList<Integer> currentRow = board.get(i);
                for (int j = 0; j < colsToAdd; j++) {
                    currentRow.add(-1);
                }
                board.set(i, currentRow);
            }
        }
    }
}
