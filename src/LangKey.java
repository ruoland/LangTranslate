import java.util.HashMap;

public class LangKey {
    public static HashMap<String, String> valueKeyOriginalMap = new HashMap<>();
    public static HashMap<String, String> valueKeyTranslateMap = new HashMap<>();

    public static String getKey(String value){
        if(valueKeyOriginalMap.containsKey(value))
            return valueKeyOriginalMap.get(value);
        else
            return valueKeyTranslateMap.get(value);
    }
}
