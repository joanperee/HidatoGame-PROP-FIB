package presentation.classes;

import javax.swing.*;
import java.awt.*;

/**
 * This class has the ability to paint any given Hidato.
 * We used Graphics, Shape, Rectangle and Polygon classes.
 * 
 * We declare the usage of the following SOURCE:
 * https://www.redblobgames.com/grids/parts/
 * 
 * It was helpful to: know how to paint each cell and
 * the formulas used to do so. A lot of trial and error
 * was used to get the desired shapes.
 */
public class HidatoBoard extends JPanel {
    /** Private enummeration to make it easier to paint a board. */
    private enum CellType {
        SQUARE, HEXAGON, TRIANGLE
    }

    /** Each Hidato has a cellType. */
    private CellType cellType;

    /** Each Hidato board is a matrix, as the format requires. */
    private String[][] grid;

    /** Attributes calculated parsing the grid. */
    private int rows, cols;

    /** Length of a default side of the cell (pixels). */
    private int CELL_SIZE = 60;

    /**
     * This is a matrix to stor the generated shapes to then be used to detect which
     * one the player is clicking.
     */
    private Shape[][] cellShapes;

    /**
     * This is a matrix to store the numbers initially given to the player so that
     * he cant remove them
     */
    private boolean[][] isGiven;

    /** The information of the hidato (topology, adjacency, rows, cols) */
    private String originalHeader;

    /**
     * Constructor for an instance of the HidatoBoard class.
     * 
     * @param board Represents an hidato in a String format.
     */
    public HidatoBoard(String board) {
        parseBoard(board);
    }

