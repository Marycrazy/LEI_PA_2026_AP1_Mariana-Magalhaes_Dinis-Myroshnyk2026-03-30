package main;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

//https://www.devmedia.com.br/utilizando-arquivos-de-propriedades-no-java/25546
//https://medium.com/@JavaFusion/what-is-a-properties-file-in-java-e955c3adc92f
// https://www.youtube.com/watch?v=jRigxelb43E
// https://docs.oracle.com/javase/tutorial/essential/environment/properties.html
public class PropertiesManager {
    private static String file = "./db/config.properties";
    private static Properties props;

    public PropertiesManager() {
        props = new Properties();
        try(FileInputStream files = new FileInputStream(file)){
            props.load(files);
        } catch (IOException e) {
            return;
        }
    }

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

    public String getProperty(String key){
        return props.getProperty(key);
    }

    public void setProperty(String key, String value){
        props.setProperty(key, value);
    }

    public boolean hasProperties(){
        return getProperty("connect") != null && getProperty("username") != null && getProperty("password") != null && getProperty("namespace") != null && getProperty("database") != null;
    }
}
