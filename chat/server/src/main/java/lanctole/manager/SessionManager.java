package lanctole.manager;

import lanctole.handler.SessionHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionManager {
    private final Set<SessionHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public void registerClient(SessionHandler client) {
        clients.add(client);
    }

    public void unregisterClient(SessionHandler client) {
        if (client != null) {
            clients.remove(client);
        }
    }

    public Set<SessionHandler> getClients() {
        return Set.copyOf(clients);
    }

    public boolean isUserNameTaken(String name) {
        return clients.stream().anyMatch(c -> c.getUserName().equals(name));
    }

    public Set<String> getUserNames() {
        return clients.stream().map(SessionHandler::getUserName).collect(Collectors.toSet());
    }
}
