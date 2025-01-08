/**
 * Represents a cell entry in a 2D spreadsheet, implementing the Index2D interface.
 * Provides access to the cell's x and y coordinates and ensures validity.
 */
public class CellEntry implements Index2D {
    private final int x, y;

    /**
     * Constructs a CellEntry with the given x and y coordinates.
     *
     * @param x The x-coordinate (column index) of the cell.
     * @param y The y-coordinate (row index) of the cell.
     */
    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isValid() {
        return x >= 0 && y >= 0;
    }

    /**
     * Returns a string representation of the cell, in the format "A1", "B2", etc.
     * Assumes that x corresponds to a column starting with 'A' and y is 1-based.
     *
     * @return The string representation of the cell.
     */
    @Override
    public String toString() {
        char column = (char) ('A' + x); // Convert x to column letter
        int row = y + 1; // Convert y to 1-based index
        return column + String.valueOf(row);
    }
}
