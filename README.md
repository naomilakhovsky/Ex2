**Ex2- spreadsheet project:**

## Overview
The spreadsheet is designed as a 2D grid of Cells, where each cell can hold one of the following:
- **Text**
- **Number**
- **Formula** (e.g., `=1+2` or `=A1+B2`)

### **Cell Class**
Handles individual cell operations.
- `boolean isNumber(String text)` - Checks if the input is a valid number.
- `boolean isText(String text)` - Checks if the input is valid text.
- `boolean isForm(String text)` - Validates if the input is a formula.
- `Double computeForm(String form)` - Evaluates formulas.

### **Spreadsheet Class**
Manages the grid of cells and provides spreadsheet-level operations.
- `Spreadsheet(int x, int y)` - Initializes a spreadsheet of size `x` by `y`.
- `Cell get(int x, int y)` - Retrieves the cell at position `(x, y)`.
- `void set(int x, int y, Cell c)` - Sets the content of the cell at `(x, y)`.
- `int xCell(String c)` - Converts column label to index (e.g., `A` → `0`).
- `int yCell(String c)` - Converts row label to index (e.g., `1` → `0`).
- `String eval(int x, int y)` - Evaluates and returns the content of a cell.
- `String[][] evalAll()` - Evaluates all cells in the spreadsheet.
- `int[][] depth()` - Calculates computational depth for all cells.









