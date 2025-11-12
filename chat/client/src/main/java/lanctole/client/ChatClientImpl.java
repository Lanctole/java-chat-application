package lanctole.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.events.ConnectionListener;
import lanctole.exception.ConnectionFailedException;
import lanctole.message.Message;
import lanctole.message.MessageFactory;
import lanctole.message.MessageType;
import lanctole.network.ClientSession;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ChatClientImpl implements ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatClientImpl.class);

    @Getter
    private String name;
    private final List<ConnectionListener> connectionListeners = new ArrayList<>();
    private int port;
    private String host;
    private ScheduledExecutorService pingScheduler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Consumer<Message>> listeners = new ArrayList<>();
    private ClientSession clientSession;

    private int pingFailureCount = 0;
    private final int maxPingFailures = 10;
    private boolean wasPreviouslyConnected = true;

    @Override
    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            Socket socket = new Socket(host, port);
            clientSession = new ClientSession(socket);
            new Thread(this::listen).start();

            pingScheduler = Executors.newSingleThreadScheduledExecutor();
            pingScheduler.scheduleAtFixedRate(this::sendPing, 0, 5, TimeUnit.SECONDS);
            logger.info("Successfully connected to server {}:{}", host, port);
        } catch (IOException | IllegalArgumentException | SecurityException e) {
            throw new ConnectionFailedException("Cannot connect to server: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        cleanup();
    }

    @Override
    public void sendMessage(Message message) {
        try {
            clientSession.sendLine(objectMapper.writeValueAsString(message));
        } catch (IOException e) {
            throw new ConnectionFailedException(e.getMessage());
        }
    }

    @Override
    public void createTextMessage(String text) {
        Message msg = MessageFactory.createMessage(MessageType.MESSAGE, text, name);
        sendMessage(msg);
    }

    @Override
    public void addMessageListener(Consumer<Message> listener) {
        listeners.add(listener);
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        Message msg = MessageFactory.createSetupNicknameMessage(name, MessageType.SET_NAME);
        sendMessage(msg);
    }

    private void cleanup() {
        if (pingScheduler != null && !pingScheduler.isShutdown()) {
            pingScheduler.shutdownNow();
            logger.info("Ping scheduler shutdown.");
        }
        if (clientSession != null && !clientSession.isClosed()) {
            try {
                clientSession.close();
                logger.info("Socket closed successfully.");
            } catch (IOException e) {
                logger.warn("Error while closing socket: {}", e.getMessage());
            }
        }
    }

    private void listen() {
        String line;
        try {
            while ((line = clientSession.readLine()) != null) {
                Message message = objectMapper.readValue(line, Message.class);
                listeners.forEach(listener -> listener.accept(message));
            }
        } catch (IOException e) {
            logger.warn("Connection lost while reading messages: {}", e.getMessage());
        }
    }

    private void sendPing() {
        try {
            Message pingMessage = MessageFactory.createPingMessage();
            sendMessage(pingMessage);
            pingFailureCount = 0;
        } catch (ConnectionFailedException e) {
            logger.warn("Failed to send ping: {}", e.getMessage());
            handleConnectionFailure();
        }
    }

    private void handleConnectionFailure() {
        pingFailureCount++;

        if (wasPreviouslyConnected) {
            wasPreviouslyConnected = false;
            notifyConnectionLost();
        }
        
        if (pingFailureCount < maxPingFailures) {
            logger.info("Attempting to reconnect... attempt {}", pingFailureCount);

            new Thread(() -> {
                try {
                    int backoffTime = 50 * (int) Math.pow(2, pingFailureCount);
                    Thread.sleep(backoffTime);

                    reconnect();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Reconnection thread was interrupted", e);
                }
            }).start();

        } else {
            notifyConnectionFinallyLost();
            logger.error("Maximum number of reconnection attempts exceeded.");
        }
    }

    private void reconnect() {
        try {
            cleanup();
            connect(host, port);
            if (name != null) {
                Message msg = MessageFactory.createSetupNicknameMessage(name, MessageType.OLD_NAME);
                sendMessage(msg);
            }
            logger.info("Reconnection successful.");
            if (!wasPreviouslyConnected) {
                wasPreviouslyConnected = true;
                notifyConnectionRestored();
            }
        } catch (ConnectionFailedException e) {
            logger.warn("Reconnection attempt failed: {}", e.getMessage());
            handleConnectionFailure();
        }
    }

    private void notifyConnectionFinallyLost() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionFinallyLost();
        }
    }

    private void notifyConnectionLost() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionLost();
        }
    }

    private void notifyConnectionRestored(){
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionRestored();
        }
    }
}