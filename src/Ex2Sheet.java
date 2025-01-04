import java.io.IOException;
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
                table[i][j] = new SCell(""); // Initialize empty cells
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Default dimensions
    }

    @Override
    public String value(int x, int y) {
        if (isIn(x, y)) {
            return table[x][y].toString();
        }
        return Ex2Utils.EMPTY_CELL;
    }

    @Override
    public Cell get(int x, int y) {
        if (isIn(x, y)) {
            return table[x][y];
        }
        return null;
    }

    @Override
    public Cell get(String cords) {
        if (cords == null || cords.length() < 2) {
            return null; // Invalid input
        }

        // Extract column and row
        String column = cords.substring(0, 1).toUpperCase();
        String row = cords.substring(1);

        // Convert column to index
        int x = -1;
        for (int i = 0; i < Ex2Utils.ABC.length; i++) {
            if (Ex2Utils.ABC[i].equals(column)) {
                x = i;
                break;
            }
        }

        if (x == -1) {
            return null; // Invalid column
        }

        // Convert row to index
        int y;
        try {
            y = Integer.parseInt(row);
        } catch (NumberFormatException e) {
            return null; // Invalid row
        }

        // Ensure coordinates are within bounds
        if (!isIn(x, y)) {
            return null; // Out-of-bounds coordinates
        }

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

    @Override
    public void set(int x, int y, String s) {
        if (isIn(x, y)) {
            table[x][y] = new SCell(s); // Update cell content
            eval();
        }
    }

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y);
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && xx < width() && yy >= 0 && yy < height(); // Check if coordinates are in bounds
    }

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

    private int calculateDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y) || visited[x][y]) {
            return Ex2Utils.ERR; // Circular dependency detected
        }

        visited[x][y] = true;
        SCell cell = (SCell) get(x, y);

        if (cell.getType() != Ex2Utils.FORM) {
            return 0; // Non-formula cells have depth 0
        }

        int maxDepth = 0;
        for (Index2D dependency : extractDependencies(cell.getData())) {
            int depX = dependency.getX();
            int depY = dependency.getY();
            if (isIn(depX, depY)) {
                int depDepth = calculateDepth(depX, depY, visited);
                if (depDepth == Ex2Utils.ERR) {
                    return Ex2Utils.ERR; // Circular dependency
                }
                maxDepth = Math.max(maxDepth, depDepth);
            }
        }
        visited[x][y] = false;
        return 1 + maxDepth;
    }


    private List<Index2D> extractDependencies(String formula) {
        List<Index2D> dependencies = new ArrayList<>();

        // Split the formula into potential cell references
        for (String token : formula.split("[^A-Za-z0-9]+")) {
            if (token.matches("[A-Z][0-9]+")) { // Match valid cell references like A1, B2
                // Extract column letter and row number
                String column = token.substring(0, 1);
                String row = token.substring(1);

                // Convert column to index
                int x = columnToIndex(column);
                int y;

                try {
                    y = Integer.parseInt(row); // Convert row to an integer
                } catch (NumberFormatException e) {
                    continue; // Skip invalid rows
                }

                if (x >= 0 && y >= 0) { // Valid indices
                    dependencies.add(new CellEntry(x, y)); // Add dependency as an Index2D
                }
            }
        }

        return dependencies;
    }

    private int columnToIndex(String column) {
        for (int i = 0; i < Ex2Utils.ABC.length; i++) {
            if (Ex2Utils.ABC[i].equalsIgnoreCase(column)) {
                return i; // Return the index of the column
            }
        }
        return -1; // Return -1 if the column is not found
    }


    @Override
    public void load(String fileName) throws IOException {
        // Add implementation for loading spreadsheet from file
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add implementation for saving spreadsheet to file
    }

    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;

        SCell cell = (SCell) get(x, y);
        if (cell.getType() == Ex2Utils.FORM) {
            try {
                return String.valueOf(cell.computeForm(cell.getData()));
            } catch (Exception e) {
                return Ex2Utils.ERR_FORM;
            }
        }
        return cell.toString();
    }
}
