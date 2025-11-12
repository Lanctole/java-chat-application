package lanctole.events;

import lanctole.message.Message;

public interface MessagePublisher {
    void notifySubscribers(Message message);
    void notifySubscribersWarning(String warningMessage);

    void addSubscriber(MessageSubscriber messageSubscriber);
    void notifyChatMembersRefreshed();
    void addNicknameOperationsListener(NicknameOperationsListener listener);
}
