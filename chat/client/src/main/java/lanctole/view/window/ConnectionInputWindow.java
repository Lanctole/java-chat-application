package lanctole.view.window;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.ActionListener;

public class ConnectionInputWindow extends JFrame {
    private static final String JOIN_CHAT = "Join chat";
    private static final String CHAT_IP = "Chat IP:";
    private static final String CHAT_PORT = "PORT:";
    private static final String CONNECT = "connect";
    private final JTextField inputHost;
    private final JTextField inputPort;
    private final JButton button;

    public ConnectionInputWindow(int port, String host) {
        super(JOIN_CHAT);
        setResizable(false);

        JPanel panel = new JPanel();
        JLabel output = new JLabel();
        inputHost = new JTextField(10);
        inputPort = new JTextField(10);

        ((AbstractDocument) inputPort.getDocument()).setDocumentFilter(new DigitFilter());

        add(panel);
        JLabel chatIpLabel = new JLabel(CHAT_IP);
        JLabel chatPortLabel = new JLabel(CHAT_PORT);

        inputHost.setText(host);
        inputPort.setText(String.valueOf(port));
        button = new JButton(CONNECT);
        panel.add(chatIpLabel);
        panel.add(inputHost);
        panel.add(chatPortLabel);
        panel.add(inputPort);
        panel.add(button);
        panel.add(output);
        setSize(400, 100);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getInputHost() {
        return this.inputHost.getText();
    }

    public String getInputPort() {
        return this.inputPort.getText();
    }

    public void setButtonActionListener(ActionListener e) {
        inputHost.addActionListener(e);
        inputPort.addActionListener(e);
        button.addActionListener(e);
    }

    private static class DigitFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
