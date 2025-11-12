package lanctole.manager;

import lombok.extern.slf4j.Slf4j;
import lanctole.handler.SessionHandler;
import lanctole.message.Message;
import lanctole.service.JsonService;
import lanctole.service.MessageSender;

@Slf4j
public class MessageDispatcher {
    private final SessionManager sessionManager;
    private final JsonService jsonService;

    public MessageDispatcher(SessionManager sessionManager, JsonService jsonService) {
        this.sessionManager = sessionManager;
        this.jsonService = jsonService;
    }

    public void broadcast(Message message) {
        for (SessionHandler client : sessionManager.getClients()) {
            log.debug("notify client {}", client.getUserName());
            new MessageSender(client.getSession(), jsonService).send(message);
        }
    }
}
