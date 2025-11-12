package lanctole.service;

import lanctole.manager.MessageDispatcher;
import lanctole.message.Message;
import lanctole.message.MessageFactory;

import java.util.Set;

public class MessageService {
    private final MessageDispatcher dispatcher;

    public MessageService(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Message createNicknameAcceptedMessage() {
        return MessageFactory.createNicknameAcceptedMessage();
    }

    public Message createOldNameMessage() {
        return MessageFactory.createOldNameMessage();
    }

    public void broadcastSystemMessage(String content) {
        Message broadcastMessage = MessageFactory.createSystemMessage(content);
        dispatcher.broadcast(broadcastMessage);
    }

    public Message createNicknameDeniedMessage(String reason) {
        return MessageFactory.createNicknameDeniedMessage(reason);
    }

    public void broadcastMemberList(Set<String> userNames) {
        Message broadcastMessage = MessageFactory.createMembersListMessage(userNames);
        dispatcher.broadcast(broadcastMessage);
    }
}
