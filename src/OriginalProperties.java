import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class OriginalProperties extends Properties {
    private File jarFile;
    public OriginalProperties(File file) {
        this.jarFile = file;
        fileLoad();
    }

    private void fileLoad() {
        try {
            JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile));
            JarFile j = new JarFile(jarFile);
            ZipEntry zipEntry;
            while ((zipEntry = jarInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith("en_US.lang") || zipEntry.getName().endsWith("en_us.lang")) {
                    InputStream stream = new JarFile(jarFile).getInputStream(zipEntry);
                    load(stream);
                    stream.close();
                    break;
                }
            }
            jarInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Object obj : keySet()){
            LangKey.valueKeyOriginalMap.put((String)get(obj), (String)obj);
        }
    }
}
