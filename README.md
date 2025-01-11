**Ex2- spreadsheet project:**

## Overview
The spreadsheet is designed as a 2D grid of Cells, where each cell can hold one of the following:
- **Text**
- **Number**
- **Formula** (e.g., `=1+2` or `=A1+B2`)
- 
- **Features**

Cell Value Storage: Store numbers, text, and formulas in individual cells.
Formula Evaluation: Support for arithmetic operations and cell references in formulas (e.g., =A1 + B2 * 2).
Inter-Cell Dependencies: Automatically update dependent cells when referenced cells change.
Circular Dependency Detection: Prevent infinite loops caused by circular references.

### **Cell Class**
Handles individual cell operations.
- `boolean isNumber(String text)` - Checks if the input is a valid number.
- `boolean isText(String text)` - Checks if the input is valid text.
- `boolean isForm(String text)` - Validates if the input is a formula.
- `Double computeForm(String form)` - Evaluates formulas.

### **Spreadsheet Class**
Manages the grid of cells and provides spreadsheet-level operations.
- `Cell get(int x, int y)` - Retrieves the cell at position `(x, y)`.
- `void set(int x, int y, Cell c)` - Sets the content of the cell at `(x, y)`.
- `String eval(int x, int y)` - Evaluates and returns the content of a cell.
- `String[][] evalAll()` - Evaluates all cells in the spreadsheet.
- `int[][] depth()` - Calculates computational depth for all cells.
- 
- here is an example of a spreadsheet:
![image](https://github.com/user-attachments/assets/2e618e68-f4fa-4370-9795-4d4ed9be02fb)










