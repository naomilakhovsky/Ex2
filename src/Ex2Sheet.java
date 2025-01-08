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
        if (!isIn(x, y)) {
            return Ex2Utils.EMPTY_CELL;
        }
        // Safely cast to SCell
        SCell cell = (SCell) table[x][y];

        // If it's a formula => compute it and return the numeric result
        if (cell.getType() == Ex2Utils.FORM) {
            try {
                double result = cell.computeForm(cell.getData(), this);
                return String.format("%.1f", result);
            } catch (Exception e) {
                // Something like invalid reference or parse error
                return Ex2Utils.ERR_FORM;
            }
        }
        // If numeric or text => just use the existing toString() logic
        return cell.toString();
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
            return null; // invalid input
        }
        // 1) Extract column letter (e.g., "A") + row string (e.g., "0")
        String column = cords.substring(0, 1).toUpperCase();
        String rowStr = cords.substring(1);

        // 2) Convert column to x index
        int x = -1;
        for (int i = 0; i < Ex2Utils.ABC.length; i++) {
            if (Ex2Utils.ABC[i].equalsIgnoreCase(column)) {
                x = i;
                break;
            }
        }
        if (x == -1) return null; // invalid column

        // 3) Convert row to y index (NO "-1" ANYMORE!)
        int y;
        try {
            y = Integer.parseInt(rowStr); // "0" -> 0, "1" -> 1, etc.
        } catch (NumberFormatException e) {
            return null;
        }

        // 4) Check bounds
        if (!isIn(x, y)) return null;

        // 5) Return that cell
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
        // Evaluate all cells in the spreadsheet
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y);
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && xx < width() && yy >= 0 && yy < height();
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

        // For formula => 1 + max depth(dependencies)
        int maxDepth = 0;
        for (Index2D dependency : extractDependencies(cell.getData())) {
            int depX = dependency.getX();
            int depY = dependency.getY();
            if (isIn(depX, depY)) {
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

    public List<Index2D> extractDependencies(String formula) {
        List<Index2D> dependencies = new ArrayList<>();
        // remove '=' for simpler splitting
        String expr = formula.startsWith("=") ? formula.substring(1) : formula;
        for (String token : expr.split("[^A-Za-z0-9]+")) {
            if (token.matches("[A-Z][0-9]+")) {
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
        // Implementation not shown
    }

    @Override
    public void save(String fileName) throws IOException {
        // Implementation not shown
    }

    @Override
    public String eval(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        SCell cell = (SCell) get(x, y);

        // If it's a formula, try to compute
        if (cell.getType() == Ex2Utils.FORM) {
            try {
                double result = cell.computeForm(cell.getData(), this);
                return String.valueOf(result);
            } catch (Exception e) {
                return Ex2Utils.ERR_FORM; // e.g. invalid reference
            }
        }
        // else: numeric or text
        return cell.toString();
    }
}
