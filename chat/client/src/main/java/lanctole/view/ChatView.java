package lanctole.view;

import lanctole.controller.Controller;
import lanctole.events.ConnectionListener;
import lanctole.events.MessageSubscriber;
import lanctole.message.Message;
import lanctole.view.panel.ChatMembersPanel;
import lanctole.view.panel.ChatMessagePanel;
import lanctole.view.window.MainWindow;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ChatView implements MessageSubscriber {
    private final Controller controller;
    private final MainWindow mainWindow = new MainWindow();
    private final ChatMembersPanel chatMembersPanel = new ChatMembersPanel(List.of("Loading..."));

    public ChatView(Controller controller) {
        this.controller = controller;
        setupListenersForMainFrame();
        mainWindow.setChatMembersPanel(chatMembersPanel);
        mainWindow.revalidate();
    }

    private void setupListenersForMainFrame() {
        mainWindow.setWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.disconnect();
            }
        });

        mainWindow.setTextFieldListener(e -> {
            if (!mainWindow.getTextFieldContent().isBlank()) {
                controller.sendMessage(mainWindow.getTextFieldContent());
                mainWindow.setTextFieldContent("");
            }
        });

        controller.getChatClient().addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnectionLost() {
                handleConnectionLost();
            }

            @Override
            public void onConnectionRestored() {
                handleConnectionRestored();
            }

            @Override
            public void onConnectionFinallyLost() {
                handleConnectionFinallyLost();
            }
        });
    }

    private void handleConnectionFinallyLost() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mainWindow,
                    "Connection lost. Application will be closed.",
                    "Connection Lost",
                    JOptionPane.ERROR_MESSAGE);
            mainWindow.dispose();
            System.exit(0);
        });
    }

    private void handleConnectionRestored() {
        SwingUtilities.invokeLater(() -> {
            mainWindow.toggleUIEnabled(true);
            JOptionPane.showMessageDialog(mainWindow,
                    "Connection restored!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void handleConnectionLost() {
        SwingUtilities.invokeLater(() -> {
            mainWindow.toggleUIEnabled(false);
            JOptionPane.showMessageDialog(mainWindow,
                    "Connection lost. Trying to reconnect...",
                    "Connection issues",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    @Override
    public void updateMessage(Message message) {
        mainWindow.updateMessagePanel(new ChatMessagePanel(message));
        mainWindow.revalidate();
        mainWindow.repaint();
    }

    @Override
    public void updateWarning(String warningMessage) {
        JOptionPane.showMessageDialog(mainWindow, warningMessage, "Warning!", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void updateChatMembers(List<String> chatMembers) {
        chatMembersPanel.updateChatMembers(chatMembers);
    }
}