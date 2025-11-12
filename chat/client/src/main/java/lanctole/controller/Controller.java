package lanctole.controller;

import lanctole.client.ChatClient;

public interface Controller {
    void setNewName(String name);
    void sendMessage(String message);
    ChatClient getChatClient();
    void connect(String host, int port);
    void disconnect();
}