package swing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


//마인크래프트 Lang 파일을 가져오는 클래스
public class MinecraftJar {
    private Properties enProperties, koProperties;
    String mcFolder = (System.getenv("APPDATA") + "/.minecraft");
    File indexesFolder = new File(mcFolder + "/assets/indexes");
    File objectsFolder = new File(mcFolder + "/assets/objects");
    File jsonFile = indexesFolder.listFiles()[indexesFolder.listFiles().length - 1];

    public void loadMC() {
        setKoProperties();
    }
    public void setKoProperties() {

        try {
            JsonElement jsonParser = JsonParser.parseReader(new FileReader(jsonFile));
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
    }
}
