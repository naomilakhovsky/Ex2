/**
 * This class represents a single cell in the spreadsheet.
 * A cell can contain a number, text, or a formula.
 * It determines the type of content, evaluates formulas, and stores data.
 */
public class SCell implements Cell {
    private String line;
    private int type;
    private int order;

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
            return false;
        }

        formula = formula.substring(1);

        if (formula.isEmpty()) {
            return false;
        }

        boolean expectOperand = true;
        int parenthesesBalance = 0;
        boolean lastCharWasDot = false;

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (isCellReference(formula, i)) {
                if (!expectOperand) {
                    return false;
                }

                i += extractCellReferenceLength(formula.substring(i)) - 1;
                expectOperand = false;
                lastCharWasDot = false;
            }
            // Check for digits (numbers)
            else if (Character.isDigit(c)) {
                if (!expectOperand) {
                    return false;
                }

                while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    if (formula.charAt(i) == '.') {
                        if (lastCharWasDot) {
                            return false;
                        }
                        lastCharWasDot = true;
                    }
                    i++;
                }
                i--; // Adjust index after processing the number
                expectOperand = false;
                lastCharWasDot = false;
            }
            // Check for operators
            else if (c == '+' || c == '-') {
                if (expectOperand && (i == 0 || formula.charAt(i - 1) == '(')) {
                    expectOperand = true;
                } else if (!expectOperand) {
                    expectOperand = true;
                } else {
                    return false;
                }
            }
            // Check for other operators
            else if (c == '*' || c == '/') {
                if (expectOperand) {
                    return false;
                }
                expectOperand = true;
                lastCharWasDot = false;
            }

            else if (c == '(') {
                parenthesesBalance++;
                expectOperand = true;
                lastCharWasDot = false;
            }
            else if (c == ')') {
                parenthesesBalance--;
                if (parenthesesBalance < 0 || expectOperand) {
                    return false;
                }
                lastCharWasDot = false;
            }
            // Invalid character
            else {
                return false;
            }
        }

        return parenthesesBalance == 0 && !expectOperand && !lastCharWasDot;
    }


    /**
     * Checks if a string represents a valid cell reference (e.g., A0, B1).
     */
    private boolean isCellReference(String formula, int startIndex) {
        String substring = formula.substring(startIndex);

        if (startIndex > 0 && (formula.charAt(startIndex - 1) == '-' || formula.charAt(startIndex - 1) == '+')) {
            startIndex--;
        }

        // A valid cell reference starts with one or more letters followed by one or more digits
        return substring.matches("(?i)^[A-Z]+[0-9]+.*");
    }

    /**
     * Extracts the length of a valid cell reference from a formula.
     *
     * @param cellReference The substring containing the cell reference.
     * @return The length of the cell reference.
     */

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

        String expression = formula.substring(1);
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
        expression = expression.replaceAll("\\s+", "");

        if (expression.startsWith("-")) {
            return -evaluateExpression(expression.substring(1), sheet);
        } else if (expression.startsWith("+")) {
            return evaluateExpression(expression.substring(1), sheet);
        }


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
        if (expression.matches("(?i)[A-Z][0-9]+")) {
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

        int y;
        try {
            y = Integer.parseInt(rowStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in ref: " + ref);
        }

        if (!sheet.isIn(x, y)) {
            throw new IllegalArgumentException("Reference out of bounds: " + ref);
        }

        String val = sheet.eval(x, y); // e.g., "5.0", "6.0", or "ERR_FORM!"

        if (val.equals(Ex2Utils.ERR_FORM)) {
            throw new IllegalArgumentException("Invalid or non-numeric cell reference: " + ref);
        }

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

    /**
     * Converts the cell data to its string representation.
     *
     * @return The cell data as a string.
     */

    @Override
    public String toString() {
        if (type == Ex2Utils.NUMBER) { // Check if the cell contains a number
            try {
                double value = Double.parseDouble(getData());
                return String.format("%.1f", value);
            } catch (NumberFormatException e) {
                return Ex2Utils.ERR_FORM;
            }
        }
        return getData();
    }
    /**
     * Sets the raw data for the cell and determines its type.
     *
     * @param s The raw string data to set.
     */


    @Override
    public void setData(String s) {
        line = s.trim();
        if (isNumber(line)) {
            double value = Double.parseDouble(line);
            type = Ex2Utils.NUMBER;
            line = String.format("%.1f", value);
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
