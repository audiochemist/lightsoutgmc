import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class LightsOutApplication {
    public static void main(String[] args) {
        String fileName = "04.txt";
        processFile("/" + fileName);
    }

    private static void processFile(String fileName) {
        try (InputStream inputStream = LightsOutApplication.class.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            List<String> lines = reader.lines().collect(Collectors.toList());
            int depth = Integer.parseInt(lines.get(0));
            String[] boardRows = lines.get(1).split(",");
            int[][] initialState = new int[boardRows.length][boardRows[0].length()];
            for (int i = 0; i < boardRows.length; i++) {
                for (int j = 0; j < boardRows[i].length(); j++) {
                    char cell = boardRows[i].charAt(j);
                    if (Character.isDigit(cell)) {
                        initialState[i][j] = Character.getNumericValue(cell);
                    } else {
                        initialState[i][j] = cell == 'x' ? 1 : 0;
                    }
                }
            }
            String[] pieceDescriptions = lines.get(2).split(" ");
            Piece[] pieces = new Piece[pieceDescriptions.length];
            for (int i = 0; i < pieceDescriptions.length; i++) {
                pieces[i] = new Piece(pieceDescriptions[i].split(","));
            }
            LightsOutSolver solver = new LightsOutSolver(initialState, depth, pieces);
            solver.solve();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing the depth or board values: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}