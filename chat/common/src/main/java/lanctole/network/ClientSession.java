package lanctole.network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ClientSession {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public void sendLine(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public void close() throws IOException {
        socket.close();
    }

    public void setTimeout(int timeoutMillis) throws SocketException {
        socket.setSoTimeout(timeoutMillis);
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
