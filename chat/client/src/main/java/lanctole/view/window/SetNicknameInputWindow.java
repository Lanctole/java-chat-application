package lanctole.view.window;

import lanctole.events.NicknameOperationsListener;
import lanctole.message.Message;

import javax.swing.*;
import java.awt.event.ActionListener;

public class SetNicknameInputWindow extends JFrame implements NicknameOperationsListener {
    private Runnable onSuccess;

    private static final String SET_YOUR_NICKNAME = "Set your nickname";
    private static final String NICKNAME = "Nickname:";
    private static final String OK = "OK";
    private static final String DEFAULT_TEXT = "";
    private static final String CONNECTION_DENIED_ERROR = "Error";
    private final JTextField input;
    private final JButton button;

    public SetNicknameInputWindow() {
        super(SET_YOUR_NICKNAME);
        JLabel nickNameLabel = new JLabel(NICKNAME);
        JPanel panel = new JPanel();
        JLabel output = new JLabel();
        input = new JTextField(10);
        add(panel);
        panel.add(nickNameLabel);
        input.setText(DEFAULT_TEXT);
        panel.add(input);
        button = new JButton(OK);
        panel.add(button);
        panel.add(output);

        setSize(300, 85);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getInput() {
        return this.input.getText();
    }

    public void setButtonActionListener(ActionListener e) {
        this.input.addActionListener(e);
        this.button.addActionListener(e);
    }

    @Override
    public void onNicknameDenied(Message message) {
        JOptionPane.showMessageDialog(
                this,
                message.getContent(),
                CONNECTION_DENIED_ERROR,
                JOptionPane.ERROR_MESSAGE
        );
        input.setText(DEFAULT_TEXT);
    }

    public void setOnNicknameAccepted(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @Override
    public void onNicknameAccepted(Message message) {
        dispose();
        if (onSuccess != null) {
            onSuccess.run();
        }
    }
}
