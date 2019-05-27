package ba.unsa.etf.rma.helperi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONQuestionConverter {

    public static String findID(String name) {
        String[] atr = name.split("/");
        return atr[atr.length - 1];
    }

    public static String findName(JSONObject json) throws JSONException {
        return json.getString("stringValue");
    }

    public static int findIndex(JSONObject json) throws JSONException {
        return json.getInt("integerValue");
    }

    public static ArrayList<String> getAnswers(JSONArray json) throws JSONException{
        ArrayList<String> answer = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            answer.add(findName(json.getJSONObject(i)));
        }
        return answer;
    }
}
