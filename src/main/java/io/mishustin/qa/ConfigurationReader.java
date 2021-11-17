package io.mishustin.qa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigurationReader {

    private static final Logger LOG = LogManager.getLogger(ConfigurationReader.class);

    private ConfigurationReader() {
    }

    public static Properties readPropertiesFile(Path configFilePath) {
        LOG.info("Read configuration file {}", configFilePath);

        try (InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(configFilePath.toString())) {

            if (resourceAsStream == null) {
                throw new FileNotFoundException("File " + configFilePath + " not found");
            }

            Properties properties = new Properties();
            properties.load(resourceAsStream);
            return properties;
        } catch (IOException e) {
            throw new ToolboxException(e);
        }
    }
}