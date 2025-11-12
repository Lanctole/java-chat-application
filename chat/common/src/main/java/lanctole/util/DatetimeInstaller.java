package lanctole.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatetimeInstaller {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String install() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return "[" + timestamp + "] ";
    }
}
