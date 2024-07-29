import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LightsOutSolver {
    private int[][] board;
    private int rows;
    private int cols;
    private int depth;
    private Piece[] pieces;
    private int bestSolution;
    private int[][][] memo;
    private long startTime;
    private int redefinitionLevel;

    public LightsOutSolver(int[][] board, int depth, Piece[] pieces) {
        this.board = board;
        this.rows = board.length;
        this.cols = board[0].length;
        this.depth = depth;
        this.pieces = pieces;
        this.bestSolution = Integer.MAX_VALUE;
        this.memo = new int[rows][cols][pieces.length];
        for (int[][] layer : memo) {
            for (int[] row : layer) {
                Arrays.fill(row, -1);
            }
        }
        this.redefinitionLevel = 0;
    }

    public void solve() {
        startTime = System.currentTimeMillis();
        List<int[]> placedPieces = new ArrayList<>();
        if (placePieces(0, placedPieces, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            System.out.println("Solution found!");
            printSolution(placedPieces);
        } else {
            System.out.println("No solution found.");
        }
        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) / 1000.0;
        System.out.println("Time elapsed: " + elapsedTime + " seconds");
    }

    private boolean placePieces(int pieceIndex, List<int[]> placedPieces, int alpha, int beta) {
        if (System.currentTimeMillis() - startTime > 6000 * (redefinitionLevel + 1)) {
            return redefineSearch(pieceIndex, placedPieces, alpha, beta);
        }

        if (pieceIndex == pieces.length) {
            if (isSolved()) {
                bestSolution = placedPieces.size();
                return true;
            }
            return false;
        }

        Piece piece = pieces[pieceIndex];
        List<int[]> positions = generatePositions(piece);

        for (int[] pos : positions) {
            int x = pos[0];
            int y = pos[1];
            if (placePiece(piece, x, y)) {
                placedPieces.add(new int[]{x, y});
                if (placedPieces.size() < bestSolution) {
                    if (placePieces(pieceIndex + 1, placedPieces, alpha, beta)) {
                        return true;
                    }
                }
                removePiece(piece, x, y);
                placedPieces.remove(placedPieces.size() - 1);
            }
            if (beta <= alpha) {
                break;
            }
        }

        return false;
    }

    private boolean redefineSearch(int pieceIndex, List<int[]> placedPieces, int alpha, int beta) {
        redefinitionLevel++;
        System.out.println("Redefining search with enhanced heuristics and aggressive pruning... Level: " + redefinitionLevel);

        if (pieceIndex == pieces.length) {
            if (isSolved()) {
                bestSolution = placedPieces.size();
                return true;
            }
            return false;
        }

        Piece piece = pieces[pieceIndex];
        List<int[]> positions = generatePositionsEnhanced(piece);

        for (int[] pos : positions) {
            int x = pos[0];
            int y = pos[1];
            if (placePiece(piece, x, y)) {
                placedPieces.add(new int[]{x, y});
                if (placedPieces.size() < bestSolution) {
                    if (redefineSearch(pieceIndex + 1, placedPieces, alpha, beta)) {
                        return true;
                    }
                }
                removePiece(piece, x, y);
                placedPieces.remove(placedPieces.size() - 1);
            }
            if (beta <= alpha) {
                break;
            }
        }

        return false;
    }

    private List<int[]> generatePositions(Piece piece) {
        List<int[]> positions = new ArrayList<>();
        for (int x = 0; x <= rows - piece.getHeight(); x++) {
            for (int y = 0; y <= cols - piece.getWidth(); y++) {
                positions.add(new int[]{x, y});
            }
        }
        positions.sort(Comparator.comparingInt(pos -> heuristic(pos[0], pos[1], piece)));
        return positions;
    }

    private List<int[]> generatePositionsEnhanced(Piece piece) {
        List<int[]> positions = new ArrayList<>();
        for (int x = 0; x <= rows - piece.getHeight(); x++) {
            for (int y = 0; y <= cols - piece.getWidth(); y++) {
                positions.add(new int[]{x, y});
            }
        }
        positions.sort(Comparator.comparingInt(pos -> enhancedHeuristic(pos[0], pos[1], piece)));
        return positions;
    }

    private int heuristic(int x, int y, Piece piece) {
        int score = 0;
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i].charAt(j) == 'X') {
                    score += board[x + i][y + j] == 0 ? 1 : 0;
                    score += countAdjacentLights(x + i, y + j);
                }
            }
        }
        return score;
    }

    private int enhancedHeuristic(int x, int y, Piece piece) {
        int score = 0;
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i].charAt(j) == 'X') {
                    score += board[x + i][y + j] == 0 ? 2 * redefinitionLevel : 0; // Enhanced heuristic
                    score += countAdjacentLights(x + i, y + j) * 2 * redefinitionLevel; // Enhanced heuristic
                }
            }
        }
        return score;
    }

    private int countAdjacentLights(int x, int y) {
        int count = 0;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && board[nx][ny] != 0) {
                count++;
            }
        }
        return count;
    }

    private boolean placePiece(Piece piece, int x, int y) {
        if (x + piece.getHeight() > rows || y + piece.getWidth() > cols) {
            return false;
        }
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i].charAt(j) == 'X') {
                    board[x + i][y + j] = (board[x + i][y + j] + 1) % depth;
                }
            }
        }
        return true;
    }

    private void removePiece(Piece piece, int x, int y) {
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i].charAt(j) == 'X') {
                    board[x + i][y + j] = (board[x + i][y + j] - 1 + depth) % depth;
                }
            }
        }
    }

    private boolean isSolved() {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void printSolution(List<int[]> placedPieces) {
        for (int[] coords : placedPieces) {
            System.out.print(coords[0] + "," + coords[1] + " ");
        }
        System.out.println();
    }

    private void printBoard() {
        for (int[] row : board) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}