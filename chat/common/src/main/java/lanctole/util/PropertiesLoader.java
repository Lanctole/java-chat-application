package lanctole.util;

import lanctole.exception.ConfigurationLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String PROPERTY_PORT = "server.port";
    private static final String PROPERTY_HOST = "server.host";

    public static int getPort() {
        try {
            Properties properties = loadProperties();
            return Integer.parseInt(properties.getProperty(PROPERTY_PORT));
        } catch (IOException | NumberFormatException e) {
            throw new ConfigurationLoadException("Failed to load server port from configuration.", e);
        }
    }

    public static String getHost() {
        try {
            Properties properties = loadProperties();
            String host = properties.getProperty(PROPERTY_HOST);
            if (host == null || host.isBlank()) {
                throw new ConfigurationLoadException("Host property is missing or empty.", null);
            }
            return host;
        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to load server host from configuration.", e);
        }
    }

    private static Properties loadProperties() throws IOException {
        try (InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(APPLICATION_PROPERTIES)) {

            if (inputStream == null) {
                throw new IOException("File " + APPLICATION_PROPERTIES + " not found in classpath.");
            }

            Properties configuration = new Properties();
            configuration.load(inputStream);
            return configuration;
        }
    }
}
