/**
 * This class represents a single cell in the spreadsheet.
 * A cell can contain a number, text, or a formula.
 * It determines the type of content, evaluates formulas, and stores data.
 */
public class SCell implements Cell {
    private String line;
    private int type;
    private int order;   // Order or depth used in dependency calculation

    /**
     * Constructor to initialize the cell with data.
     *
     * @param s The raw string data for the cell.
     */
    public SCell(String s) {
        setData(s);
    }

    /**
     * Determines if a given string represents a valid number.
     *
     * @param text The string to validate.
     * @return True if the string is a number; otherwise, false.
     */
    public boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Determines if a given string represents text (not a number or formula).
     *
     * @param text The string to validate.
     * @return True if the string is plain text; otherwise, false.
     */
    public boolean isText(String text) {
        if (text == null) {
            return false;
        }
        return !text.startsWith("=") && !isNumber(text) && !isForm(text);
    }

    /**
     * Determines if a given string is a valid formula.
     *
     * @param text The string to validate.
     * @return True if the string is a formula; otherwise, false.
     */
    public boolean isForm(String text) {
        if (text == null || text.isEmpty()) {
            return false; // Null or empty strings cannot be formulas
        }
        return text.startsWith("=") && validateFormula(text);
    }

    /**
     * Validates the structure of a formula.
     *
     * @param formula The formula string (without the '=' prefix).
     * @return True if the formula is valid; otherwise, false.
     */
    public boolean validateFormula(String formula) {
        // Check if the formula starts with '='
        if (formula == null || formula.isEmpty() || !formula.startsWith("=")) {
            return false; // A valid formula must start with '='
        }

        // Remove the '=' prefix for further validation
        formula = formula.substring(1);

        if (formula.isEmpty()) {
            return false; // After removing '=', the formula cannot be empty
        }

        boolean expectOperand = true; // True if expecting an operand (number, cell reference, or '(')
        int parenthesesBalance = 0;  // To track balanced parentheses
        boolean lastCharWasDot = false; // To ensure dots are correctly placed

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            // Check for cell references
            if (isCellReference(formula, i)) {
                if (!expectOperand) {
                    return false; // Invalid if a cell reference is not expected here
                }

                // Skip the cell reference length
                i += extractCellReferenceLength(formula.substring(i)) - 1;
                expectOperand = false; // Operand found
                lastCharWasDot = false; // Reset dot flag
            }
            // Check for digits (numbers)
            else if (Character.isDigit(c)) {
                if (!expectOperand) {
                    return false; // Invalid if a number is not expected here
                }

                // Process the entire number
                while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    if (formula.charAt(i) == '.') {
                        if (lastCharWasDot) {
                            return false; // Invalid: consecutive dots
                        }
                        lastCharWasDot = true; // Set dot flag
                    }
                    i++;
                }
                i--; // Adjust index after processing the number
                expectOperand = false; // Operand found
                lastCharWasDot = false; // Reset dot flag
            }
            // Check for operators
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (expectOperand) {
                    return false; // Invalid: operator without a preceding operand
                }
                expectOperand = true; // Expect an operand next
                lastCharWasDot = false; // Reset dot flag
            }
            // Check for opening parentheses
            else if (c == '(') {
                parenthesesBalance++;
                expectOperand = true; // Expect an operand inside parentheses
                lastCharWasDot = false; // Reset dot flag
            }
            // Check for closing parentheses
            else if (c == ')') {
                parenthesesBalance--;
                if (parenthesesBalance < 0 || expectOperand) {
                    return false; // Unmatched closing parenthesis or misplaced
                }
                lastCharWasDot = false; // Reset dot flag
            }
            // Invalid character
            else {
                return false; // Any other character is invalid
            }
        }

        // Formula is valid if:
        // - Parentheses are balanced
        // - Does not end with an operator
        // - Is not expecting another operand
        return parenthesesBalance == 0 && !expectOperand && !lastCharWasDot;
    }


    /**
     * Checks if a string represents a valid cell reference (e.g., A0, B1).
     */
    private boolean isCellReference(String formula, int startIndex) {
        String substring = formula.substring(startIndex);

        // A valid cell reference starts with one or more letters followed by one or more digits
        return substring.matches("^[A-Z]+[0-9]+.*");
    }

    private int extractCellReferenceLength(String cellReference) {
        int length = 0;
        for (int i = 0; i < cellReference.length(); i++) {
            char c = cellReference.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                length++;
            } else {
                break;
            }
        }
        return length;
    }

    /**
     * Computes the value of a formula.
     *
     * @param formula The formula to evaluate (including '=' prefix).
     * @return The computed value of the formula.
     */
    public double computeForm(String formula,Ex2Sheet sheet) {
        if (!formula.startsWith("=")) {
            throw new IllegalArgumentException("Formula must start with '='");
        }

        String expression = formula.substring(1); // Remove '=' prefix
        double result = evaluateExpression(expression, sheet);
        return Double.parseDouble(String.format("%.1f", result)); // Format as double

    }

    /**
     * Recursively evaluates an expression, resolving cell references, operators, and numbers.
     *
     * @param expression The mathematical expression to evaluate.
     * @return The result of the evaluation.
     */
    private double evaluateExpression(String expression,Ex2Sheet sheet) {
        expression = expression.replaceAll("\\s+", ""); // Remove whitespace

        if (isNumber(expression)) {
            return Double.parseDouble(expression); // Base case: numeric value
        }

        // Handle parentheses
        if (expression.startsWith("(") && expression.endsWith(")")) {
            return evaluateExpression(expression.substring(1, expression.length() - 1),sheet);
        }

        // Evaluate addition and subtraction
        int plusIndex = findOperator(expression, '+');
        int minusIndex = findOperator(expression, '-');
        if (plusIndex != -1 || minusIndex != -1) {
            int index = (plusIndex != -1) ? plusIndex : minusIndex;
            double left = evaluateExpression(expression.substring(0, index),sheet);
            double right = evaluateExpression(expression.substring(index + 1),sheet);
            return (expression.charAt(index) == '+') ? left + right : left - right;
        }

        // Evaluate multiplication and division
        int multiplyIndex = findOperator(expression, '*');
        int divideIndex = findOperator(expression, '/');
        if (multiplyIndex != -1 || divideIndex != -1) {
            int index = (multiplyIndex != -1) ? multiplyIndex : divideIndex;
            double left = evaluateExpression(expression.substring(0, index),sheet);
            double right = evaluateExpression(expression.substring(index + 1),sheet);
            if (expression.charAt(index) == '/') {
                if (right == 0) throw new ArithmeticException("Division by zero");
                return left / right;
            }
            return left * right;
        }

        // Resolve cell references
        if (expression.matches("[A-Z][0-9]+")) {
            return resolveCellValue(expression,sheet);
        }

        throw new IllegalArgumentException("Invalid expression: " + expression);
    }

    /**
     * Resolves the value of a referenced cell (e.g., A1, B2).
     *
     * @param ref The cell reference as a string.
     * @return The value of the referenced cell as a double.
     */
    private double resolveCellValue(String ref, Ex2Sheet sheet) {
        // 1) Parse something like "A0" => x=0, y=0
        if (ref == null || ref.length() < 2) {
            throw new IllegalArgumentException("Invalid cell reference: " + ref);
        }

        // Extract the column letter (e.g. "A") and the row substring (e.g. "0")
        String column = ref.substring(0, 1).toUpperCase();
        String rowStr = ref.substring(1);

        // Convert column to x
        int x = -1;
        for (int i = 0; i < Ex2Utils.ABC.length; i++) {
            if (Ex2Utils.ABC[i].equalsIgnoreCase(column)) {
                x = i;
                break;
            }
        }
        if (x == -1) {
            throw new IllegalArgumentException("Invalid column in ref: " + ref);
        }

        // Convert the row part to y (NO -1)
        int y;
        try {
            y = Integer.parseInt(rowStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in ref: " + ref);
        }

        // 2) Check if (x, y) is in range
        if (!sheet.isIn(x, y)) {
            throw new IllegalArgumentException("Reference out of bounds: " + ref);
        }

        // 3) Let the sheet evaluate the cell (x,y)
        String val = sheet.eval(x, y); // e.g., "5.0", "6.0", or "ERR_FORM!"

        // 4) If "ERR_FORM!" => we can't parse it as double
        if (val.equals(Ex2Utils.ERR_FORM)) {
            throw new IllegalArgumentException("Invalid or non-numeric cell reference: " + ref);
        }

        // 5) Otherwise parse the numeric result
        return Double.parseDouble(val);
    }



    /**
     * Finds the first occurrence of an operator outside parentheses.
     * @param expression The expression to search.
     * @param operator   The operator to find.
     * @return The index of the operator, or -1 if not found.
     */
    private int findOperator(String expression, char operator) {
        int depth = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && c == operator) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        if (type == Ex2Utils.NUMBER) { // Check if the cell contains a number
            try {
                double value = Double.parseDouble(getData());
                return String.format("%.1f", value); // Format the number as a double (e.g., "5.0")
            } catch (NumberFormatException e) {
                return Ex2Utils.ERR_FORM; // Handle invalid numeric data gracefully
            }
        }
        return getData(); // For non-numeric cells, return the raw data
    }


    @Override
    public void setData(String s) {
        line = s.trim();
        if (isNumber(line)) {
            double value = Double.parseDouble(line);
            type = Ex2Utils.NUMBER;
            line = String.format("%.1f", value); // Store as double format
        } else if (isText(line)) {
            type = Ex2Utils.TEXT;
        } else if (isForm(line)) {
            type = Ex2Utils.FORM;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
        }
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        order = t;
    }
}
