package lanctole.events;

import lanctole.message.Message;

import java.util.List;

public interface MessageSubscriber {
    void updateMessage(Message message);
    void updateWarning(String warningMessage);
    void updateChatMembers(List<String> chatMembers);
}
