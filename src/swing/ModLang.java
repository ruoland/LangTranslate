package swing;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

public class ModLang {
    private String modID;
    private Properties enProperties = new Properties();
    private Properties koProperties = new Properties();
    public ModLang(File file) throws IOException {
        ModJar modJar = new ModJar(file);
        System.out.println(file) ;
        enProperties.load(modJar.findModLangFile("en_US"));
        koProperties.load(modJar.findTranslateLangFile("ko_KR"));
        modID = modJar.getModID();

    }

    public File getKoreaLangFile() throws IOException {
        return new File("./assets/"+modID+"/lang/ko_KR.lang");
    }

    public String getModID() {
        return modID;
    }

    public Properties getEnProperties() {
        return enProperties;
    }

    public Properties getKoProperties() {
        return koProperties;
    }

    public void propertiesSave() throws IOException{
        koProperties.store(new FileWriter(getKoreaLangFile()), "");
    }
    public String findValueKey(String value){
        for(Object object : enProperties.keySet())
        {
            String key = (String) object;
            if(getEnProperties().getProperty(key).equalsIgnoreCase(value)){
                return (String) object;
            }
        }
        return null;
    }
    public boolean isTranslate(String key){
        return enProperties.getProperty(key).equalsIgnoreCase(koProperties.getProperty(key, enProperties.getProperty(key)));
    }

    public Vector<String> getEngLang(){
        Vector<String> langList = new Vector<>();

        for(Object object : getEnProperties().keySet()){
            String key = (String) object;
            langList.add(key+"="+getEnProperties().getProperty(key));
        }
        return null;
    }
}

