import java.io.*;

public class TranslateProperties extends OriginalProperties {
    private File filePath;
    private String fileName;
    public TranslateProperties(String fileName){
        super(new File("./mod/"+fileName));
        filePath = new File("./"+fileName+"/lang/ko_KR.lang");
        this.fileName = fileName;
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store()  {
        try {
            if(Main.addOriginal){
                for(Object obj : keySet()){
                    String key = (String) obj;
                    String translateValue = (String) get(key);
                    if(!translateValue.contains((CharSequence) Main.originalProprerties.get(fileName).get(key))) {//원본이 번역되어 있는지
                        put(key, get(key) + " (" + Main.originalProprerties.get(fileName).get(key) + ")");
                    }
                }
            }
            super.store(new FileWriter(filePath), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        LangKey.valueKeyTranslateMap.put((String)value, (String)key);
        return super.put(key, value);

    }

    public synchronized void load() throws IOException {
        super.load(new FileReader(filePath));
        for(Object obj : keySet()){
            LangKey.valueKeyTranslateMap.put((String)get(obj), (String)obj);
        }
    }
}
