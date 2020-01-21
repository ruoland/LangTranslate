
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class PapagoAPI {
    public static String source = "en", target = "ko";
    static String clientId = null, clientSecret = null;

    public static String main(JFrame jFrame, String argText){
        try {
            String text = URLEncoder.encode(argText, "UTF-8");
            HttpURLConnection con = (HttpURLConnection)new URL("https://openapi.naver.com/v1/papago/n2mt").openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source=" + source + "&target=" + target + "&text="+argText;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();

            if (responseCode != 200) {
                JOptionPane.showConfirmDialog(jFrame, jsonObject.get("errorMessage").getAsString(), "오류 발생 에러코드:" + jsonObject.get("errorCode").getAsInt(), JOptionPane.OK_CANCEL_OPTION);
                return null;
            }

            br.close();
            return  jsonObject.get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return argText;
    }
    private static final Pattern ENG_PATTERN = Pattern.compile("^[0-9ㄱ-ㅎ가-힣`~§!@#$%^&*()-=_+\\\\[\\\\]{}:;',./<>?\\\\\\\\|]*$");
    public static boolean isEng(String text){
        return !ENG_PATTERN.matcher(text.replaceAll(" ","")).matches();
    }

}