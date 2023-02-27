package api.playercontroller.utils;

import static java.util.Objects.isNull;

public class EnvProperties {

    private static final String PROPERTIES_FILE_NAME = "env.properties";
    private static final String BASE_URL = "baseURL";

    public static String getBaseURL() {
        String systemProperty = System.getProperty(BASE_URL);
        return !isNull(systemProperty) ? systemProperty : PropertiesReader.getProperty(PROPERTIES_FILE_NAME, BASE_URL);
    }
}
