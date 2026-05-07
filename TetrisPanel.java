import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TetrisPanel extends JPanel implements ActionListener, KeyListener {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int SCORE_PER_LINE = 50;
    private static final int MAX_SCORE = 1000;

    private final TetrisMain main;
    private final TetrisBoard board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentRotation;
    private int pieceX;
    private int pieceY;
    private int score;
    private boolean gameOver;
    private final Timer timer;

    public TetrisPanel(TetrisMain main) {
        this.main = main;
        this.board = new TetrisBoard(BOARD_WIDTH, BOARD_HEIGHT);
        setPreferredSize(new Dimension(BOARD_WIDTH * CELL_SIZE + 200, BOARD_HEIGHT * CELL_SIZE));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(500, this);
    }

    public void startGame() {
        board.reset();
        score = 0;
        gameOver = false;
        currentPiece = Tetromino.random();
        nextPiece = Tetromino.random();
        currentRotation = 0;
        pieceX = 3;
        pieceY = -1;
        timer.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawCurrentPiece(g);
        drawSidePanel(g);
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

    private void drawSidePanel(Graphics g) {
        int panelX = BOARD_WIDTH * CELL_SIZE + 20;
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("下個方塊", panelX, 30);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.drawString("分數: " + score, panelX, 180);
        g.drawString("空白鍵: 翻轉", panelX, 220);
        g.drawString("← →: 左右移動", panelX, 250);
        g.drawString("↓: 快速下降", panelX, 280);
        g.drawString("Enter: 返回菜單", panelX, 310);

        if (nextPiece != null) {
            int[][] shape = nextPiece.getShape(0);
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        int x = panelX + col * CELL_SIZE;
                        int y = 40 + row * CELL_SIZE;
                        g.setColor(nextPiece.getColor());
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
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
            Tetromino piece = Tetromino.values()[value - 1];
            g.setColor(piece.getColor());
            g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(px, py, CELL_SIZE, CELL_SIZE);
        } else {
            g.setColor(new Color(40, 40, 40));
            g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }
        if (canMove(currentPiece, currentRotation, pieceX, pieceY + 1)) {
            pieceY++;
        } else {
            board.lockPiece(currentPiece, currentRotation, pieceX, pieceY);
            int lines = board.clearLines();
            score += lines * SCORE_PER_LINE;
            if (score > MAX_SCORE) {
                score = MAX_SCORE;
            }
            if (score == MAX_SCORE) {
                gameOver = true;
                timer.stop();
            } else {
                spawnNextPiece();
            }
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
        }
    }

    private boolean canMove(Tetromino piece, int rotation, int x, int y) {
        return board.canPlace(piece, rotation, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                main.showMenu();
            }
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
                }
                break;
            case KeyEvent.VK_SPACE:
                int nextRotation = (currentRotation + 1) % currentPiece.getRotationCount();
                if (canMove(currentPiece, nextRotation, pieceX, pieceY)) {
                    currentRotation = nextRotation;
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
        // no action needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // no action needed
    }
}
