package swing;

import ruo.io.FileUtil;

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
        modLang.setResourceCreate( FileUtil.createResourcePack("번역 리소스팩", 3, " 번역 리소스팩입니다") );
        modLang.propertiesSave();
        return modLang;
    }

    private static void createLangProperties(String modid) {
        try {
            Path path = Files.createDirectories(Paths.get("./번역/assets/" + modid + "/lang"));
            File langFile = new File(path.toString(), "ko_kr.lang");
            langFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
