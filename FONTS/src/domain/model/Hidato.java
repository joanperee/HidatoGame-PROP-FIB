package domain.model;

import java.util.*;
import domain.enumeration.Difficulty;
import domain.enumeration.Adjacency;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Abstract class representing the Hidato puzzle.
 * It contains the basic structure, attributes, and file-parsing logic 
 * required to manage an Hidato game
 */
public abstract class Hidato implements Cloneable {
    /** UUID of the Hidato. */
    private UUID idH_;
    /** The Hidato's difficulty level*/
    private Difficulty difficulty_;
    /** The Hidato's adjacency type */
    private Adjacency adjacency_;
    /** Number of rows of the Hidato.*/
    private int rows_;
    /** Number of columns of the Hidato. */
    private int cols_;
    /** Number of holes of the Hidato. */
    private int numHoles_;
    /** Maximum number that can be a part of the Hidato.*/
    private int maxValue_;
    /** Matrix representation of the Hidato.*/
    private int[][] representation_;
    /** List of scores on the Hidato.*/
    private int[] highestScore_;
    /** List of all cells on the Hidato.*/
    private ArrayList<Cell> cells_;

    /**
     * Constructor by ID and Cell parameters.
     * @param idH Unique identifier for the Hidato.
     * @param numCells Total number of cells (between 3 and 200).
     * @param numHoles Number of non-accessible cells in the board.
     * @throws IllegalArgumentException if numCells is not within the range [3, 200].
     */
    public Hidato(UUID idH, int numCells, int numHoles) {
        if (numCells < 3 || numCells > 200) {
            throw new IllegalArgumentException("The number of cells must be between 3 and 200.");
        }
        this.idH_ = idH;
        this.numHoles_ = numHoles;
        this.cells_ = new ArrayList<>(numCells);
    }

