package main;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handles persistent configuration properties for the application, such as database
 * connection credentials and settings stored in an external file.
 */
public class PropertiesManager {
    /** Relative path to the configuration properties file. */
    private static String file = "./db/config.properties";
    /** Holds the key-value configuration pairs. */
    private static Properties props;

    /**
     * Constructs a new PropertiesManager and attempts to load configuration
     * settings from the default properties file path.
     */
    public PropertiesManager() {
        props = new Properties();
        try(FileInputStream files = new FileInputStream(file)){
            props.load(files);
        } catch (IOException e) {
            return;
        }
    }

    /**
     * Saves the current internal properties state back to the configuration file on disk.
     *
     * @return {@code true} if the file was saved successfully; {@code false} if an I/O error occurred
     */
    public boolean saveFile(){
        try(FileOutputStream files = new FileOutputStream(file)){
            props.store(files, "Configuration properties");
            System.out.println("Properties saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving properties file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the configuration value associated with the specified key string.
     *
     * @param key the property key name to look up
     * @return the string value matching the key, or {@code null} if the key does not exist
     */
    public String getProperty(String key){
        return props.getProperty(key);
    }

    /**
     * Sets or updates a configuration property key to a new specified string value.
     *
     * @param key   the property key name to set
     * @param value the property value to assign to the key
     */
    public void setProperty(String key, String value){
        props.setProperty(key, value);
    }

    /**
     * Validates whether all mandatory database connection properties are populated
     * within the loaded configuration.
     *
     * @return {@code true} if all essential keys (connect, username, password, namespace, database) are present; {@code false} otherwise
     */
    public boolean hasProperties(){
        return getProperty("connect") != null && getProperty("username") != null && getProperty("password") != null && getProperty("namespace") != null && getProperty("database") != null;
    }
}