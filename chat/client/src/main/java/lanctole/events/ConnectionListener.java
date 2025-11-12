package lanctole.events;

public interface ConnectionListener {
    void onConnectionFinallyLost();
    void onConnectionLost();
    void onConnectionRestored();
}
