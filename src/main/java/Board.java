import java.util.BitSet;

public class Board {
    private BitSet grid;
    private BitSet initialState;
    private int rows;
    private int cols;
    private int depth;

    public Board(int[][] initialState, int depth) {
        this.rows = initialState.length;
        this.cols = initialState[0].length;
        this.grid = new BitSet(rows * cols * depth);
        this.initialState = new BitSet(rows * cols * depth);
        this.depth = depth;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int value = initialState[i][j];
                for (int k = 0; k < value; k++) {
                    this.initialState.set((i * cols + j) * depth + k);
                }
            }
        }
        resetBoard();
    }

    public boolean placePiece(Piece piece, int x, int y) {
        if (x + piece.getHeight() > rows || y + piece.getWidth() > cols) {
            return false; // The piece is out of the board
        }
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i].charAt(j) == 'X') {
                    int position = (x + i) * cols + (y + j);
                    int currentValue = getCurrentValue(position);
                    grid.clear(position * depth, (position + 1) * depth);
                    if (currentValue < depth - 1) {
                        grid.set(position * depth + currentValue);
                    }
                }
            }
        }
        return true;
    }

    public void removePiece(Piece piece, int x, int y) {
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i].charAt(j) == 'X') {
                    int position = (x + i) * cols + (y + j);
                    int currentValue = getCurrentValue(position);
                    grid.clear(position * depth, (position + 1) * depth);
                    if (currentValue > 0) {
                        grid.set(position * depth + currentValue - 1);
                    }
                }
            }
        }
    }

    private int getCurrentValue(int position) {
        int currentValue = 0;
        for (int k = 0; k < depth; k++) {
            if (grid.get(position * depth + k)) {
                currentValue = k + 1;
            }
        }
        return currentValue;
    }

    public void resetBoard() {
        grid.clear();
        grid.or(initialState);
    }

    public int getWidth() {
        return cols;
    }

    public int getHeight() {
        return rows;
    }

    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int value = getCurrentValue(i * cols + j);
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public boolean isSolved() {
        return grid.isEmpty();
    }

    public int heuristic() {
        int score = 0;
        for (int i = 0; i < rows * cols * depth; i++) {
            if (grid.get(i)) {
                score++;
            }
        }
        return score;
    }

    @Override
    public String toString() {
        return grid.toString();
    }
}
