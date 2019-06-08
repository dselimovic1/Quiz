package ba.unsa.etf.rma.klase;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ba.unsa.etf.rma.converteri.JSONRangListConverter;
import ba.unsa.etf.rma.interfejsi.FirestoreStorable;


public class Rang implements FirestoreStorable, Serializable {

    private String imeKviza;
    private HashMap<Integer, Par> mapa = new HashMap<>();
    private String documentID;
    private int ID;


    private Rang() {}

    public Rang(String imeKviza) {
        this.imeKviza = imeKviza;
    }


    public String getImeKviza() {
        return imeKviza;
    }

    public void setImeKviza(String imeKviza) {
        this.imeKviza = imeKviza;
    }

    public HashMap<Integer, Par> getMapa() {
        return mapa;
    }


    public void dodajRezultat(Par par) {
        int pozicija = dajPoziciju(par);
        azurirajMapu(pozicija);
        mapa.put(pozicija, par);
    }

    private int dajPoziciju(Par par) {
        int pozicija = mapa.size();
        for(Par p : mapa.values()) {
            if(par.procenatTacnih > p.procenatTacnih) pozicija--;
        }
        return pozicija;
    }

    private void azurirajMapu(int pozicija) {
        for (int i = mapa.size() - 1; i >= pozicija; i--)
            mapa.put(i + 1, mapa.get(i));
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "";
        json += "{\"fields\": {\"nazivKviza\": {\"stringValue\": \"" + imeKviza + "\"}," +
                "\"lista\": {\"mapValue\": {\"fields\": {";
        for(int i = 0; i < mapa.size(); i++) {
            String proc = String.format("%.2f", mapa.get(i).procenatTacnih);
            json += "\"" + (i + 1) + "\": {\"mapValue\": {\"fields\": {" +
                    "\"" + mapa.get(i).imeIgraca + "\": {\"stringValue\": \"" + proc + "\"}}}}";
            if(i != mapa.size() - 1) json += ",";
        }
        json += "}}}}}";
        return json;
    }

    public static Rang convertFromJSON(JSONObject json) {
        Rang rang = new Rang();
        try {
            JSONObject fields = json.getJSONObject("fields");
            rang.setDocumentID(JSONRangListConverter.findID(json.getString("name")));
            rang.setImeKviza(JSONRangListConverter.setName(fields.getJSONObject("nazivKviza")));
            ArrayList<Par> pairs = JSONRangListConverter.setPairs(fields.getJSONObject("lista").getJSONObject("mapValue").getJSONObject("fields"));
            rang.setHashMap(pairs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rang;
    }

    private void setHashMap(ArrayList<Par> pairs) {
        Iterator<Par> iterator = pairs.listIterator();
        while(iterator.hasNext()) dodajRezultat(iterator.next());
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public static class RangEntry {
        private RangEntry() {}

        public static final String TABLE_NAME = "Rang";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "naziv_kviza";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                "FOREIGN KEY (" + COLUMN_NAME + ") REFERENCES " + Kviz.KvizEntry.TABLE_NAME +
                "(" + Kviz.KvizEntry.COLUMN_NAME + ") ON DELETE CASCADE);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME};
    }

    public static class ParEntry {
        private ParEntry() {}

        public static final String TABLE_NAME = "Par";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PLAYER_NAME = "ime_igraca";
        public static final String COLUMN_RESULT = "rezultat";
        public static final String COLUMN_RANG_ID = "id_rangliste";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PLAYER_NAME + " TEXT NOT NULL," +
                COLUMN_RESULT + " REAL NOT NULL," +
                COLUMN_RANG_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + COLUMN_RANG_ID + ") REFERENCES " + RangEntry.TABLE_NAME +
                "(" + RangEntry.COLUMN_ID + ") ON DELETE CASCADE);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_PLAYER_NAME, COLUMN_RESULT, COLUMN_RANG_ID};
    }

    public static class Par implements Comparable<Par>{
        public String imeIgraca;
        public double procenatTacnih;
        public int ID;

        public Par(String imeIgraca, double procenatTacnih) {
            this.imeIgraca = imeIgraca;
            this.procenatTacnih = procenatTacnih;
        }

        @Override
        public int compareTo(@NonNull Par par) {
            if(procenatTacnih < par.procenatTacnih) return 1;
            else if(procenatTacnih > par.procenatTacnih) return -1;
            return 0;
        }
    }

}
