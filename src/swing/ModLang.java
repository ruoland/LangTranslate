package swing;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class ModLang {
    private String modID;
    private Properties enProperties = new Properties();
    private Properties koProperties = new Properties();
    public ModLang(File file) throws IOException {
        ModJar modJar = new ModJar(file);

        enProperties.load(modJar.findModLangFile("en_US"));
        koProperties.load(new InputStreamReader(modJar.findTranslateLangFile("ko_KR")));
        modID = modJar.getModID();

    }

    public File getKoreaLangFile() throws IOException {
        return new File("./번역/assets/"+modID+"/lang/ko_kr.lang");
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
        koProperties.store(new OutputStreamWriter(new FileOutputStream(getKoreaLangFile()), Charset.forName("UTF-8")), "");
        List<String> koLangFileList =Files.readAllLines(getKoreaLangFile().toPath());
        koLangFileList.remove(0);
        koLangFileList.remove(0);
        Files.write(getKoreaLangFile().toPath(), koLangFileList, Charset.forName("UTF-8"));

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
        System.out.println(enProperties);
        System.out.println(koProperties);
        return !enProperties.getProperty(key).equalsIgnoreCase(koProperties.getProperty(key, enProperties.getProperty(key)));
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

