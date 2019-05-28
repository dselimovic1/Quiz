package ba.unsa.etf.rma.helperi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import ba.unsa.etf.rma.klase.Rang;

public class JSONRangListConverter  {


    public static String findID(String name) {
        String[] atr = name.split("/");
        return atr[atr.length - 1];
    }

    public static String setName(JSONObject json) throws JSONException {
        return json.getString("stringValue");
    }

    public static ArrayList<Rang.Par> setPairs (JSONObject json) throws JSONException {
        ArrayList<Rang.Par> pairs = new ArrayList<>();
        Iterator<String> iterator = json.keys();
        while(iterator.hasNext()) {
            pairs.add(setPair(json.getJSONObject(iterator.next())));
        }
        return pairs;
    }

    private static Rang.Par setPair(JSONObject json) throws JSONException {
        JSONObject player = json.getJSONObject("mapValue");
        String name = getName(player);
        JSONObject result = player.getJSONObject("fields");
        double proc = getResult(result);
        return new Rang.Par(name, proc);
    }

    private static String getName(JSONObject player) {
        Iterator<String> iterator = player.keys();
        String name = "";
        while(iterator.hasNext()) name = iterator.next();
        return name;
    }

    private static double getResult(JSONObject result) throws JSONException {
        return Double.parseDouble(result.getString("stringValue"));
    }
}