    /**
     * As the Hidato is generated and presented through a
     * String, this function parses it into a matrix String[][].
     * 
     * @param board Represents an hidato in a String format.
     */
    private void parseBoard(String board) {
        String[] lines = board.trim().split("\n");
        originalHeader = lines[0].trim();
        String[] header = lines[0].trim().split(",");
        String typecell = header[0].trim();

        if (typecell.equals("Q"))
            cellType = CellType.SQUARE;
        else if (typecell.equals("H"))
            cellType = CellType.HEXAGON;
        else
            cellType = CellType.TRIANGLE;

        // The adjacency is header[1] but it is not
        // necessary to know it in the painting. This
        // class only paints, it does not implement logic.

        // Rows and cols
        rows = Integer.parseInt(header[2].trim());
        cols = Integer.parseInt(header[3].trim());

        cellShapes = new Shape[rows][cols];
        grid = new String[rows][cols];
        isGiven = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            // Vector of the rows remaining
            String[] cells = lines[i + 1].trim().split(",", -1);
            for (int j = 0; j < cols; j++) {
                if (j < cells.length) {
                    String val = cells[j].trim();

                    if (val.isEmpty()) {
                        val = "#";
                    }

                    grid[i][j] = val;

                    if (!grid[i][j].equals("?") && !grid[i][j].equals("#") && !grid[i][j].equals("*")) {
                        isGiven[i][j] = true;
                    }
                } else {
                    grid[i][j] = "#";
                }
            }

        }
    }

    /**
     * This function paints all the cells through the Graphics
     * AWT class. It is called every time the super JPanel is
     * repainted, so it must be overwritten from the class.
     * 
     * @param g Graphics object used to store the components' info.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = (getWidth() - 30) / cols;
        int h = (getHeight() - 30) / rows;
        int newCellSize = Math.max(Math.min(w, h), 60);
        this.CELL_SIZE = newCellSize;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (cellType) {
                    case SQUARE:
                        drawSquare(g2, i, j);
                        break;
                    case HEXAGON:
                        drawHexagon(g2, i, j);
                        break;
                    case TRIANGLE:
                        drawTriangle(g2, i, j);
                        break;
                }
            }
        }
    }

    /**
     * Draws a square given a row r and a column c.
     * 
     * @param g2 Graphics 2D (Swing) object used to store the components' info.
     * @param r  The row where it will be painted.
     * @param c  The column where it will be painted.
     */
    private void drawSquare(Graphics2D g2, int r, int c) {
        String val = grid[r][c];
        int x = c * CELL_SIZE + 10;
        int y = r * CELL_SIZE + 10;

        Rectangle rect = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);

        cellShapes[r][c] = rect;
        fillAndBorder(g2, rect, val, r, c);
        drawLabel(g2, val, x + CELL_SIZE / 2, y + CELL_SIZE / 2);
    }

    /**
     * Draws an hexagon given a row r and a column c.
     * 
     * @param g2 Graphics 2D (Swing) object used to store the components' info.
     * @param r  The row where it will be painted.
     * @param c  The column where it will be painted.
     */
    private void drawHexagon(Graphics2D g2, int r, int c) {
        String val = grid[r][c];
        double radius = CELL_SIZE * 0.55;
        double rowH = radius * Math.sqrt(3);
        double colW = radius * 1.5;
        // if the line is odd then an offset of apotema is applied
        double offsetX = (r % 2 == 1) ? (radius * Math.sqrt(3) / 2) : 0;
        double cx = c * rowH + offsetX + (radius * Math.sqrt(3) / 2) + 10;
        double cy = r * colW + radius + 10;

        Polygon hex = hexagonShape(cx, cy, radius);

        cellShapes[r][c] = hex;
        fillAndBorder(g2, hex, val, r, c);
        drawLabel(g2, val, (int) cx, (int) cy);
    }

    /**
     * Helper function to paint an hexagon. Given the coordinates
     * cx and cy along with a desired radius it computes each
     * side, which is given applying a 60º rotation to the prev one.
     * 
     * @param cx     Coordenate x of the Pane where to paint it.
     * @param cy     Coordenate y of the Pane where to paint it.
     * @param radius
     * @return The coordinates for the hexagon, which is a Polygon.
     */
    private Polygon hexagonShape(double cx, double cy, double radius) {
        int[] xs = new int[6];
        int[] ys = new int[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i + 30);
            xs[i] = (int) (cx + radius * Math.cos(angle));
            ys[i] = (int) (cy + radius * Math.sin(angle));
        }
        return new Polygon(xs, ys, 6);
    }

    /**
     * Draws a triangle given row r and column c.
     * 
     * @param g2 Graphics 2D (Swing) object used to store the components' info.
     * @param r  The row where it will be painted.
     * @param c  The column where it will be painted.
     */
    private void drawTriangle(Graphics2D g2, int r, int c) {
        String val = grid[r][c];
        double base = CELL_SIZE;
        double h = base * Math.sqrt(3) / 2;
        double yTop = r * h + 10;
        double xLeft = c * (base / 2) + 10;
        // A row r, col c: points up if (r+c) is even, down if (r+c) is odd
        boolean pointsUp = (r + c) % 2 == 0;

        Polygon tri;
        if (pointsUp) {
            int x1 = (int) xLeft;
            int y1 = (int) (yTop + h);
            int x2 = (int) (xLeft + base);
            int y2 = (int) (yTop + h);
            int x3 = (int) (xLeft + base / 2);
            int y3 = (int) yTop;
            tri = new Polygon(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, 3);
        } else {
            int x1 = (int) xLeft;
            int y1 = (int) yTop;
            int x2 = (int) (xLeft + base);
            int y2 = (int) yTop;
            int x3 = (int) (xLeft + base / 2);
            int y3 = (int) (yTop + h);
            tri = new Polygon(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, 3);
        }

        cellShapes[r][c] = tri;
        fillAndBorder(g2, tri, val, r, c);
        int cx = (int) (xLeft + base / 2);
        int cy = pointsUp ? (int) (yTop + h * 2 / 3) : (int) (yTop + h / 3);
        drawLabel(g2, val, cx, cy);
    }

    /**
     * This function adds graphic information to each cell
     * depending on its value (number, hole "*", or empty "?"). Each cell
     * (tri, hex, square) is passed as a Polygon and direcly
     * converted to a Shape.
     * 
     * @param g2    Graphics 2D (Swing) object used.
     * @param shape The type of shape to be fillen (Hex, Sq, Tri).
     * @param val   Its value (#, *, ?, number).
     * @param r     The row representing the shape in the hidato matrix.
     * @param c     The column representing the shape in the hidato matrix.
     */
    private void fillAndBorder(Graphics2D g2, Shape shape, String val, int r, int c) {

        if (val.equals("*") || val.equals("#")) {
            // DARK FOR HOLES OR INACCESSIBLE CELLS
            g2.setColor(Color.BLACK);
            g2.fill(shape);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(shape);
        } else if (val.equals("?")) {
            // WHITE FOR EMPTY SOLVABLE CELLS
            g2.setColor(Color.WHITE);
            g2.fill(shape);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(shape);
        } else {
            if (isGiven(r, c)) {
                // GREY FOR INITIALLY SOLVED CELLS
                g2.setColor(Color.LIGHT_GRAY);
            } else {
                // BLUE FOR USER-SOLVED CELLS
                g2.setColor(Color.CYAN);
            }
            g2.fill(shape);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(shape);
        }
    }

    /**
     * If needed, the cell gets the numeric value (label) inside
     * its Shape.
     * 
     * @param g2
     * @param val
     * @param cx
     * @param cy
     */
    private void drawLabel(Graphics2D g2, String val, int cx, int cy) {
        if (!val.equals("*") && !val.equals("#") && !val.equals("?")) {
            g2.setColor(Color.BLACK);
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx - fm.stringWidth(val) / 2;
            int ty = cy + fm.getAscent() / 2 - 2;
            g2.drawString(val, tx, ty);
        }
    }

    /**
     * Function to update the board with the newBoard.
     * That is, when the user changes the current state.
     * 
     * @param newBoard
     */
    public void updateBoard(String newBoard) {
        parseBoard(newBoard);
        repaint();
    }

    /**
     * This function is triggered by the method paintComponent.
     * That's why it is overwritten by the new dimension which takse
     * into account the current CELL_SIZE of the hidato. We sum +20
     * so the dimension has a bit or margins and does not cut any cell.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(cols * CELL_SIZE + 20, rows * CELL_SIZE + 20);
    }

    /**
     * This function calculates which cell is in some coordinates.
     * 
     * @param x The x coordinate of the mouse click.
     * @param y The y coordinate of the mouse click.
     * @return Returns the clicked cell (row and column) or NULL if none
     */
    public int[] getCellAt(int x, int y) {
        if (cellShapes == null)
            return null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Shape shape = cellShapes[r][c];

                if (shape != null && shape.contains(x, y)) {

                    // the clicks on # and * are ignored
                    if (!grid[r][c].equals("#") && !grid[r][c].equals("*")) {
                        return new int[] { r, c };
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the value on for the cell on row r and column c.
     * 
     * @param r Row.
     * @param c Column.
     * @return The value of the corresponding cell
     */
    public String getCellValue(int r, int c) {
        return grid[r][c];
    }

    /**
     * Tells whether the value for cell on row r and column c
     * was already given or filled by the user.
     * 
     * @param r Row.
     * @param c Column.
     * @return Whether it's given or not.
     */
    public boolean isGiven(int r, int c) {
        return isGiven != null && isGiven[r][c];
    }

    /**
     * Sets the value of cell on row r and column c and paints it.
     * 
     * @param r   Row.
     * @param c   Column.
     * @param val Value to set.
     */
    public void setCellValue(int r, int c, String val) {
        grid[r][c] = val;
        repaint();
    }

    /**
     * Converts the current board to String format.
     * 
     * @return The String containing the current board.
     */
    public String exportBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append(originalHeader).append("\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]);
                if (j < cols - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Updates the isGiven matrix by comparing the current board with the original board.
     * Any cell that was a hole ('?') in the original board is marked as NOT given,
     * allowing the player to edit it.
     * * @param originalBoard Represents the initial state of the hidato in String format.
     */
    public void setOriginalBoard(String originalBoard) {
        if (originalBoard == null || originalBoard.isEmpty()) return;
        
        String[] lines = originalBoard.trim().split("\n");
        for (int i = 0; i < rows; i++) {
            
            if (i + 1 >= lines.length) break; 
            
            String[] cells = lines[i + 1].trim().split(",", -1);
            for (int j = 0; j < cols; j++) {
                if (j < cells.length) {
                    String originalVal = cells[j].trim();
                    
                    if (originalVal.equals("?")) {
                        isGiven[i][j] = false;
                    }
                }
            }
        }
        
        repaint(); 
    }
}