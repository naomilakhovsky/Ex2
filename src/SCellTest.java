import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SCellTest {

    private int[] parseCellReference(String ref) {

        char colChar = ref.charAt(0);
        String rowPart = ref.substring(1);


        int x = colChar - 'A';


        int y = Integer.parseInt(rowPart);

        return new int[] { x, y };
    }

    @Test
    void isNumber() {
        SCell cell = new SCell("");

        assertFalse(cell.isNumber("abc"));
        assertFalse(cell.isNumber("1233448a"));
        assertTrue(cell.isNumber("-456"));
        assertTrue(cell.isNumber("78.9"));
        assertTrue(cell.isNumber("-0.001"));
        assertTrue(cell.isNumber("1e10"));
        assertTrue(cell.isNumber("5"));

    }

    @Test
    void isText() {
        SCell cell = new SCell("");
        assertTrue(cell.isText("hello"));
        assertFalse(cell.isText("123"));
        assertTrue(cell.isText("hello123"));
        assertTrue(cell.isText("1+3"));
        assertTrue(cell.isText(""));
        assertFalse(cell.isText("0.5"));
    }

    @Test
    void isForm() {
        SCell cell = new SCell("");

        // Valid formulas
        assertTrue(cell.isForm("=1"), "Expected '=1' to be identified as a formula.");
        assertTrue(cell.isForm("=1+2*2"), "Expected '=1+2*2' to be identified as a formula.");
        assertTrue(cell.isForm("=(2)"), "Expected '=(2)' to be identified as a formula.");
        assertTrue(cell.isForm("=(1+2)*2"), "Expected '=(1+2)*2' to be identified as a formula.");
        assertTrue(cell.isForm("=3.14*2"), "Expected '=3.14*2' to be identified as a formula.");
        assertTrue(cell.isForm("=A0+1"), "Expected '=A0+1' to be a valid formula.");
        assertTrue(cell.isForm("=B10/2"), "Expected '=B10/2' to be a valid formula.");
        assertTrue(cell.isForm("=C1*(D2+5)"), "Expected '=C1*(D2+5)' to be a valid formula.");
        assertTrue(cell.isForm("=A0-B1+C2"), "Expected '=A0-B1+C2' to be a valid formula.");

        // Invalid formulas
        assertFalse(cell.isForm("123"), "Expected '123' to not be identified as a formula.");
        assertFalse(cell.isForm("=1+.5"), "Expected '=1+.5' to not be identified as a formula (invalid structure).");
        assertFalse(cell.isForm("=1++2"), "Expected '=1++2' to not be identified as a formula (invalid structure).");
        assertFalse(cell.isForm("=(1+2))"), "Expected '=(1+2))' to not be identified as a formula (unmatched parentheses).");
        assertFalse(cell.isForm("=(1+2"), "Expected '=(1+2' to not be identified as a formula (unmatched parentheses).");
        assertFalse(cell.isForm("=1+2*"), "Expected '=1+2*' to not be identified as a formula (invalid structure).");
        assertFalse(cell.isForm("=1+*2"), "Expected '=1+*2' to not be identified as a formula (invalid operator placement).");
        assertFalse(cell.isForm("=5**"), "Expected '=5**' to not be identified as a formula (invalid operator placement).");
        assertFalse(cell.isForm(""), "Expected an empty string to not be identified as a formula.");
        assertFalse(cell.isForm(null), "Expected null to not be identified as a formula.");
    }



    @Test
    void computeForm() {
        Ex2Sheet sheet = new Ex2Sheet();

        int[] a1 = parseCellReference("A1");
        sheet.set(a1[0], a1[1], "5");

        int[] b2 = parseCellReference("B2");
        sheet.set(b2[0], b2[1], "3");

        SCell cell = new SCell("");

        // Test valid formulas
        assertEquals(8.0, cell.computeForm("=A1+B2", sheet), "Expected '=A1+B2' to compute to 8.0.");
        assertEquals(15.0, cell.computeForm("=A1*B2", sheet), "Expected '=A1*B2' to compute to 15.0.");

        // Valid expressions
        assertEquals(7.0, cell.computeForm("=5+2", sheet), "Expected '=5+2' to compute to 7.0.");
        assertEquals(10.0, cell.computeForm("=2*5", sheet), "Expected '=2*5' to compute to 10.0.");
        assertEquals(2.5, cell.computeForm("=5/2", sheet), "Expected '=5/2' to compute to 2.5.");
        assertEquals(3.0, cell.computeForm("=10-7", sheet), "Expected '=10-7' to compute to 3.0.");
        assertEquals(14.0, cell.computeForm("=2*(3+4)", sheet), "Expected '=2*(3+4)' to compute to 14.0.");

    }
    @Test
    void testValidateFormulaWithCellReferences() {
        SCell cell = new SCell("");

        // Valid formulas
        assertTrue(cell.validateFormula("=A0+1"), "Expected 'A0+1' to be valid.");
        assertTrue(cell.validateFormula("=B1*2"), "Expected 'B1*2' to be valid.");
        assertTrue(cell.validateFormula("=(C2-D3)/E4"), "Expected '(C2-D3)/E4' to be valid.");
        assertTrue(cell.validateFormula("=5*(A1+B2)"), "Expected '5*(A1+B2)' to be valid.");

        // Invalid formulas
        assertFalse(cell.validateFormula("=1A0+1"), "Expected '1A0+1' to be invalid.");
        assertFalse(cell.validateFormula("=A1++B1"), "Expected 'A1++B1' to be invalid.");
        assertTrue(cell.validateFormula("=A0"), "Expected 'A0' to be invalid.");
        assertFalse(cell.validateFormula(" "), "Expected '=' to be invalid.");
        assertFalse(cell.validateFormula("=1A1"), "Expected '1A1' to be invalid.");
        assertFalse(cell.validateFormula("=5.A1"), "Expected '5.A1' to be invalid.");
    }



    @Test
    void getOrder() {
        SCell cell = new SCell("data");

        assertEquals(0, cell.getOrder(), "Expected initial order to be 0.");

        cell.setOrder(5);
        assertEquals(5, cell.getOrder(), "Expected order to be updated to 5.");

        cell.setOrder(-2); // Test with negative value
        assertEquals(-2, cell.getOrder(), "Expected order to be updated to -2.");

    }

    @Test
    void testToString() {
        SCell cell = new SCell("Hello");

        assertEquals("Hello", cell.toString(), "Expected toString to return 'Hello'.");

        cell.setData("123");
        assertEquals("123.0", cell.toString(), "Expected toString to return '123'.");

        cell.setData("");
        assertEquals("", cell.toString(), "Expected toString to return an empty string.");

        cell.setData("!@#$%^&*()");
        assertEquals("!@#$%^&*()", cell.toString(), "Expected toString to return '!@#$%^&*()'.");
    }


    @Test
    void setData() {
        SCell cell = new SCell("");

        cell.setData("123");
        assertEquals(Ex2Utils.NUMBER, cell.getType(), "Expected type to be NUMBER.");


        cell.setData("5");
        assertEquals(Ex2Utils.NUMBER, cell.getType(), "Expected type to be NUMBER.");


        cell.setData("Hello");
        assertEquals(Ex2Utils.TEXT, cell.getType(), "Expected type to be TEXT.");

        cell.setData("=1+2");
        assertEquals(Ex2Utils.FORM, cell.getType(), "Expected type to be FORM.");

        cell.setData("=A0+1");
        assertEquals(Ex2Utils.FORM, cell.getType(), "Expected type FORM for input '=A1+10'");
        assertEquals("=A0+1", cell.getData(), "Expected data '=A1+10' for input '=A1+10'");

        cell.setData("123-abc");
        assertEquals(Ex2Utils.TEXT, cell.getType(), "Expected type to be ERR_FORM_FORMAT.");

        cell.setData("  456  ");
        assertEquals(Ex2Utils.NUMBER, cell.getType(), "Expected type to be NUMBER.");

        cell.setData("=5**");
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType(), "Expected type to be ERR.");
    }


    @Test
    void getData() {
        SCell cell = new SCell("");

        assertEquals("", cell.getData(), "Expected data to be an empty string.");

        cell.setData("123");
        assertEquals("123.0", cell.getData(), "Expected data to be '123'.");

        cell.setData("=1+2");
        assertEquals("=1+2", cell.getData(), "Expected data to be '=1+2'.");

    }


    @Test
    void testSetAndGetType() {
        SCell cell = new SCell("");

        cell.setType(1);
        assertEquals(1, cell.getType(), "Expected type to be 1 after setting.");

        cell.setType(-1);
        assertEquals(-1, cell.getType(), "Expected type to be -1 after setting.");

        cell.setType(100);
        assertEquals(100, cell.getType(), "Expected type to be 100 after setting.");

        cell.setType(-100);
        assertEquals(-100, cell.getType(), "Expected type to be -100 after setting.");
    }


    @Test
    void setOrder() {
        SCell cell = new SCell("");

        cell.setOrder(0);
        assertEquals(0, cell.getOrder(), "Expected order to be 0.");

        cell.setOrder(-3);
        assertEquals(-3, cell.getOrder(), "Expected order to be -3.");

    }
}