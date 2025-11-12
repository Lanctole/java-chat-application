package lanctole.view.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class MainWindow extends JFrame {
    private static final int WIDTH = 925;
    private static final int HEIGHT = 500;
    private static final String SEND_BUTTON_TEXT = "Send";
    private static final String WINDOW_NAME = "Chat";
    private final JPanel messagePanel = new JPanel();
    private final JScrollPane scrollPane;
    private final JTextField textField = new JTextField();
    private final JButton sendButton;

    public MainWindow() {
        super(WINDOW_NAME);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sendButton = new JButton(SEND_BUTTON_TEXT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        sendButton.setPreferredSize(new Dimension(199, 30));

        inputPanel.add(textField);
        inputPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        inputPanel.add(sendButton);

        bottomPanel.add(inputPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.add(messagePanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(messageContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setChatMembersPanel(JPanel chatMembersPanel) {
        getContentPane().add(chatMembersPanel, BorderLayout.EAST);
        revalidate();
    }

    public void updateMessagePanel(JPanel incomingMessage) {
        incomingMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagePanel.add(incomingMessage);

        messagePanel.revalidate();
        messagePanel.repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
            messagePanel.revalidate();
            messagePanel.repaint();
            incomingMessage.revalidate();
            incomingMessage.repaint();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void setWindowListener(WindowAdapter windowAdapter) {
        addWindowListener(windowAdapter);
    }

    public void setTextFieldListener(ActionListener actionListener) {
        textField.addActionListener(actionListener);
        if (sendButton != null) {
            sendButton.addActionListener(actionListener);
        }
    }

    public String getTextFieldContent() {
        return this.textField.getText();
    }

    public void setTextFieldContent(String text) {
        this.textField.setText(text);
    }

    public void toggleUIEnabled(boolean enabled) {
        textField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
}