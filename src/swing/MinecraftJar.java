package swing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;


//마인크래프트 Lang 파일을 가져오는 클래스
public class MinecraftJar {
    private Properties enProperties, koProperties;
    String mcFolder = (System.getenv("APPDATA") + "/.minecraft");
    File indexesFolder = new File(mcFolder + "/assets/indexes");
    File objectsFolder = new File(mcFolder + "/assets/objects");
    File jsonFile = indexesFolder.listFiles()[indexesFolder.listFiles().length - 1];

    public void loadMCProperties() {

        try {
            JsonElement jsonParser = new JsonParser().parse(new FileReader(jsonFile));
            JsonObject object = jsonParser.getAsJsonObject().getAsJsonObject().getAsJsonObject("objects").getAsJsonObject("minecraft/lang/ko_KR.json");
            if(object == null)
                object = jsonParser.getAsJsonObject().getAsJsonObject().getAsJsonObject("objects").getAsJsonObject("minecraft/lang/ko_kr.json");
            String hash = object.getAsJsonPrimitive("hash").getAsString();
            File langFile = new File(objectsFolder, hash.substring(0, 2) + "/" + hash);
            Properties properties = new Properties();
            properties.load(new FileReader(langFile));
            ArrayList<String> removeList = new ArrayList<>();
            for (Object obj : properties.keySet()) {
                String key = ((String) obj);
                if (key.length() > 1)
                    key = key.substring(1, key.length() - 1);

                if (!key.startsWith("item") && !key.startsWith("block"))
                    removeList.add(key);
            }
            for (String key : removeList) {
                properties.remove("\"" + key + "\"");
            }
            koProperties = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(koProperties);
        System.out.println(enProperties);
    }

    /**
     * 모드 안에 있는 랭 파일을 찾음
     */
    public InputStream findModLangFile(String language) throws IOException {
        language = language+".lang";
        JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile));
        JarFile j = new JarFile(jarFile);
        ZipEntry zipEntry;
        while ((zipEntry = jarInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().endsWith(language) || zipEntry.getName().endsWith(language.toLowerCase())) {
                InputStream stream = new JarFile(jarFile).getInputStream(zipEntry);
                modID = zipEntry.getName().substring(7, zipEntry.getName().indexOf("/lang/"));
                return (stream);
            }
        }
        throw new IOException();
    }

    public String translateMCText(String argText){
        for (Object obj : koProperties.keySet()) {
            String key = (String) obj;
            if (enProperties.get(key) == null)
                continue;

            argText = (String) argText.replace((String)enProperties.get(key), (String)koProperties.get(key));
        }
        return argText;
    }
    public Properties getEnProperties() {
        return enProperties;
    }

    public Properties getKoProperties() {
        return koProperties;
    }
}
