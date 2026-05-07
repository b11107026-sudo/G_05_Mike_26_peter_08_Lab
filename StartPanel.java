import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {
    public StartPanel(TetrisMain main) {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("俄羅斯方塊", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        add(title, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("寬度 10 格，高度 20 格", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(Color.LIGHT_GRAY);
        add(subtitle, gbc);

        gbc.gridy++;
        JButton startButton = new JButton("開始遊戲");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        startButton.addActionListener(e -> main.showGame());
        add(startButton, gbc);

        gbc.gridy++;
        JButton exitButton = new JButton("離開");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton, gbc);

        gbc.gridy++;
        JLabel info = new JLabel("使用方向鍵移動，空白鍵翻轉，得分 50 分 / 行，滿分 1000", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setForeground(Color.WHITE);
        add(info, gbc);

        gbc.gridy++;
        JLabel hint = new JLabel("遊戲結束後按 Enter 返回選單", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 14));
        hint.setForeground(Color.GRAY);
        add(hint, gbc);
    }
}
