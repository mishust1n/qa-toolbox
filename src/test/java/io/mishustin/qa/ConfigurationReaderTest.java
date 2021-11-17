package io.mishustin.qa;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Properties;

import static org.testng.Assert.*;

public class ConfigurationReaderTest {

    @Test(expectedExceptions = ToolboxException.class)
    public void readPropertiesFromSystemShouldThrowException() {
        ConfigurationReader.readPropertiesFile(Path.of("invalid-file"));
    }

    @Test
    public void readPropertyShouldReadProperty() {
        Properties properties = ConfigurationReader.readPropertiesFile(Path.of("test.properties"));

        assertEquals(properties.getProperty("key"), "value");
    }

}
