// Add your documentation below:

public class CellEntry implements Index2D {
    private int x, y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isValid() {
        return x >= 0 && y >= 0;
    }
}
