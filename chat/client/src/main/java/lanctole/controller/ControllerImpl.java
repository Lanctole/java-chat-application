package lanctole.controller;

import lanctole.client.ChatClient;

public class ControllerImpl implements Controller {
    private final ChatClient client;

    public ControllerImpl(ChatClient client) {
        this.client = client;
    }

    @Override
    public void setNewName(String name) {
        client.setName(name);
    }

    @Override
    public void sendMessage(String message) {
        client.createTextMessage(message);
    }

    @Override
    public ChatClient getChatClient() {
        return client;
    }

    @Override
    public void connect(String serverIp, int serverPort) {
        client.connect(serverIp, serverPort);
    }

    @Override
    public void disconnect() {
        client.disconnect();
    }
}