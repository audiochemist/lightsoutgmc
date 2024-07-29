public class Piece {
    private String[] shape;

    public Piece(String[] shape) {
        this.shape = shape;
    }

    public String[] getShape() {
        return shape;
    }

    public int getWidth() {
        return shape[0].length();
    }

    public int getHeight() {
        return shape.length;
    }
}

