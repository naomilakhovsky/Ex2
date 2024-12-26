public class cell {
        private String content;

        public cell(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public static boolean isNumber(String text) {
            try {
                Double.parseDouble(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

       public static boolean isText(String text) {
            return !isNumber(text) && !isForm(text);
       }
    public static boolean isForm(String text) {
        if (text == null || !text.startsWith("=")) {
            return false;
        }
        String formula = text.substring(1);
        // Basic check for valid formula: Ensure parentheses match
        int openParentheses = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') openParentheses++;
            if (c == ')') openParentheses--;
            if (openParentheses < 0) return false; // Mismatched closing parenthesis
        }
        return openParentheses == 0; // Ensure no unmatched opening parenthesis
    }

    public static Double computeForm(String form) {
        if (form == null || !form.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula format");
        }
        try {
            String expression = form.substring(1);
            // Evaluate the formula using an external library or a custom parser
            return evaluateExpression(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error computing formula: " + e.getMessage());
        }
    }

    private static Double evaluateExpression(String expression) {
        // A basic implementation of a formula evaluator would parse and compute here.
        // For simplicity, we will leave this as a placeholder.
        // This can be replaced with a library like exp4j or a custom recursive descent parser.
        return 0.0; // Placeholder
    }
}


