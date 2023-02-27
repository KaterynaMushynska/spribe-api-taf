package api.playercontroller.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static final String PROPERTIES_FILE_LOCATION = System.getProperty("os.name")
            .toLowerCase().contains("windows") ? "src/main/resources/" : "src\\main\\resources\\";

    public static String getProperty(String fileName, String propertyName) {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE_LOCATION + fileName)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop.getProperty(propertyName);
    }
}
