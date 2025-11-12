package lanctole.messagehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.events.MessagePublisher;
import lanctole.events.MessageSubscriber;
import lanctole.events.NicknameOperationsListener;
import lanctole.message.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageHandler implements MessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final List<MessageSubscriber> messageSubscribers = new ArrayList<>();
    private List<String> chatMembers = new ArrayList<>();
    private final List<NicknameOperationsListener> nicknameOperationsListeners = new CopyOnWriteArrayList<>();

    public void handleIncomingMessage(Message message) {
        switch (message.getType()) {
            case MESSAGE, SYSTEM_NOTIFICATION -> notifySubscribers(message);
            case REFRESH_CHAT_MEMBERS_LIST -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    chatMembers = mapper.readValue(message.getContent(), new TypeReference<>() {
                    });
                    notifyChatMembersRefreshed();
                } catch (IOException e) {
                    notifySubscribersWarning("Error while refreshing members list");
                }
            }
            case NICKNAME_DENIED -> notifyNicknameDenied(message);
            case NICKNAME_ACCEPTED -> notifyNicknameAccepted(message);
            case OLD_NAME -> log.warn("Old name message");
            default -> throw new IllegalArgumentException();
        }
    }

    public void notifyNicknameDenied(Message message) {
        for (var listener : nicknameOperationsListeners) {
            listener.onNicknameDenied(message);
        }
    }

    public void notifyNicknameAccepted(Message message) {
        for (var listener : nicknameOperationsListeners) {
            listener.onNicknameAccepted(message);
        }
    }

    @Override
    public void notifySubscribers(Message message) {
        for (var subscriber : this.messageSubscribers) {
            subscriber.updateMessage(message);
        }
    }

    @Override
    public void notifyChatMembersRefreshed(){
        for (var subscriber : this.messageSubscribers) {
            subscriber.updateChatMembers(chatMembers);
        }
    }

    @Override
    public void notifySubscribersWarning(String warningMessage) {
        for (var subscriber : this.messageSubscribers) {
            subscriber.updateWarning(warningMessage);
        }
    }

    @Override
    public void addSubscriber(MessageSubscriber messageSubscriber) {
        this.messageSubscribers.add(messageSubscriber);
    }

    @Override
    public void addNicknameOperationsListener(NicknameOperationsListener listener) {
        nicknameOperationsListeners.add(listener);
    }
}