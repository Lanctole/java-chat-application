package lanctole.view.panel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChatMembersPanel extends JPanel {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public ChatMembersPanel(List<String> chatMembers) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        JLabel header = new JLabel("Members");
        header.setFont(new Font("Verdana", Font.BOLD, 14));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        JList<String> displayList = new JList<>(listModel);
        displayList.setFont(new Font("Verdana", Font.PLAIN, 12));
        displayList.setFixedCellWidth(180);


        JScrollPane scrollPane = new JScrollPane(displayList);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        Color color = new Color(229, 228, 243);
        displayList.setBackground(color);
        scrollPane.getViewport().setBackground(color);
        scrollPane.setBackground(color);
        add(scrollPane, BorderLayout.CENTER);

        updateChatMembers(chatMembers);
    }

    public void updateChatMembers(List<String> chatMembers) {
        if (chatMembers == null) return;
        SwingUtilities.invokeLater(() -> {
            listModel.removeAllElements();
            for (String member : chatMembers) {
                listModel.addElement(member);
            }
        });
    }
}
