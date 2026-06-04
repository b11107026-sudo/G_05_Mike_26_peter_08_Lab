import java.util.ArrayList;
import java.util.List;

public class TetrisBoard {
    private final int width;
    private final int height;
    private final int[][] grid;

    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[height][width];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCell(int row, int col) {
        return grid[row][col];
    }

    public int[][] getGridCopy() {
        int[][] copy = new int[height][width];
        for (int row = 0; row < height; row++) {
            System.arraycopy(grid[row], 0, copy[row], 0, width);
        }
        return copy;
    }

    public void setGrid(int[][] newGrid) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = (row < newGrid.length && col < newGrid[row].length) ? newGrid[row][col] : 0;
            }
        }
    }

    public void reset() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = 0;
            }
        }
    }

    public boolean canPlace(Tetromino piece, int rotation, int offsetX, int offsetY) {
        int[][] shape = piece.getShape(rotation);
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = offsetX + col;
                    int boardY = offsetY + row;
                    if (boardX < 0 || boardX >= width) {
                        return false;
                    }
                    if (boardY >= height) {
                        return false;
                    }
                    if (boardY >= 0 && grid[boardY][boardX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void lockPiece(Tetromino piece, int rotation, int offsetX, int offsetY) {
        int[][] shape = piece.getShape(rotation);
        int colorIndex = piece.ordinal() + 1;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = offsetX + col;
                    int boardY = offsetY + row;
                    if (boardY >= 0 && boardY < height && boardX >= 0 && boardX < width) {
                        grid[boardY][boardX] = colorIndex;
                    }
                }
            }
        }
    }

    public List<Integer> findFullRows() {
        List<Integer> fullRows = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            if (isFullRow(row)) {
                fullRows.add(row);
            }
        }
        return fullRows;
    }

    public int clearRowsWithGravity(List<Integer> rowsToClear) {
        if (rowsToClear.isEmpty()) {
            return 0;
        }

        boolean[] clearMask = new boolean[height];
        for (int row : rowsToClear) {
            if (row >= 0 && row < height) {
                clearMask[row] = true;
            }
        }

        int destRow = height - 1;
        for (int row = height - 1; row >= 0; row--) {
            if (!clearMask[row]) {
                System.arraycopy(grid[row], 0, grid[destRow], 0, width);
                destRow--;
            }
        }
        for (int row = destRow; row >= 0; row--) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = 0;
            }
        }

        // 將每列的方塊整組向下壓，保持垂直塊的相對順序。
        for (int col = 0; col < width; col++) {
            int writeRow = height - 1;
            for (int row = height - 1; row >= 0; row--) {
                if (grid[row][col] != 0) {
                    int value = grid[row][col];
                    grid[row][col] = 0;
                    grid[writeRow][col] = value;
                    writeRow--;
                }
            }
            for (int row = writeRow; row >= 0; row--) {
                grid[row][col] = 0;
            }
        }

        return rowsToClear.size();
    }

    private boolean isFullRow(int row) {
        for (int col = 0; col < width; col++) {
            if (grid[row][col] == 0) {
                return false;
            }
        }
        return true;
    }
}

