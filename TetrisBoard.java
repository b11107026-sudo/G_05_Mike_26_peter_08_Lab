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

    public int clearLines() {
        int cleared = 0;
        for (int row = height - 1; row >= 0; row--) {
            if (isFullRow(row)) {
                clearRow(row);
                cleared++;
                row++; // recheck same row after shifting
            }
        }
        return cleared;
    }

    private boolean isFullRow(int row) {
        for (int col = 0; col < width; col++) {
            if (grid[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearRow(int row) {
        for (int r = row; r > 0; r--) {
            System.arraycopy(grid[r - 1], 0, grid[r], 0, width);
        }
        for (int col = 0; col < width; col++) {
            grid[0][col] = 0;
        }
    }
}
