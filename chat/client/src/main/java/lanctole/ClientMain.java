package lanctole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.client.ChatClient;
import lanctole.client.ChatClientImpl;
import lanctole.controller.Controller;
import lanctole.controller.ControllerImpl;
import lanctole.exception.ConfigurationLoadException;
import lanctole.messagehandler.MessageHandler;
import lanctole.startup.ClientStartupFlow;
import lanctole.util.PropertiesLoader;

public class ClientMain {
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) {
        ChatClient client = new ChatClientImpl();
        MessageHandler handler = new MessageHandler();
        Controller controller = new ControllerImpl(client);

        client.addMessageListener(handler::handleIncomingMessage);
        ClientStartupFlow flow = new ClientStartupFlow(controller, handler);

        try {
            int port = PropertiesLoader.getPort();
            String host = PropertiesLoader.getHost();
            flow.start(port, host);
        } catch (ConfigurationLoadException e) {
            logger.error("Critical error: failed to load configuration", e);
            System.exit(1);
        }
    }
}