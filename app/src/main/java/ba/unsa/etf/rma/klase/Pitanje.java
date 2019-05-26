package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ba.unsa.etf.rma.interfejsi.Loadable;

public class Pitanje implements Parcelable, Loadable {

    private String naziv;
    private String tekstPitanja;
    private ArrayList<String> odgovori = new ArrayList<>();
    private String tacan;
    private String documentID;

    public Pitanje() {
    }

    public Pitanje(String naziv, String tekstPitanja, ArrayList<String> odgovori, String tacan) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.tacan = tacan;
    }

    protected Pitanje(Parcel parcel) {
        naziv = parcel.readString();
        tekstPitanja = parcel.readString();
        odgovori = parcel.createStringArrayList();
        tacan = parcel.readString();
        documentID = parcel.readString();
    }

    public static final Creator<Pitanje> CREATOR = new Creator<Pitanje>() {
        @Override
        public Pitanje createFromParcel(Parcel parcel) {
            return new Pitanje(parcel);
        }

        @Override
        public Pitanje[] newArray(int i) {
            return new Pitanje[i];
        }
    };

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public String getTacan() {
        return tacan;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public ArrayList<String> dajRandomOdgovore() {
        ArrayList<String> randomOdgovori = new ArrayList<>(odgovori);
        Collections.shuffle(randomOdgovori);
        return randomOdgovori;
    }

    public void dodajOdgovor(String odgovor) {
        odgovori.add(odgovor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pitanje pitanje = (Pitanje) o;
        return Objects.equals(naziv, pitanje.naziv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeString(tekstPitanja);
        parcel.writeStringList(odgovori);
        parcel.writeString(tacan);
        parcel.writeString(documentID);
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "";
        int index = getOdgovori().indexOf(getTacan());
        json += "{\"fields\": {\"naziv\": {\"stringValue\": \"" + getNaziv() + "\"}," +
                "\"odgovori\": {\"arrayValue\": {\"values\": [";
        for(int i = 0; i < getOdgovori().size(); i++) {
            json += "{\"stringValue\": \"" +  getOdgovori().get(i) + "\"}";
            if(i != getOdgovori().size() - 1) json += ",";
        }
        json += "]}}, \"indexTacnog\": {\"integerValue\": \"" + index + "\"}}}";
        return json;
    }

    private static String findID(String name) {
        String[] atr = name.split("/");
        return atr[atr.length - 1];
    }

    private static String findName(JSONObject json) throws JSONException {
        return json.getString("stringValue");
    }

    private static int findIndex(JSONObject json) throws JSONException {
        return json.getInt("integerValue");
    }

    private static ArrayList<String> getAnswers(JSONArray json) throws JSONException{
        ArrayList<String> answer = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            answer.add(findName(json.getJSONObject(i)));
        }
        return answer;
    }

    public static Pitanje converFromJSON(JSONObject json) {
        Pitanje pitanje = new Pitanje();
        try {
            JSONObject fields = json.getJSONObject("fields");
            pitanje.setDocumentID(findID(json.getString("name")));
            pitanje.setNaziv(findName(fields.getJSONObject("naziv")));
            pitanje.setTekstPitanja(pitanje.getNaziv());
            pitanje.setOdgovori(getAnswers(fields.getJSONObject("odgovori").getJSONObject("arrayValue").getJSONArray("values")));
            pitanje.setTacan(pitanje.getOdgovori().get(findIndex(fields.getJSONObject("indexTacnog"))));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pitanje;
    }
}
