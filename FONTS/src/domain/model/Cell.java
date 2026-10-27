package domain.model;

/**
 * Represents a single cell within a Hidato board.
 * A cell is defined by its coordinates (row, column) and its state,
 * which includes its value and whether it is a fixed number or a hole.
 */
public class Cell {
    /** The row index of the cell in the board matrix. **/
    private int row_;
    /** The column index of the cell in the board matrix. **/
    private int col_;
    /** The numerical value contained in the cell (0 if empty). **/
    private int value_;
    /** Indicates if the cell's value was part of the initial board setup and cannot be changed. **/
    private boolean fixed_;
    /** Indicates if the cell is a "hole" or outside the valid playing area. **/
    private boolean hole_;

    /** * Constructor to initialize a Cell with specific attributes.
     * * @param row The row index of the cell. Must be non-negative.
     * @param col The column index of the cell. Must be non-negative.
     * @param value The numerical value to assign to the cell.
     * @param fixed True if the value is predefined and unchangeable.
     * @param hole True if the cell is not part of the playable board area.
     * @throws IllegalArgumentException if row or col are negative.
     **/
    public Cell(int row, int col, int value, boolean fixed, boolean hole) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative.");
        }
        this.row_ = row;
        this.col_ = col;
        this.value_ = value;
        this.fixed_ = fixed;
        this.hole_ = hole;
    }

    /** * Gets the row index of the cell.
     * @return The row coordinate.
     **/
    public int getRow() {
        return row_;
    }

    /** * Sets the row index of the cell.
     * @param row The new row coordinate.
     **/
    public void setRow(int row) {
        this.row_ = row;
    }

    /** * Gets the column index of the cell.
     * @return The column coordinate.
     **/
    public int getCol() {
        return col_;
    }

    /** * Sets the column index of the cell.
     * @param col The new column coordinate.
     **/
    public void setCol(int col) {
        this.col_ = col;
    }

    /** * Gets the current value of the cell.
     * @return An integer representing the cell's value.
     **/
    public int getValue() {
        return value_;
    }

    /** * Sets the value of the cell if it is not fixed and not a hole.
     * @param value The new integer value to set.
     **/
    public void setValue(int value) {
        if (!fixed_ && !hole_) {
            this.value_ = value;
        }
    }

    /** * Checks if the cell is a fixed cell.
     * @return True if the cell is fixed, false otherwise.
     **/
    public boolean isFixed() {
        return fixed_;
    }

    /** * Sets whether the cell should be considered fixed.
     * @param fixed Boolean indicating the fixed status.
     **/
    public void setFixed(boolean fixed) {
        this.fixed_ = fixed;
    }

    /** * Checks if the cell is a hole.
     * @return True if the cell is a hole, false otherwise.
     **/
    public boolean isHole() {
        return hole_;
    }

    /** * Sets whether the cell is a hole.
     * @param hole Boolean indicating the hole status.
     **/
    public void setHole(boolean hole) {
        this.hole_ = hole;
    }

    /** * Determines if the cell is currently empty.
     * A cell is considered empty if it is not a hole and its value is 0.
     * @return True if empty, false if it contains a value or is a hole.
     **/
    public boolean isEmpty() {
        return !hole_ && value_ == 0;
    }
}