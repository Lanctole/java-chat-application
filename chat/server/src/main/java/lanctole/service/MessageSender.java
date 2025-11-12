package lanctole.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.message.Message;
import lanctole.network.ClientSession;

import java.io.IOException;

public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private final ClientSession session;
    private final JsonService jsonService;

    public MessageSender(ClientSession session, JsonService jsonService) {
        this.session = session;
        this.jsonService = jsonService;
    }

    public void send(Message message) {
        try {
            session.sendLine(jsonService.serialize(message));
        } catch (IOException e) {
            logger.warn("Error sending message to client: {}", e.getMessage());
            try {
                session.close();
            } catch (IOException ex) {
                logger.warn("Error during closing: {}", e.getMessage());
            }
        }
    }
}
