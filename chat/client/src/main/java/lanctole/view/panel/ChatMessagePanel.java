package lanctole.view.panel;

import lanctole.exception.MessageRenderingException;
import lanctole.message.Message;
import lanctole.message.MessageType;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatMessagePanel extends JPanel {
    private static final Font BASE_FONT = new Font("Verdana", Font.PLAIN, 12);
    private static final int MAX_WIDTH = 700;

    public ChatMessagePanel(Message message) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextPane textPane = new LineBreakerTextPane();
        textPane.setEditable(false);
        textPane.setFont(BASE_FONT);
        textPane.setOpaque(true);
        textPane.setFocusable(false);
        textPane.setHighlighter(null);
        textPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (message.getType() == MessageType.SYSTEM_NOTIFICATION) {
            appendStyledSystemMessage(textPane, message);
            textPane.setBackground(new Color(238, 238, 238));
        } else {
            appendStyledUserMessage(textPane, message);
            textPane.setBorder(BorderFactory.createLineBorder(Color.white, 1));
            textPane.setBackground(new Color(184, 219, 255));
        }

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setOpaque(false);
        wrapper.add(textPane);

        textPane.setMaximumSize(new Dimension(MAX_WIDTH, Integer.MAX_VALUE));
        wrapper.setMaximumSize(new Dimension(MAX_WIDTH, Integer.MAX_VALUE));

        add(wrapper);
    }

    private void appendStyledUserMessage(JTextPane textPane, Message message) {
        StyledDocument doc = textPane.getStyledDocument();

        Style timeStyle = doc.addStyle("Time", null);
        StyleConstants.setFontSize(timeStyle, 10);
        StyleConstants.setItalic(timeStyle, true);
        StyleConstants.setForeground(timeStyle, Color.GRAY);

        Style senderStyle = doc.addStyle("Sender", null);
        StyleConstants.setFontSize(senderStyle, 12);
        StyleConstants.setBold(senderStyle, true);

        Style messageStyle = doc.addStyle("Message", null);
        StyleConstants.setFontSize(messageStyle, 12);
        StyleConstants.setForeground(messageStyle, Color.BLACK);

        try {
            doc.insertString(doc.getLength(), message.getDatetime() + "\n", timeStyle);
            doc.insertString(doc.getLength(), message.getSender() + ": ", senderStyle);
            doc.insertString(doc.getLength(), message.getContent() + "\n", messageStyle);
        } catch (BadLocationException e) {
            throw new MessageRenderingException("Error rendering user message", e);
        }
    }

    private void appendStyledSystemMessage(JTextPane textPane, Message message) {
        StyledDocument doc = textPane.getStyledDocument();

        Style systemStyle = doc.addStyle("System", null);
        StyleConstants.setFontSize(systemStyle, 11);
        StyleConstants.setItalic(systemStyle, true);
        StyleConstants.setForeground(systemStyle, Color.GRAY);
        StyleConstants.setAlignment(systemStyle, StyleConstants.ALIGN_CENTER);

        try {
            doc.insertString(doc.getLength(), message.getContent(), systemStyle);
            doc.setParagraphAttributes(0, doc.getLength(), systemStyle, false);
        } catch (BadLocationException e) {
            throw new MessageRenderingException("Error rendering system message", e);
        }
    }
}
