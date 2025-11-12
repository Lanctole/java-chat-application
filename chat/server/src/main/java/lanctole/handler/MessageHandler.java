package lanctole.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.manager.MessageDispatcher;
import lanctole.message.Message;
import lanctole.message.MessageFactory;
import lanctole.message.MessageType;

public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final MessageDispatcher dispatcher;

    public MessageHandler(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void handleMessage(Message message, SessionHandler sessionHandler) {
        switch (message.getType()) {
            case MESSAGE -> handleChatMessage(message, sessionHandler);
            case SET_NAME, OLD_NAME -> handleUserInitialization(message, sessionHandler);
            case DISCONNECT -> handleDisconnect(sessionHandler);
            case PING -> handlePing();
            default -> logger.warn("Unknown message type: {}", message.getType());
        }
    }

    private void handleChatMessage(Message message, SessionHandler sessionHandler) {
        Message msg = MessageFactory.createMessage(MessageType.MESSAGE, message.getContent(), sessionHandler.getUserName());
        dispatcher.broadcast(msg);
    }

    private void handleUserInitialization(Message message, SessionHandler sessionHandler) {
        sessionHandler.initiateUser(message);
    }

    private void handleDisconnect(SessionHandler sessionHandler) {
        sessionHandler.close();
    }

    private void handlePing() {
        logger.debug("Ping received");
    }
}
