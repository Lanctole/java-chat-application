package lanctole.client;

import lanctole.events.ConnectionListener;
import lanctole.message.Message;

import java.util.function.Consumer;

public interface ChatClient {
    void connect(String host, int port);
    void disconnect();
    void setName(String name);
    void sendMessage(Message message);
    void addMessageListener(Consumer<Message> listener);
    void createTextMessage(String message);
    void addConnectionListener(ConnectionListener listener);
}
