import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class StartPanel extends JPanel {
    private Image backgroundImage;
    private final JTextArea recordArea;

    public StartPanel(TetrisMain main) {
        loadBackgroundImage();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleChinese = new JLabel("俄羅斯方塊", SwingConstants.CENTER);
        titleChinese.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleChinese.setForeground(new Color(100, 200, 255));
        add(titleChinese, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton startButton = new JButton("開始遊戲");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> main.startNewGame());
        add(startButton, gbc);

        gbc.gridy++;
        JButton exitButton = new JButton("離開遊戲");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        exitButton.setBackground(new Color(200, 50, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel info = new JLabel("← → 左右移動  ↓ 加速下落  空白 翻轉  P 暫停", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setForeground(new Color(200, 200, 200));
        add(info, gbc);

        gbc.gridy++;
        recordArea = new JTextArea(6, 28);
        recordArea.setEditable(false);
        recordArea.setOpaque(false);
        recordArea.setForeground(new Color(150, 255, 150));
        recordArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        recordArea.setLineWrap(true);
        recordArea.setWrapStyleWord(true);
        add(recordArea, gbc);
    }

    private void loadBackgroundImage() {
        String[] imageFolders = {"resources/image", "../resources/image", "image"};
        for (String folder : imageFolders) {
            File file = new File(folder, "startmenubkg.png");
            if (file.exists()) {
                try {
                    backgroundImage = ImageIO.read(file);
                    return;
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void refreshMenu() {
        List<String> history = RecordManager.loadScoreHistory(6);
        if (history.isEmpty()) {
            recordArea.setText("✦ 高分紀錄 ✦\n\n還沒有成績紀錄\n每次消行可得 250 分\n每按一次向下鍵獲得 1 分");
        } else {
            StringBuilder sb = new StringBuilder("✦ 高分紀錄 ✦\n");
            for (int i = 0; i < history.size(); i++) {
                sb.append(i + 1).append(". ").append(history.get(i)).append("\n");
            }
            recordArea.setText(sb.toString());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            super.paintComponent(g);
        }
    }
}
