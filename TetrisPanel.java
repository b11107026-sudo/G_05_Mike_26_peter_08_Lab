import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TetrisPanel extends JPanel implements ActionListener, KeyListener {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int SCORE_PER_LINE = 250;
    private static final int MAX_SCORE = 1000000;
    private static final int NORMAL_FALL_DELAY = 360;
    private static final int FAST_FALL_DELAY = 60;
    private static final int EXPLOSION_STEPS = 6;
    private static final int EXPLOSION_DELAY = 100;
    private static final String[] IMAGE_FOLDERS = {"resources/image", "../resources/image", "image"};

    private final TetrisMain main;
    private final TetrisBoard board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentRotation;
    private int pieceX;
    private int pieceY;
    private int score;
    private boolean gameOver;
    private boolean fastDrop;
    private boolean paused;
    private final Timer timer;
    private final Timer explosionTimer;
    private boolean exploding;
    private List<Integer> explodingRows;
    private int explosionFrame;
    private Image backgroundImage;
    private Image startMenuImage;
    private Image gameOverImage;
    private final Image[] pieceImages;
    private final Color ghostColor = new Color(255, 255, 255, 100);

    public TetrisPanel(TetrisMain main) {
        this.main = main;
        this.board = new TetrisBoard(BOARD_WIDTH, BOARD_HEIGHT);
        setPreferredSize(new Dimension(BOARD_WIDTH * CELL_SIZE + 200, BOARD_HEIGHT * CELL_SIZE));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(NORMAL_FALL_DELAY, this);
        explosionTimer = new Timer(EXPLOSION_DELAY, e -> handleExplosionFrame());
        exploding = false;
        explodingRows = new ArrayList<>();
        fastDrop = false;
        pieceImages = new Image[Tetromino.values().length];
        loadImages();
    }

    private void loadImages() {
        backgroundImage = loadImage("background1.png");
        startMenuImage = loadImage("startmenubkg.png");
        gameOverImage = loadImage("game-over.png");
        for (Tetromino piece : Tetromino.values()) {
            pieceImages[piece.ordinal()] = loadImage(piece.name() + ".png");
        }
    }

    private Image loadImage(String fileName) {
        for (String folder : IMAGE_FOLDERS) {
            File file = new File(folder, fileName);
            if (file.exists()) {
                try {
                    return ImageIO.read(file);
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    public void startGame() {
        RecordManager.deleteSavedGame();
        board.reset();
        score = 0;
        gameOver = false;
        paused = false;
        currentPiece = Tetromino.random();
        nextPiece = Tetromino.random();
        currentRotation = 0;
        pieceX = 3;
        pieceY = -1;
        exploding = false;
        explosionFrame = 0;
        explodingRows.clear();
        fastDrop = false;
        explosionTimer.stop();
        timer.setDelay(NORMAL_FALL_DELAY);
        timer.start();
        SoundManager.playBackground();
        repaint();
    }

    public void stopGame() {
        timer.stop();
        explosionTimer.stop();
        if (!gameOver && currentPiece != null) {
            saveGameState();
        }
        SoundManager.stopBackground();
    }

    public boolean loadSavedGame() {
        RecordManager.GameSave save = RecordManager.loadSavedGame();
        if (save == null) {
            return false;
        }
        board.setGrid(save.grid);
        score = save.score;
        currentPiece = Tetromino.values()[save.currentPieceOrdinal];
        nextPiece = Tetromino.values()[save.nextPieceOrdinal];
        currentRotation = save.currentRotation;
        pieceX = save.pieceX;
        pieceY = save.pieceY;
        gameOver = false;
        paused = false;
        exploding = false;
        explosionFrame = 0;
        explodingRows.clear();
        timer.setDelay(NORMAL_FALL_DELAY);
        return true;
    }

    public void resumeSavedGame() {
        if (paused) {
            paused = false;
        }
        timer.start();
        SoundManager.playBackground();
        repaint();
    }

    private void saveGameState() {
        int[][] grid = board.getGridCopy();
        RecordManager.saveGameState(score, currentPiece.ordinal(), nextPiece.ordinal(), currentRotation, pieceX, pieceY, grid);
    }

    private void clearSavedGame() {
        RecordManager.deleteSavedGame();
    }

    public void togglePause() {
        if (gameOver) {
            return;
        }
        paused = !paused;
        if (paused) {
            timer.stop();
        } else {
            timer.start();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        drawBoard(g);
        drawGhostPiece(g);
        if (exploding) {
            drawExplosionOverlay(g);
        }
        drawCurrentPiece(g);
        drawSidePanel(g);
        if (paused) {
            drawPauseOverlay(g);
        }
    }

    private void drawExplosionOverlay(Graphics g) {
        if (explodingRows == null || explodingRows.isEmpty()) {
            return;
        }
        Color explosionColor = explosionFrame % 2 == 0 ? Color.ORANGE : Color.YELLOW;
        g.setColor(new Color(explosionColor.getRed(), explosionColor.getGreen(), explosionColor.getBlue(), 180));
        for (int row : explodingRows) {
            if (row < 0 || row >= BOARD_HEIGHT) {
                continue;
            }
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            }
        }
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int value = board.getCell(row, col);
                drawCell(g, col, row, value);
            }
        }
        g.setColor(Color.GRAY);
        for (int row = 0; row <= BOARD_HEIGHT; row++) {
            g.drawLine(0, row * CELL_SIZE, BOARD_WIDTH * CELL_SIZE, row * CELL_SIZE);
        }
        for (int col = 0; col <= BOARD_WIDTH; col++) {
            g.drawLine(col * CELL_SIZE, 0, col * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        }
    }

    private void drawCurrentPiece(Graphics g) {
        if (currentPiece == null) {
            return;
        }
        int[][] shape = currentPiece.getShape(currentRotation);
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = pieceX + col;
                    int boardY = pieceY + row;
                    if (boardY >= 0) {
                        drawCell(g, boardX, boardY, currentPiece.ordinal() + 1);
                    }
                }
            }
        }
    }

    private void drawGhostPiece(Graphics g) {
        if (currentPiece == null || gameOver) {
            return;
        }
        int ghostY = computeGhostY();
        int[][] shape = currentPiece.getShape(currentRotation);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(ghostColor);
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = pieceX + col;
                    int boardY = ghostY + row;
                    if (boardY >= 0) {
                        int px = boardX * CELL_SIZE;
                        int py = boardY * CELL_SIZE;
                        g2d.fillRect(px + 2, py + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                    }
                }
            }
        }
        g2d.dispose();
    }

    private int computeGhostY() {
        int ghostY = pieceY;
        while (canMove(currentPiece, currentRotation, pieceX, ghostY + 1)) {
            ghostY++;
        }
        return ghostY;
    }

    private void drawSidePanel(Graphics g) {
        int panelX = BOARD_WIDTH * CELL_SIZE + 20;
        if (startMenuImage != null) {
            g.drawImage(startMenuImage, panelX - 10, 0, getWidth() - panelX + 10, getHeight(), this);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("下個方塊", panelX, 30);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.drawString("分數: " + score, panelX, 180);
        g.drawString("空白鍵: 翻轉", panelX, 220);
        g.drawString("← →: 左右移動", panelX, 250);
        g.drawString("↓: 按住加速下落", panelX, 280);
        g.drawString("Enter: 返回菜單", panelX, 310);

        if (nextPiece != null) {
            int[][] shape = nextPiece.getShape(0);
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        int x = panelX + col * CELL_SIZE;
                        int y = 40 + row * CELL_SIZE;
                        Image image = pieceImages[nextPiece.ordinal()];
                        if (image != null) {
                            g.drawImage(image, x, y, CELL_SIZE, CELL_SIZE, this);
                            g.setColor(Color.BLACK);
                            g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                        } else {
                            g.setColor(nextPiece.getColor());
                            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                            g.setColor(Color.BLACK);
                            g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                        }
                    }
                }
            }
        }

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 32));
            g.drawString("遊戲結束", 60, 300);
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g.drawString("按 Enter 返回選單", 40, 340);
        }
    }

    private void drawCell(Graphics g, int x, int y, int value) {
        int px = x * CELL_SIZE;
        int py = y * CELL_SIZE;
        if (value > 0) {
            Image image = pieceImages[value - 1];
            if (image != null) {
                g.drawImage(image, px, py, CELL_SIZE, CELL_SIZE, this);
                g.setColor(Color.BLACK);
                g.drawRect(px, py, CELL_SIZE, CELL_SIZE);
            } else {
                Tetromino piece = Tetromino.values()[value - 1];
                g.setColor(piece.getColor());
                g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(px, py, CELL_SIZE, CELL_SIZE);
            }
        } else {
            g.setColor(new Color(40, 40, 40));
            g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
        }
    }

    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.drawString("遊戲暫停", 60, 220);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.drawString("按 P 繼續", 70, 260);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || exploding || paused) {
            return;
        }
        if (canMove(currentPiece, currentRotation, pieceX, pieceY + 1)) {
            pieceY++;
        } else {
            board.lockPiece(currentPiece, currentRotation, pieceX, pieceY);
            List<Integer> lines = board.findFullRows();
            if (!lines.isEmpty()) {
                startExplosion(lines);
                return;
            }
            spawnNextPiece();
        }
        repaint();
    }

    private void spawnNextPiece() {
        currentPiece = nextPiece;
        nextPiece = Tetromino.random();
        currentRotation = 0;
        pieceX = 3;
        pieceY = -1;
        if (!canMove(currentPiece, currentRotation, pieceX, pieceY)) {
            gameOver = true;
            timer.stop();
            SoundManager.stopBackground();
            SoundManager.playGameOver();
            RecordManager.saveFinishedScore(score);
            RecordManager.deleteSavedGame();
        }
    }

    private void startExplosion(List<Integer> rows) {
        exploding = true;
        explosionFrame = 0;
        explodingRows = new ArrayList<>(rows);
        timer.stop();
        explosionTimer.start();
        SoundManager.playExplosion();
        repaint();
    }

    private void handleExplosionFrame() {
        explosionFrame++;
        if (explosionFrame >= EXPLOSION_STEPS) {
            explosionTimer.stop();
            int linesCleared = board.clearRowsWithGravity(explodingRows);
            if (linesCleared > 0) {
                SoundManager.playClear();
            }
            score += linesCleared * SCORE_PER_LINE;
            if (score < 0) {
                score = 0;
            }
            exploding = false;
            explodingRows.clear();
            
            List<Integer> newLines = board.findFullRows();
            if (!newLines.isEmpty()) {
                startExplosion(newLines);
            } else {
                spawnNextPiece();
                if (!gameOver) {
                    timer.setDelay(fastDrop ? FAST_FALL_DELAY : NORMAL_FALL_DELAY);
                    timer.start();
                }
            }
        }
        repaint();
    }

    private boolean canMove(Tetromino piece, int rotation, int x, int y) {
        return board.canPlace(piece, rotation, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P && !gameOver) {
            togglePause();
            return;
        }
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                main.showMenu();
            }
            return;
        }
        if (paused) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canMove(currentPiece, currentRotation, pieceX - 1, pieceY)) {
                    pieceX--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (canMove(currentPiece, currentRotation, pieceX + 1, pieceY)) {
                    pieceX++;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (canMove(currentPiece, currentRotation, pieceX, pieceY + 1)) {
                    pieceY++;
                    SoundManager.playDrop();
                }
                fastDrop = true;
                timer.setDelay(FAST_FALL_DELAY);
                break;
            case KeyEvent.VK_SPACE:
                int nextRotation = (currentRotation + 1) % currentPiece.getRotationCount();
                if (canMove(currentPiece, nextRotation, pieceX, pieceY)) {
                    currentRotation = nextRotation;
                    SoundManager.playRotate();
                }
                break;
            case KeyEvent.VK_ENTER:
                main.showMenu();
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            fastDrop = false;
            if (!paused) {
                timer.setDelay(NORMAL_FALL_DELAY);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // no action needed
    }
}
