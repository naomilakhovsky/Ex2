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
     * @param s The raw string data for the cell.
     */
    public SCell(String s) {
        // Add your code here
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
        return !text.isEmpty() && !isNumber(text) && !isForm(text);
    }

    /**
     * Determines if a given string is a valid formula.
     * @param text The string to validate.
     * @return True if the string is a formula; otherwise, false.
     */
    public boolean isForm(String text) {
        return text.startsWith("=") && validateFormula(text.substring(1));
    }

    /**
     * Validates the structure of a formula.
     *
     * @param formula The formula string (without the '=' prefix).
     * @return True if the formula is valid; otherwise, false.
     */

    private boolean validateFormula(String formula) {
        // Implement logic to check formula structure using regex or a parser.
        // For simplicity, assume valid formulas are numbers, operations, or cell references.
        return formula.matches("[A-Za-z0-9()+\\-*/.]+");
    }
    /**
     * Computes the value of a formula.
     *
     * @param formula The formula to evaluate (including '=' prefix).
     * @return The computed value of the formula.
     */
    public double computeForm(String formula) {
        if (!formula.startsWith("=")) {
            throw new IllegalArgumentException("Formula must start with '='");
        }

        try {
            String expr = formula.substring(1); // Remove '='

            // Replace cell references with their values
            expr = replaceCellReferences(expr);

            // Evaluate the final expression
            return evaluateExpression(expr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid formula: " + formula, e);
        }
    }

    /**
     * Replaces cell references in a formula with their evaluated values.
     *
     * @param expr The formula expression without the '=' prefix.
     * @return The expression with cell references replaced by numeric values.
     */
    private String replaceCellReferences(String expr) {
        StringBuilder replacedExpr = new StringBuilder();

        for (String token : expr.split("(?=[+\\-*/()])|(?<=[+\\-*/()])")) {
            if (token.matches("[A-Z][0-9]+")) { // Match valid cell references
                Cell refCell = Ex2Utils.getCell(token); // Fetch the referenced cell
                if (refCell != null) {
                    replacedExpr.append(refCell.getData());
                } else {
                    throw new IllegalArgumentException("Invalid cell reference: " + token);
                }
            } else {
                replacedExpr.append(token); // Append the token as-is
            }
        }

        return replacedExpr.toString();
    }

    /**
     * A helper function to evaluate a mathematical expression.
     * For simplicity, this could use libraries like javax.script or manual parsing.
     *
     * @param expression The mathematical expression to evaluate.
     * @return The result of the evaluation.
     */
    private double evaluateExpression(String expression) {
        // Simplified: Implement a proper parser for complex expressions.
        return Double.parseDouble(expression); // Replace with actual parsing logic
    }




    @Override
    public int getOrder() {

        return order;

    }

    //@Override
    @Override
    public String toString()
    {
        return getData();
    }

    @Override
public void setData(String s) {
        line = s.trim();
        if (isNumber(line)) {
            type = Ex2Utils.NUMBER;
        } else if (isText(line)) {
            type = Ex2Utils.TEXT;
        } else if (isForm(line)) {
            type = Ex2Utils.FORM;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
        }

    }
    @Override
    public String getData()
    {
        return line;
    }

    @Override
    public int getType()
    {
        return type;
    }

    @Override
    public void setType(int t)
    {
        type = t;
    }

    @Override
    public void setOrder(int t)
    {
        order =t;
    }
}
