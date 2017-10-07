public class Field {

    private final int MAX_SIZE = 50;

    private int width;
    private int height;
    private int[][] field = new int[MAX_SIZE][MAX_SIZE];

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getSize() {
        return Math.max(width, height);
    }

    public void setWidth(int width) {
        if (width < 51 && width > 0)
            this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
