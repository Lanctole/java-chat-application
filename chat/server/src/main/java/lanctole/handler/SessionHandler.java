package lanctole.handler;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.manager.MessageDispatcher;
import lanctole.manager.SessionManager;
import lanctole.message.Message;
import lanctole.message.MessageType;
import lanctole.network.ClientSession;
import lanctole.service.JsonService;
import lanctole.service.MessageSender;
import lanctole.service.MessageService;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class SessionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SessionHandler.class);

    @Getter
    private final ClientSession session;
    private final SessionManager sessionManager;
    private final JsonService jsonService;
    private final MessageHandler messageHandler;
    private final MessageSender messageSender;
    private final MessageService messageService;
    @Getter
    private String userName;

    public SessionHandler(ClientSession session, SessionManager sessionManager, JsonService jsonService) {
        this.session = session;
        this.jsonService = jsonService;
        this.sessionManager = sessionManager;
        this.messageSender = new MessageSender(session, jsonService);
        MessageDispatcher dispatcher = new MessageDispatcher(sessionManager, jsonService);
        this.messageService = new MessageService(dispatcher);
        this.messageHandler = new MessageHandler(dispatcher);
    }

    @Override
    public void run() {
        try {
            session.setTimeout(30000);
            logger.info("Waiting message from user {}", userName);

            String input;
            while (!session.isClosed() && (input = session.readLine()) != null) {
                logger.debug("Message accepted: {}", input);
                Message message = jsonService.deserialize(input, Message.class);
                messageHandler.handleMessage(message, this);
            }
        } catch (SocketTimeoutException e) {
            logger.warn("User {} timed out: no data received", userName);
        } catch (IOException e) {
            logger.warn("Error connecting with user {}: {}", userName, e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void initiateUser(Message message) {
        this.userName = message.getSender();
        logger.info("Try to init user: {}", userName);
        if (sessionManager.isUserNameTaken(userName)) {
            logger.warn("User {} is already taken", userName);
            sendKickMessage("User with nickname \"" + this.userName + "\" already exists");
            return;
        }

        sessionManager.registerClient(this);
        logger.info(" user: {} registered", userName);
        Message msg;
        if (MessageType.OLD_NAME.equals(message.getType())) {
            msg = messageService.createOldNameMessage();
        } else {
            msg = messageService.createNicknameAcceptedMessage();
            messageService.broadcastSystemMessage(userName + " entered chat");
        }
        messageSender.send(msg);

        messageService.broadcastMemberList(sessionManager.getUserNames());
    }

    private void sendKickMessage(String reason) {
        Message msg = messageService.createNicknameDeniedMessage(reason);
        messageSender.send(msg);
    }

    private void disconnect() {
        try {
            if (userName != null) {
                sessionManager.unregisterClient(this);
                messageService.broadcastSystemMessage(userName + " left chat");
                messageService.broadcastMemberList(sessionManager.getUserNames());
            }
        } finally {
            close();
        }
    }

    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            logger.error("Error during closing: {}", e.getMessage());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof SessionHandler handler) {
            return userName != null && userName.equals(handler.userName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return userName != null ? userName.hashCode() : 0;
    }
}
