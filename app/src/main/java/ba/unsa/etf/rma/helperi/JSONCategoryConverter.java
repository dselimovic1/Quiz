package ba.unsa.etf.rma.helperi;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONCategoryConverter {
    public static String findID(String name) {
        String[] atr = name.split("/");
        return atr[atr.length - 1];
    }

    public static String findCategoryID(JSONObject json) throws JSONException {
        return json.getString("stringValue");
    }

    public static String findCategoryName(JSONObject object) throws JSONException {
        return object.getString("stringValue");
    }
}