    /**
     * Method that loads an Hidato from raw data string.
     */
    public static Hidato loadFromString(String boardData) {
        if (boardData == null || boardData.trim().isEmpty()) return null;

        try (Scanner scanner = new Scanner(boardData)) {
            if (scanner.hasNextLine()) {
                String headerLine = scanner.nextLine();
                String[] header = headerLine.split(",");
                String type = header[0].trim();

                switch (type) {
                    case "Q": return new Square(boardData);
                    case "H": return new Hexagon(boardData);
                    case "T": return new Triangle(boardData);
                    default: return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Method that converts an Hidato to raw data string.
     */
    public static String stringFromHidato(Hidato hidato) {
        if (hidato == null) return "";

        StringBuilder sb = new StringBuilder();

        String type = "";
        if (hidato instanceof Square) {
            type = "Q";
        } else if (hidato instanceof Hexagon) {
            type = "H";
        } else if (hidato instanceof Triangle) {
            type = "T";
        }

        String adjacency = (hidato.getAdjacency() == Adjacency.CA) ? "CA" : "C";

        sb.append(type).append(",")
                .append(adjacency).append(",")
                .append(hidato.getRows()).append(",")
                .append(hidato.getCols()).append("\n");

        int[][] representation = hidato.getRepresentation();
        for (int r = 0; r < hidato.getRows(); r++) {
            for (int c = 0; c < hidato.getCols(); c++) {
                int val = representation[r][c];

                if (val == -1) {
                    sb.append("*");
                } else if (val == 0) {
                    sb.append("?");
                } else {
                    sb.append(val);
                }

                if (c < hidato.getCols() - 1) {
                    sb.append(",");
                }
            }
            if (r < hidato.getRows() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Loads an Hidato from a file.
     * * @param filepath Path to the file containing the Hidato data.
     * @return An instance of Square, Hexagon, or Triangle, or null if an error occurs.
     */
    public static Hidato loadFromFile(String filepath) {
        try (Scanner scanner = new Scanner(new File(filepath))) {
            StringBuilder data = new StringBuilder();
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine()).append("\n");
            }
            // Pass the fully read file string to the string parser
            return loadFromString(data.toString());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filepath);
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Constructor that parses the raw string to initialize the Hidato board.
     * * @param boardData The raw data string of the board.
     */
    protected Hidato(String boardData) {
        this.cells_ = new ArrayList<>();
        this.numHoles_ = 0;
        this.maxValue_ = 0;

        try (Scanner scanner = new Scanner(boardData)) {
            if (scanner.hasNextLine()) {
                String[] header = scanner.nextLine().split(","); 
                
                if (header.length < 4) {
                    throw new InputMismatchException("Invalid header format. Expected 4 parameters.");
                }

                String adjStr = header[1].trim();
                this.adjacency_ = adjStr.equals("CA") ? Adjacency.CA : Adjacency.C;
                
                this.rows_ = Integer.parseInt(header[2].trim());
                this.cols_ = Integer.parseInt(header[3].trim());
                this.representation_ = new int[this.rows_][this.cols_];
                
                int r = 0;
                while (scanner.hasNextLine() && r < this.rows_) {
                    String[] line = scanner.nextLine().trim().split(","); 
                    for (int c = 0; c < this.cols_; c++) {
                        if (c >= line.length) throw new ArrayIndexOutOfBoundsException("Missing column data at row " + r);
                        
                        String cellStr = line[c].trim();
                        int val = 0;
                        boolean fixed = false;
                        boolean hole = false;
                        
                        if (cellStr.equals("#") || cellStr.equals("*")) { 
                            hole = true;
                            val = -1;
                        } else if (cellStr.equals("?")) { 
                            val = 0;
                        } else { 
                            try {
                                val = Integer.parseInt(cellStr);
                                fixed = true;
                                if (val > this.maxValue_) {
                                    this.maxValue_ = val;
                                }
                            } catch (NumberFormatException e) {
                                throw new InputMismatchException("Invalid cell value: " + cellStr);
                            }
                        }
                        
                        if (hole) {
                            this.numHoles_++;
                        }
                        
                        this.representation_[r][c] = val;
                        this.cells_.add(new Cell(r, c, val, fixed, hole));
                    }
                    r++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing board data: " + e.getMessage());
        }
    }

    /** @return The unique ID of the Hidato. **/
    public UUID getIdH() { return idH_; }
    /** @param idH The unique ID to set. **/
    public void setIdH(UUID idH) { this.idH_ = idH; }

    /** @return The difficulty level of the Hidato. **/
    public Difficulty getDifficulty() { return difficulty_; }
    /** @param difficulty The difficulty level to set. **/
    public void setDifficulty(Difficulty difficulty) { this.difficulty_ = difficulty; }

    /** @return The adjacency type (C or CA). **/
    public Adjacency getAdjacency() { return adjacency_; }
    /** @param adjacency The adjacency type to set. **/
    public void setAdjacency(Adjacency adjacency) { this.adjacency_ = adjacency; }

    /** @return The number of rows in the board. **/
    public int getRows() { return rows_; }
    /** @param rows The number of rows to set. **/
    public void setRows(int rows) { this.rows_ = rows; }

    /** @return The number of columns in the board. **/
    public int getCols() { return cols_; }
    /** @param cols The number of columns to set. **/
    public void setCols(int cols) { this.cols_ = cols; }

    /** @return The total number of hole/non-accessible cells. **/
    public int getNumHoles() { return numHoles_; }
    /** @param numHoles The number of holes to set. **/
    public void setNumHoles(int numHoles) { this.numHoles_ = numHoles; }

    /** @return The highest number currently placed on the board. **/
    public int getMaxValue() { return maxValue_; }
    /** @param maxValue The maximum value to set. **/
    public void setMaxValue(int maxValue) { this.maxValue_ = maxValue; }

    /** @return The 2D integer matrix representation of the board. **/
    public int[][] getRepresentation() { return representation_; }
    /** @param representation The matrix to set. **/
    public void setRepresentation(int[][] representation) { this.representation_ = representation; }

    /** @return The highest scores recorded for this Hidato. **/
    public int[] getHighestScore() { return highestScore_; }
    /** @param highestScore The scores array to set. **/
    public void setHighestScore(int[] highestScore) { this.highestScore_ = highestScore; }

    /** @return The list of Cell objects within the board. **/
    public ArrayList<Cell> getCells() { return cells_; }
    /** @param cells The list of cells to set. **/
    public void setCells(ArrayList<Cell> cells) { this.cells_ = cells; }

    /**
     * Abstract method to get adjacent coordinates based on cell type and topology.
     * @param i Row index.
     * @param j Column index.
     * @return Collection of adjacent coordinates {row, col}.
     */
    public abstract Collection<int[]> getAdjacents(int i, int j);

    /**
     * Generates a square adjacency matrix for the graph representation of the board.
     * @return A 2D matrix where matrix[a][b] = 1 if cell a is adjacent to cell b.
     */
    public int[][] getAdjacencyMatrix() {
        int totalCells = rows_ * cols_;
        int[][] matrix = new int[totalCells][totalCells];

        for (int i = 0; i < rows_; i++) {
            for (int j = 0; j < cols_; j++) {
                int currentIndex = i * cols_ + j;
                Collection<int[]> adjacents = getAdjacents(i, j);
                
                if (adjacents != null) {
                    for (int[] adj : adjacents) {
                        int adjR = adj[0];
                        int adjC = adj[1];
                        
                        if (adjR >= 0 && adjR < rows_ && adjC >= 0 && adjC < cols_) {
                            int adjIndex = adjR * cols_ + adjC;
                            matrix[currentIndex][adjIndex] = 1;
                        }
                    }
                }
            }
        }
        return matrix;
    }

    /**
     * The number of total cells of the current hidato.
     * 
     * @return Rows*Cols as the hidato is represented by a grid.
     */
    public int getNumCells() { return rows_*cols_; }

    /**
     * Returns the maximum number of adjacencies possible for a cell 
     * based on the Hidato type and current adjacency rules.
     * @return The maximum theoretical neighbors a single cell can have.
     */
    public int maxAdjacencies() {
        Adjacency adjType = this.getAdjacency();
        
        if (this instanceof Square) {
            return (adjType == Adjacency.CA) ? 8 : 4;
        } 
        else if (this instanceof Hexagon) {
            return 6;
        } 
        else if (this instanceof Triangle) {
            return (adjType == Adjacency.CA) ? 12 : 3;
        }
        
        return 0;
    }

    /**
     * Creates and returns an identical copy of this Hidato.
     * Changes to the returned object will not affect the original instance.
     * @return A deep copy of the current Hidato.
     */
    public Hidato copyHidato() {
        try {
            Hidato copy = (Hidato) super.clone();
            
            // Deep copy the mutable arrays and collections
            if (this.representation_ != null) {
                copy.representation_ = new int[this.rows_][this.cols_];
                for (int i = 0; i < this.rows_; i++) {
                    copy.representation_[i] = this.representation_[i].clone();
                }
            }

            if (this.highestScore_ != null) {
                copy.highestScore_ = this.highestScore_.clone();
            }

            if (this.cells_ != null) {
                copy.cells_ = new ArrayList<>();
                for (Cell c : this.cells_) {
                    copy.cells_.add(new Cell(c.getRow(), c.getCol(), c.getValue(), c.isFixed(), c.isHole()));
                }
            }

            return copy;
            
        } catch (CloneNotSupportedException e) {
            System.err.println("Error during Hidato copy: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets the value of a cell based on its calculated ID.
     * Assumes the ID maps to a standard row-major index mapping using the number of columns.
     * @param id The ID of the cell (calculated as: row * cols_ + col + 1).
     * @param value The integer value to assign to the cell.
     */
    public void setCell(int id, int value) {
        int index = id - 1;
        int row = index / this.cols_; 
        int col = index % this.cols_;
        
        this.representation_[row][col] = value;
        
        if (index >= 0 && index < this.cells_.size()) {
            this.cells_.get(index).setValue(value);
        }
    }

    /**
     * Retrieves the current value of a cell based on its calculated ID.
     * @param id The ID of the cell (calculated as: row * cols_ + col + 1).
     * @return The numerical value contained in the cell.
     */
    public int getValue(int id) {
        int index = id - 1;
        int row = index / this.cols_;
        int col = index % this.cols_;
        return this.representation_[row][col];
    }

    /**
     * Checks if a cell is a hole based on its calculated ID.
     * @param id The ID of the cell (calculated as: row * cols_ + col + 1).
     * @return True if the cell is a hole, false otherwise.
     * @throws IndexOutOfBoundsException if the calculated index is out of bounds.
     */
    public boolean isHole(int id) {
        int index = id - 1;
        
        if (index >= 0 && index < this.cells_.size()) {
            return this.cells_.get(index).isHole();
        }
        
        throw new IndexOutOfBoundsException("Invalid cell ID: " + id);
    }
}