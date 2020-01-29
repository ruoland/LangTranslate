package swing;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class ModJar {
    private String modID;
    private File jarFile;

    ModJar(File jarFile) throws IOException {
        this.jarFile = jarFile;
        modID = getModID();

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
                return (stream);
            }
        }
        throw new IOException();
    }

    /**
     * 번역한 파일이 있는지 찾음
     */
    public InputStream findTranslateLangFile(String language) throws IOException {
        System.out.println(modID);
        language = language+".lang";

        File langFile = new File("./assets/" + modID + "/lang/" + language);
        if (langFile.isFile())
            return new FileInputStream(langFile);
        else
            return findModLangFile("en_US");
    }


    public String getModID() throws IOException {
        JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile));
        JarFile j = new JarFile(jarFile);
        ZipEntry zipEntry;
        while ((zipEntry = jarInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().contains("mcmod.info")) {
                InputStreamReader stream = new InputStreamReader(j.getInputStream(zipEntry));
                JsonElement element = JsonParser.parseReader(stream);
                return modID = (element.getAsJsonArray().get(0).getAsJsonObject().get("modid").getAsString());
            }
        }
        throw new IOException();
    }
}
