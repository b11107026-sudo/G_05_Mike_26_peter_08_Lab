import javax.swing.*;
import java.awt.*;

public class TetrisMain {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cards;
    private StartPanel startPanel;
    private TetrisPanel gamePanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TetrisMain().createAndShowGui());
    }

    private void createAndShowGui() {
        frame = new JFrame("俄羅斯方塊");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        startPanel = new StartPanel(this);
        gamePanel = new TetrisPanel(this);

        cards.add(startPanel, "MENU");
        cards.add(gamePanel, "GAME");

        frame.add(cards);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showMenu();
    }

    public void showGame() {
        cardLayout.show(cards, "GAME");
        gamePanel.startGame();
        gamePanel.requestFocusInWindow();
    }

    public void showMenu() {
        cardLayout.show(cards, "MENU");
        startPanel.requestFocusInWindow();
    }
}
