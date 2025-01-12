import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    /**
     * Constructor to initialize the spreadsheet with given dimensions.
     * @param x Width of the spreadsheet.
     * @param y Height of the spreadsheet.
     */
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Default dimensions
    }

    /**
     * Retrieves the computed value of a cell at the specified coordinates.
     *
     * @param x Column index of the cell.
     * @param y Row index of the cell.
     * @return The value of the cell as a string.
     */

    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) {
            return Ex2Utils.EMPTY_CELL;
        }
        SCell cell = (SCell) table[x][y];
        switch (cell.getType()) {
            case Ex2Utils.FORM:
                // If formula => compute and return the numeric result
                try {
                    double result = cell.computeForm(cell.getData(), this);
                    return String.format("%.1f", result);
                } catch (Exception e) {
                    return Ex2Utils.ERR_FORM; // e.g., parse error or invalid ref
                }
            case Ex2Utils.ERR_FORM_FORMAT:
                // If formula is recognized as invalid show ERR_FORM
                return Ex2Utils.ERR_FORM;
            case Ex2Utils.ERR_CYCLE_FORM:
                // If formula has a circular dependency show ERR_CYCLE
                return Ex2Utils.ERR_CYCLE;
            default:
                // For numeric or text cells => return toString()
                return cell.toString();
        }
    }


    @Override
    public Cell get(int x, int y) {
        if (isIn(x, y)) {
            return table[x][y];
        }
        return null;
    }
    /**
     * Retrieves the cell at the specified cell reference (e.g., "A0").
     *
     * @param cords The string representation of the cell's coordinates.
     * @return The cell object, or null if the reference is invalid.
     */

    @Override
    public Cell get(String cords) {
        if (cords == null || cords.length() < 2) {
            return null; // invalid input
        }
        String column = cords.substring(0, 1).toUpperCase();
        String rowStr = cords.substring(1);

        int x = -1;
        for (int i = 0; i < Ex2Utils.ABC.length; i++) {
            if (Ex2Utils.ABC[i].equalsIgnoreCase(column)) {
                x = i;
                break;
            }
        }
        if (x == -1) return null; // invalid column

        int y;
        try {
            y = Integer.parseInt(rowStr); // "0" -> 0, "1" -> 1, etc.
        } catch (NumberFormatException e) {
            return null;
        }

        if (!isIn(x, y)) return null;

        return get(x, y);
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    /**
     * Updates the value of a cell at the specified coordinates.
     *
     * @param x Column index of the cell.
     * @param y Row index of the cell.
     * @param s The new value to set in the cell.
     */

    @Override
    public void set(int x, int y, String s) {
        if (isIn(x, y)) {
            table[x][y] = new SCell(s); // Update cell content
            eval();
        }
    }

    /**
     * Evaluates all cells in the spreadsheet, resolving formulas and dependencies.
     */

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                SCell cell = (SCell) table[x][y];

                if (cell.getType() == Ex2Utils.FORM) {

                    int depthResult = calculateDepth(x, y, new boolean[width()][height()]);
                    if (depthResult == Ex2Utils.ERR) {
                        // Mark formula as cycle
                        cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                    } else {
                        eval(x, y); // Normal evaluation
                    }
                } else {
                    eval(x, y); // Evaluate numeric or text
                }
            }
        }
    }
    /**
     * Checks if the specified coordinates are within the spreadsheet bounds.
     *
     * @param xx Column index to check.
     * @param yy Row index to check.
     * @return True if the coordinates are within bounds, otherwise false.
     */

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && xx < width() && yy >= 0 && yy < height();
    }

    /**
     * Calculates the dependency depth of all cells in the spreadsheet.
     *
     * @return A 2D array representing the depth of each cell.
     */

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                ans[x][y] = calculateDepth(x, y, new boolean[width()][height()]);
            }
        }
        return ans;
    }

    /**
     * Recursively calculates the depth of a cell based on its dependencies.
     *
     * @param x       Column index of the cell.
     * @param y       Row index of the cell.
     * @param visited Tracks visited cells to prevent infinite recursion.
     * @return The depth of the cell, or Ex2Utils.ERR for errors.
     */

    public int calculateDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y) || visited[x][y]) {
            return Ex2Utils.ERR; // circular or invalid
        }
        visited[x][y] = true;
        SCell cell = (SCell) get(x, y);

        // Non-formula => depth=0
        if (cell.getType() != Ex2Utils.FORM) {
            visited[x][y] = false;
            return 0;
        }

        if (cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
            visited[x][y] = false;
            return Ex2Utils.ERR;
        }

        // For formula => 1 + max depth(dependencies)
        int maxDepth = 0;
        for (Index2D dependency : extractDependencies(cell.getData())) {
            int depX = dependency.getX();
            int depY = dependency.getY();
            if (isIn(depX, depY)) {
                SCell depCell = (SCell) get(depX, depY);
                if (depCell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
                    visited[x][y] = false;
                    return Ex2Utils.ERR;
                }
                int depDepth = calculateDepth(depX, depY, visited);
                if (depDepth == Ex2Utils.ERR) {
                    visited[x][y] = false;
                    return Ex2Utils.ERR;
                }
                maxDepth = Math.max(maxDepth, depDepth);
            }
        }
        visited[x][y] = false;
        return 1 + maxDepth;
    }

    /**
     * Extracts all cell references from a formula.
     *
     * @param formula The formula string to analyze.
     * @return A list of cell references found in the formula.
     */

    public List<Index2D> extractDependencies(String formula) {
        List<Index2D> dependencies = new ArrayList<>();
        // remove '=' for simpler splitting
        String expr = formula.startsWith("=") ? formula.substring(1) : formula;
        for (String token : expr.split("[^A-Za-z0-9]+")) {
            if (token.matches("(?i)[A-Z][0-9]+")) {
                String col = token.substring(0, 1);
                String row = token.substring(1);
                int x = columnToIndex(col);
                int y;
                try {
                    y = Integer.parseInt(row);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (x >= 0 && y >= 0) {
                    dependencies.add(new CellEntry(x, y));
                }
            }
        }
        return dependencies;
    }

    /**
     * Converts a column letter to its corresponding index.
     *
     * @param column The column letter (e.g., "A").
     * @return The index of the column, or -1 if invalid.
     */

    public int columnToIndex(String column) {
        for (int i = 0; i < Ex2Utils.ABC.length; i++) {
            if (Ex2Utils.ABC[i].equalsIgnoreCase(column)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid data format");
                }

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String cellData = parts[2];

                if (table == null || x >= table.length || y >= table[0].length) {
                    int newWidth = Math.max(table != null ? table.length : 0, x + 1);
                    int newHeight = Math.max(table != null && table[0] != null ? table[0].length : 0, y + 1);
                    SCell[][] newTable = new SCell[newWidth][newHeight];

                    if (table != null) {
                        for (int i = 0; i < table.length; i++) {
                            System.arraycopy(table[i], 0, newTable[i], 0, table[i].length);
                        }
                    }

                    table = newTable;
                }

                table[x][y] = new SCell(cellData);
            }

            eval();
        }
    }

    @Override
    public void save(String fileName) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("I2CS ArielU: SpreadSheet (Ex2) assignment: ");

            for (int x = 0; x < table.length; x++) {
                for (int y = 0; y < table[x].length; y++) {
                    SCell cell = (SCell) table[x][y];
                    if (cell != null && !cell.getData().isEmpty()) {
                        pw.println(x + "," + y + "," + cell.getData().replace(",", ";"));
                    }
                }
            }
        }
    }
    /**
     * Evaluates a specific cell and returns its value as a string.
     *
     * @param x The column index of the cell.
     * @param y The row index of the cell.
     * @return The evaluated value of the cell.
     */

    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        SCell cell = (SCell) get(x, y);

        if (cell.getType() == Ex2Utils.FORM) {
            try {
                double result = cell.computeForm(cell.getData(), this);
                return String.valueOf(result);
            } catch (Exception e) {
                return Ex2Utils.ERR_FORM;
            }
        }
        return cell.toString();
    }
}
