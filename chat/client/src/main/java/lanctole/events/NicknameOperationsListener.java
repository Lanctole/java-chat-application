package lanctole.events;

import lanctole.message.Message;

public interface NicknameOperationsListener {
    void onNicknameDenied(Message message);
    void onNicknameAccepted(Message message);
}
