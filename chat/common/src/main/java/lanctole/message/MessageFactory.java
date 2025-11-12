package lanctole.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lanctole.util.DatetimeInstaller;

import java.io.IOException;
import java.util.Set;

public class MessageFactory {

    public static Message createMessage(MessageType type, String content, String sender) {
        return Message.builder()
                .datetime(DatetimeInstaller.install())
                .sender(sender)
                .content(content)
                .type(type)
                .build();
    }

    public static Message createSetupNicknameMessage(String name, MessageType type) {
        return Message.builder()
                .sender(name)
                .type(type)
                .build();
    }

    public static Message createPingMessage() {
        return Message.builder()
                .type(MessageType.PING)
                .build();
    }

    public static Message createNicknameDeniedMessage(String content) {
        return Message.builder()
                .datetime(DatetimeInstaller.install())
                .content(content)
                .type(MessageType.NICKNAME_DENIED)
                .build();
    }

    public static Message createSystemMessage(String content) {
        return Message.builder()
                .datetime(DatetimeInstaller.install())
                .content(content)
                .type(MessageType.SYSTEM_NOTIFICATION)
                .build();
    }

    public static Message createOldNameMessage() {
        return Message.builder()
                .type(MessageType.OLD_NAME)
                .build();
    }

    public static Message createNicknameAcceptedMessage() {
        return Message.builder()
                .type(MessageType.NICKNAME_ACCEPTED)
                .build();
    }

    public static Message createMembersListMessage(Set<String> userNames) {
        try {
            return Message.builder()
                    .datetime(DatetimeInstaller.install())
                    .content(new ObjectMapper().writeValueAsString(userNames))
                    .type(MessageType.REFRESH_CHAT_MEMBERS_LIST)
                    .build();
        } catch (IOException e) {
            return Message.builder()
                    .datetime(DatetimeInstaller.install())
                    .content("")
                    .type(MessageType.REFRESH_CHAT_MEMBERS_LIST)
                    .build();
        }
    }
}
