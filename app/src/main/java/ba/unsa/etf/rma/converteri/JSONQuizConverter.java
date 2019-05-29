package ba.unsa.etf.rma.converteri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;

public class JSONQuizConverter {

    public static String findID(String name) {
        String[] atr = name.split("/");
        return atr[atr.length - 1];
    }

    public static String findName(JSONObject json) throws JSONException {
        return json.getString("stringValue");
    }

    public static ArrayList<Pitanje> getQuestions(JSONArray array) throws JSONException {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Pitanje p = new Pitanje();
            p.setDocumentID(findName(array.getJSONObject(i)));
            pitanja.add(p);
        }
        return pitanja;
    }

    public static Kategorija getCategory(JSONObject json) throws JSONException {
        String ID = findName(json);
        Kategorija k = new Kategorija();
        k.setDocumentID(ID);
        return k;
    }
}
