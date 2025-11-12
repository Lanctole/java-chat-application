package lanctole.startup;

import lanctole.controller.Controller;
import lanctole.exception.ConnectionFailedException;
import lanctole.messagehandler.MessageHandler;
import lanctole.view.ChatView;
import lanctole.view.window.ConnectionInputWindow;
import lanctole.view.window.SetNicknameInputWindow;

import javax.swing.*;

public class ClientStartupFlow {
    private static final String NICKNAME_CANNOT_BY_EMPTY = "Nickname cannot by empty";
    private static final String ERROR = "Error";
    private static final String PORT_IS_NOT_A_VALID_NUMBER = "Port is not a valid number";
    private static final int MAX_NICKNAME_LENGTH = 32;
    private static final String NICKNAME_LENGTH_ERROR =
            "Nickname must be shorter then " + MAX_NICKNAME_LENGTH + " characters";
    private static final String CONNECTION_ERROR = "Connection error";

    private final Controller controller;
    private final MessageHandler handler;
    private SetNicknameInputWindow nicknameWindow;

    public ClientStartupFlow(Controller controller, MessageHandler handler) {
        this.controller = controller;
        this.handler = handler;
    }

    public void start(int port, String host) {
        showConnectionWindow(port, host);
    }

    private void showConnectionWindow(int port, String host) {
        ConnectionInputWindow connectionWindow = new ConnectionInputWindow(port, host);
        connectionWindow.setButtonActionListener(e -> {
            String inputtedHost = connectionWindow.getInputHost();
            int inputtedPort;

            try {
                inputtedPort = Integer.parseInt(connectionWindow.getInputPort());
                controller.connect(inputtedHost, inputtedPort);
                connectionWindow.dispose();
                showNicknameWindow();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        connectionWindow,
                        PORT_IS_NOT_A_VALID_NUMBER,
                        ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (ConnectionFailedException ex) {
                JOptionPane.showMessageDialog(
                        connectionWindow,
                        ex.getMessage(),
                        CONNECTION_ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    public void showMainView() {
        ChatView chatView = new ChatView(controller);
        handler.addSubscriber(chatView);
    }

    private void showNicknameWindow() {
        if (nicknameWindow == null) {
            nicknameWindow = new SetNicknameInputWindow();
            handler.addNicknameOperationsListener(nicknameWindow);

            nicknameWindow.setOnNicknameAccepted(() -> {
                nicknameWindow.dispose();
                showMainView();
            });

            nicknameWindow.setButtonActionListener(e -> {
                String nickname = nicknameWindow.getInput().trim();
                if (nickname.isBlank()) {
                    JOptionPane.showMessageDialog(
                            nicknameWindow,
                            NICKNAME_CANNOT_BY_EMPTY,
                            ERROR,
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if ( nickname.length() > MAX_NICKNAME_LENGTH) {
                    JOptionPane.showMessageDialog(
                            nicknameWindow,
                            NICKNAME_LENGTH_ERROR,
                            ERROR,
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                controller.setNewName(nickname);
            });
        } else {
            nicknameWindow.setVisible(true);
            nicknameWindow.toFront();
        }
    }

}
