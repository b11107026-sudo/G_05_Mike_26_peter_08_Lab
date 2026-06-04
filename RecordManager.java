import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordManager {
    private static final File SAVE_FILE = new File("tetris_save.txt");
    private static final File SCORE_FILE = new File("tetris_scores.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean hasSavedGame() {
        return SAVE_FILE.exists();
    }

    public static void saveGameState(int score, int currentPieceOrdinal, int nextPieceOrdinal, int currentRotation, int pieceX, int pieceY, int[][] grid) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write(String.valueOf(score));
            writer.newLine();
            writer.write(String.valueOf(currentPieceOrdinal));
            writer.newLine();
            writer.write(String.valueOf(nextPieceOrdinal));
            writer.newLine();
            writer.write(String.valueOf(currentRotation));
            writer.newLine();
            writer.write(String.valueOf(pieceX));
            writer.newLine();
            writer.write(String.valueOf(pieceY));
            writer.newLine();
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    writer.write(String.valueOf(grid[row][col]));
                    if (col < grid[row].length - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException ignored) {
        }
    }

    public static GameSave loadSavedGame() {
        if (!SAVE_FILE.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            int score = Integer.parseInt(reader.readLine());
            int currentPieceOrdinal = Integer.parseInt(reader.readLine());
            int nextPieceOrdinal = Integer.parseInt(reader.readLine());
            int currentRotation = Integer.parseInt(reader.readLine());
            int pieceX = Integer.parseInt(reader.readLine());
            int pieceY = Integer.parseInt(reader.readLine());
            List<int[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int[] row = new int[parts.length];
                for (int col = 0; col < parts.length; col++) {
                    row[col] = Integer.parseInt(parts[col]);
                }
                rows.add(row);
            }
            int[][] grid = rows.toArray(new int[0][]);
            return new GameSave(score, currentPieceOrdinal, nextPieceOrdinal, currentRotation, pieceX, pieceY, grid);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void deleteSavedGame() {
        if (SAVE_FILE.exists()) {
            SAVE_FILE.delete();
        }
    }

    public static void saveFinishedScore(int score) {
        try {
            if (!SCORE_FILE.exists()) {
                SCORE_FILE.createNewFile();
            }
        } catch (IOException ignored) {
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            String record = FORMATTER.format(LocalDateTime.now()) + " - " + score;
            writer.write(record);
            writer.newLine();
        } catch (IOException ignored) {
        }
    }

    public static List<String> loadScoreHistory(int maxEntries) {
        if (!SCORE_FILE.exists()) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignored) {
        }
        Collections.reverse(lines);
        return lines.size() <= maxEntries ? lines : lines.subList(0, maxEntries);
    }

    public static class GameSave {
        public final int score;
        public final int currentPieceOrdinal;
        public final int nextPieceOrdinal;
        public final int currentRotation;
        public final int pieceX;
        public final int pieceY;
        public final int[][] grid;

        public GameSave(int score, int currentPieceOrdinal, int nextPieceOrdinal, int currentRotation, int pieceX, int pieceY, int[][] grid) {
            this.score = score;
            this.currentPieceOrdinal = currentPieceOrdinal;
            this.nextPieceOrdinal = nextPieceOrdinal;
            this.currentRotation = currentRotation;
            this.pieceX = pieceX;
            this.pieceY = pieceY;
            this.grid = grid;
        }
    }
}
