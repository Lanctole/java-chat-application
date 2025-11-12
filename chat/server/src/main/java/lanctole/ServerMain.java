package lanctole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lanctole.exception.ConfigurationLoadException;
import lanctole.handler.SessionHandler;
import lanctole.manager.SessionManager;
import lanctole.network.ClientSession;
import lanctole.service.JsonService;
import lanctole.util.PropertiesLoader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final Logger log = LoggerFactory.getLogger(ServerMain.class);
    private static final int THREAD_POOL_SIZE = 100;

    private final ExecutorService clientPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final SessionManager sessionManager = new SessionManager();
    private final JsonService jsonService = new JsonService();

    public static void main(String[] args) {
        new ServerMain().run();
    }

    public void run() {
        int port = loadPort();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server started on port {}", port);
            acceptClients(serverSocket);
        } catch (IOException e) {
            log.error("Failed to start the server", e);
        } finally {
            shutdown();
        }
    }

    private int loadPort() {
        try {
            return PropertiesLoader.getPort();
        } catch (ConfigurationLoadException e) {
            log.error("Critical error: failed to load configuration", e);
            System.exit(1);
            return -1;
        }
    }

    private void acceptClients(ServerSocket serverSocket) throws IOException {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.debug("Accepted connection from {}", clientSocket.getRemoteSocketAddress());
                clientPool.execute(new SessionHandler(new ClientSession(clientSocket), sessionManager, jsonService));
            } catch (IOException e) {
                log.error("Error accepting client connection", e);
            }
        }
    }

    private void shutdown() {
        clientPool.shutdown();
        log.info("Thread pool shut down");
    }
}