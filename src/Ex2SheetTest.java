import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class Ex2SheetTest {

    @Test
    void value() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // Set some values in the cells
        sheet.set(0, 0, "123"); // A0
        sheet.set(1, 1, "Hello"); // B1
        sheet.set(2, 2, "=1+2"); // C2 (formula)

        // Test valid cells
        assertEquals("123.0", sheet.value(0, 0), "Expected value of A1 to be '123'.");
        assertEquals("Hello", sheet.value(1, 1), "Expected value of B2 to be 'Hello'.");
        assertEquals("3.0", sheet.value(2, 2), "Expected value of C3 to be '=1+2'.");

        // Test out-of-bounds cells
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(-1, 0), "Expected out-of-bounds value to be EMPTY_CELL.");
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(3, 3), "Expected out-of-bounds value to be EMPTY_CELL.");
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(0, 3), "Expected out-of-bounds value to be EMPTY_CELL.");

        // Test empty cells
        assertEquals("", sheet.value(0, 1), "Expected empty cell value to be an empty string.");
        assertEquals("", sheet.value(2, 0), "Expected empty cell value to be an empty string.");
    }


    @Test
    void getStringCords() {
        Ex2Sheet sheet = new Ex2Sheet();

        // Set values using table indices (x, y)
        sheet.set(0, 0, "5"); // "A0" corresponds to (0, 0)
        sheet.set(1, 1, "3"); // "B1" corresponds to (1, 1)
        sheet.set(2, 2, "7"); // "C2" corresponds to (2, 2)

        // Verify resolution of string references
        assertNotNull(sheet.get("A0"), "Expected cell at A1 to not be null.");
        assertEquals("5.0", sheet.get("A0").getData(), "Expected A1 to have data '5'.");

        assertNotNull(sheet.get("B1"), "Expected cell at B2 to not be null.");
        assertEquals("3.0", sheet.get("B1").getData(), "Expected B2 to have data '3'.");

        assertNotNull(sheet.get("C2"), "Expected cell at C3 to not be null.");
        assertEquals("7.0", sheet.get("C2").getData(), "Expected C3 to have data '7'.");

        // Test out-of-bounds or invalid references
        assertNull(sheet.get("Z99"), "Expected cell at Z99 to be null (out of bounds).");
        assertNull(sheet.get("1A"), "Expected cell at 1A to be null (invalid format).");
        assertNull(sheet.get(null), "Expected null reference to be null.");
    }


    @Test
    void testGet() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // Set some values in the cells
        sheet.set(0, 0, "123"); // A1
        sheet.set(1, 1, "Hello"); // B2
        sheet.set(2, 2, "=1+2"); // C3

        // Test valid in-bound cells
        assertNotNull(sheet.get(0, 0), "Expected A1 to not be null.");
        assertEquals("123.0", sheet.get(0, 0).getData(), "Expected A1 data to be '123'.");
        assertNotNull(sheet.get(1, 1), "Expected B2 to not be null.");
        assertEquals("Hello", sheet.get(1, 1).getData(), "Expected B2 data to be 'Hello'.");
        assertNotNull(sheet.get(2, 2), "Expected C3 to not be null.");
        assertEquals("=1+2", sheet.get(2, 2).getData(), "Expected C3 data to be '=1+2'.");

        // Test out-of-bounds coordinates
        assertNull(sheet.get(-1, 0), "Expected (-1,0) to return null.");
        assertNull(sheet.get(3, 3), "Expected (3,3) to return null.");
        assertNull(sheet.get(0, -1), "Expected (0,-1) to return null.");
        assertNull(sheet.get(0, 3), "Expected (0,3) to return null.");
        assertNull(sheet.get(4, 4), "Expected (4,4) to return null.");

        // Test empty cells
        assertNotNull(sheet.get(2, 0), "Expected (2,0) to not be null.");
        assertEquals("", sheet.get(2, 0).getData(), "Expected empty cell to have empty data.");
    }

    @Test
    void widthAndHeight() {
        // Create spreadsheets with different dimensions
        Ex2Sheet sheet3x3 = new Ex2Sheet(3, 3); // 3x3
        Ex2Sheet sheet5x2 = new Ex2Sheet(5, 2); // 5x2
        Ex2Sheet sheet1x10 = new Ex2Sheet(1, 10); // 1x10
        // Test width for each spreadsheet
        assertEquals(3, sheet3x3.width(), "Expected width to be 3 for a 3x3 sheet.");
        assertEquals(5, sheet5x2.width(), "Expected width to be 5 for a 5x2 sheet.");
        assertEquals(1, sheet1x10.width(), "Expected width to be 1 for a 1x10 sheet.");
        // Test height for each spreadsheet
        assertEquals(3, sheet3x3.height(), "Expected height to be 3 for a 3x3 sheet.");
        assertEquals(2, sheet5x2.height(), "Expected height to be 2 for a 5x2 sheet.");
        assertEquals(10, sheet1x10.height(), "Expected height to be 10 for a 1x10 sheet.");
    }

    @Test
    void set() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // Test setting values to valid cells
        sheet.set(0, 0, "123");
        assertEquals("123.0", sheet.get(0, 0).getData(), "Expected cell A1 to have data '123'.");

        sheet.set(1, 1, "Hello");
        assertEquals("Hello", sheet.get(1, 1).getData(), "Expected cell B2 to have data 'Hello'.");

        sheet.set(2, 2, "=1+2");
        assertEquals("=1+2", sheet.get(2, 2).getData(), "Expected cell C3 to have data '=1+2'.");

        // Test overwriting an existing cell value
        sheet.set(0, 0, "456");
        assertEquals("456.0", sheet.get(0, 0).getData(), "Expected cell A1 to be updated to '456'.");

        // Test setting values to out-of-bounds cells
        sheet.set(-1, 0, "Out of bounds");
        assertNull(sheet.get(-1, 0), "Expected cell (-1,0) to remain null for out-of-bounds set.");

        sheet.set(3, 3, "Out of bounds");
        assertNull(sheet.get(3, 3), "Expected cell (3,3) to remain null for out-of-bounds set.");

        sheet.set(0, -1, "Out of bounds");
        assertNull(sheet.get(0, -1), "Expected cell (0,-1) to remain null for out-of-bounds set.");

        // Test setting a blank value
        //  sheet.set(1, 1, "");
        //  assertEquals("", sheet.get(1, 1).getData(), "Expected cell B2 to be updated to an empty string.");
    }

    @Test
    void isIn() {
        // Create a 3x3 spreadsheet
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        assertTrue(sheet.isIn(0, 0), "Expected (0,0) to be in bounds.");
        assertTrue(sheet.isIn(2, 2), "Expected (2,2) to be in bounds.");
        assertFalse(sheet.isIn(-1, 0), "Expected (-1,0) to be out of bounds.");
        assertFalse(sheet.isIn(0, -1), "Expected (0,-1) to be out of bounds.");
        assertFalse(sheet.isIn(3, 0), "Expected (3,0) to be out of bounds.");
        assertTrue(sheet.isIn(0, 2), "Expected (0,2) to be in bounds.");
    }

    @Test
    void depth() {
        // Create a 3x3 spreadsheet
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // Set values and formulas in the spreadsheet
        sheet.set(0, 0, "5");       // A0: No dependencies
        sheet.set(1, 0, "=A0+1");   // B0: Depends on A0
        sheet.set(2, 0, "=B0+1");   // C0: Depends on B0
        sheet.set(0, 1, "10");      // A1: No dependencies
        sheet.set(1, 1, "=A0+A1");  // B1: Depends on A0 and A1
        sheet.set(2, 1, "Hello");   // C1: No dependencies (text)

        // Expected depths in row-major format:
        // Row 0: A0 (0), B0 (1), C0 (2)
        // Row 1: A1 (0), B1 (1), C1 (0)
        // Row 2: Empty cells (0 depth)
        int[][] expectedDepth = {
                {0, 1, 2},  // Row 0: A0, B0, C0
                {0, 1, 0},  // Row 1: A1, B1, C1
                {0, 0, 0}   // Row 2: Empty
        };

        // Test depth calculation
      //  assertArrayEquals(expectedDepth, sheet.depth(), "Depth calculation failed.");

        // Verify values in the spreadsheet
        assertEquals("5.0", sheet.value(0, 0), "Expected value of A0 to be '5'.");
        assertEquals("6.0", sheet.value(1, 0), "Expected value of B0 to be '6.0'.");
        assertEquals("7.0", sheet.value(2, 0), "Expected value of C0 to be '7.0'.");
        assertEquals("10.0", sheet.value(0, 1), "Expected value of A1 to be '10'.");
        assertEquals("15.0", sheet.value(1, 1), "Expected value of B1 to be '15.0'.");
        assertEquals("Hello", sheet.value(2, 1), "Expected value of C1 to be 'Hello'.");
    }

    @Test
    void testCalculateDepth() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // Set values and formulas in the spreadsheet
        sheet.set(0, 0, "5");       // A0: No dependencies
        sheet.set(1, 0, "=A0+1");   // B0: Depends on A0
        sheet.set(2, 0, "=B0+1");   // C0: Depends on B0
        sheet.set(0, 1, "10");      // A1: No dependencies
        sheet.set(1, 1, "=A0+A1");  // B1: Depends on A0 and A1
        sheet.set(2, 1, "Hello");   // C1: Text, no dependencies

        // Test individual cell depth calculations
        assertEquals(0, sheet.calculateDepth(0, 0, new boolean[sheet.width()][sheet.height()]), "A0 should have depth 0.");
        assertEquals(1, sheet.calculateDepth(1, 0, new boolean[sheet.width()][sheet.height()]), "B0 should have depth 1.");
        assertEquals(2, sheet.calculateDepth(2, 0, new boolean[sheet.width()][sheet.height()]), "C0 should have depth 2.");
        assertEquals(0, sheet.calculateDepth(0, 1, new boolean[sheet.width()][sheet.height()]), "A1 should have depth 0.");
        assertEquals(1, sheet.calculateDepth(1, 1, new boolean[sheet.width()][sheet.height()]), "B1 should have depth 1.");
        assertEquals(0, sheet.calculateDepth(2, 1, new boolean[sheet.width()][sheet.height()]), "C1 (Text) should have depth 0.");

        //Test for circular dependencies
         // sheet.set(0, 0, "=C0"); // A0 now depends on C0, creating a circular dependency
      //  assertEquals(Ex2Utils.ERR, sheet.calculateDepth(0, 0, new boolean[sheet.width()][sheet.height()]), "A0 should return ERR due to circular dependency.");
    }


    @Test
    void ExtractDependencies() {
        Ex2Sheet sheet = new Ex2Sheet(7, 7); // Create a 7x7 spreadsheet

        // Test dependency extraction for a single dependency
        String formula1 = "=A0";
        List<Index2D> dependencies1 = sheet.extractDependencies(formula1);
        assertEquals(1, dependencies1.size(), "Expected one dependency for formula '=A0'.");
        Index2D dep1 = dependencies1.get(0);
        assertEquals(0, dep1.getX(), "Expected dependency A0 to have X=0.");
        assertEquals(0, dep1.getY(), "Expected dependency A0 to have Y=0.");

        // Test dependency extraction for a formula with multiple dependencies
        String formula2 = "=A1+B1";
        List<Index2D> dependencies2 = sheet.extractDependencies(formula2);
        assertEquals(2, dependencies2.size(), "Expected two dependencies for formula '=A1+B1'.");

        Index2D dep2 = dependencies2.get(0);
        assertEquals(0, dep2.getX(), "Expected dependency A1 to have X=0.");
        assertEquals(1, dep2.getY(), "Expected dependency A1 to have Y=1.");

        Index2D dep3 = dependencies2.get(1);
        assertEquals(1, dep3.getX(), "Expected dependency B1 to have X=1.");
        assertEquals(1, dep3.getY(), "Expected dependency B1 to have Y=1.");
    }

    @Test
    void testColumnToIndex() {
        Ex2Sheet sheet = new Ex2Sheet(); // Create an instance of the Ex2Sheet class

        // Test valid columns
        assertEquals(0, sheet.columnToIndex("A"), "Expected column A to map to index 0.");
        assertEquals(1, sheet.columnToIndex("B"), "Expected column B to map to index 1.");
        assertEquals(2, sheet.columnToIndex("C"), "Expected column C to map to index 2.");
        assertEquals(3, sheet.columnToIndex("d"), "Expected column C to map to index 2.");
        assertEquals(4, sheet.columnToIndex("e"), "Expected column C to map to index 2.");
        assertEquals(25, sheet.columnToIndex("Z"), "Expected column Z to return 25.");
        assertEquals(0, sheet.columnToIndex("a"), "Expected column a to map to index 0.");
        assertEquals(1, sheet.columnToIndex("b"), "Expected column b to map to index 1.");

        // Test invalid columns
        assertEquals(-1, sheet.columnToIndex("AA"), "Expected column AA to return -1 (invalid column).");
        assertEquals(-1, sheet.columnToIndex("1"), "Expected column 1 to return -1 (invalid column).");
        assertEquals(-1, sheet.columnToIndex(null), "Expected null column to return -1.");
    }

    @Test
    void load() {
    }

    @Test
    void save() {
    }

    @Test
    void eval() {
            // Create a 3x3 spreadsheet
            Ex2Sheet sheet = new Ex2Sheet(3, 3);

            // Set values and formulas in the spreadsheet
            sheet.set(0, 0, "5");       // A0: Numeric value
            sheet.set(1, 0, "=A0+1");   // B0: Formula depends on A0
            sheet.set(2, 0, "=B0*2");   // C0: Formula depends on B0
            sheet.set(0, 1, "Hello");   // A1: Text
            sheet.set(1, 1, "=A0+10");  // B1: Formula depends on A0
            sheet.set(2, 1, "=B1-5");   // C1: Formula depends on B1

            // Call eval to evaluate all cells
            sheet.eval();

            // Validate the evaluated values
            assertEquals("5.0", sheet.value(0, 0), "Expected value of A0 to be '5'.");
            assertEquals("6.0", sheet.value(1, 0), "Expected value of B0 to be '6.0'.");
            assertEquals("12.0", sheet.value(2, 0), "Expected value of C0 to be '12.0'.");
            assertEquals("Hello", sheet.value(0, 1), "Expected value of A1 to be 'Hello'.");
            assertEquals("15.0", sheet.value(1, 1), "Expected value of B1 to be '15.0'.");
            assertEquals("10.0", sheet.value(2, 1), "Expected value of C1 to be '10.0'.");

    }

    @Test
    void testEvalSingleCell() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // "A0" => (x=0,y=0)
        sheet.set(0, 0, "5");

        // "B0" => (x=1,y=0)
        sheet.set(1, 0, "=A0+1");

        // "C0" => (x=2,y=0)
        sheet.set(2, 0, "=B0+1");


        // Evaluate
        String valA0 = sheet.eval(0, 0);
        String valB0 = sheet.eval(1, 0);
        String valC0 = sheet.eval(2, 0);

        assertEquals("5.0", valA0, "A0 should be 5.0");
        assertEquals("6.0", valB0, "B0 should be (A0 + 1) => 6.0");
        assertEquals("7.0", valC0, "C0 should be (B0 + 1) => 7.0");
    }

        @Test
    void testStringReference_WithA0() {
        Ex2Sheet sheet = new Ex2Sheet(3,3);

        // (x=0,y=0) => "A0"
        sheet.set(0, 0, "10");
        sheet.set(0, 1, "=A0*2"); // B0 => depends on A0

        // Now let's get via string reference:  get("B0") => same as (0,1)
        Cell b0Cell = sheet.get("B0");
        assertNotNull(b0Cell, "B0 cell should exist");

        // Evaluate the formula in B0
        String valB0 = sheet.eval(0,1);
        assertEquals("20.0", valB0, "B0 should be A0*2 => 20.0");
    }

}