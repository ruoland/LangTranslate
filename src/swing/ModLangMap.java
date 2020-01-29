package swing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

public class ModLangMap {
    private static HashMap<String, ModLang> modLangMap = new HashMap<>();

    public static void put(String key, ModLang modLang){
        modLangMap.put(key, modLang);
    }

    public static ModLang get(String key){
        return modLangMap.get(key);
    }

    public static Set<String> getFileList(){
        return modLangMap.keySet();
    }
    public static ModLang createModLang(File file) throws IOException {
        ModLang modLang = new ModLang(file);;
        put(file.getName(), modLang);
        put(modLang.getModID(), modLang);
        createLangProperties(modLang.getModID());
        return modLang;
    }

    private static void createLangProperties(String modid) {
        try {
            Path path = Files.createDirectories(Paths.get("./assets/" + modid + "/lang"));
            File langFile = new File(path.toString(), "ko_KR.lang");
            langFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
