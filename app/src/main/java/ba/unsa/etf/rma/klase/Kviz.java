package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;

public class Kviz implements Parcelable, FirestoreStorable {

    private String naziv;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private Kategorija kategorija;
    private String documentID;

    public Kviz() {
    }

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public static final Creator<Kviz> CREATOR = new Creator<Kviz>() {
        @Override
        public Kviz createFromParcel(Parcel parcel) {
            return new Kviz(parcel);
        }

        @Override
        public Kviz[] newArray(int i) {
            return new Kviz[i];
        }
    };

    protected Kviz(Parcel parcel) {
        naziv = parcel.readString();
        pitanja = parcel.readArrayList(getClass().getClassLoader());
        kategorija = parcel.readParcelable(getClass().getClassLoader());
        documentID = parcel.readString();
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeList(pitanja);
        parcel.writeParcelable(kategorija, 0);
        parcel.writeString(documentID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kviz kviz = (Kviz) o;
        return Objects.equals(naziv, kviz.naziv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv);
    }

    public ArrayList<String> dajImenaPitanja() {
        ArrayList<String> imena = new ArrayList<>();
        for(Pitanje p : pitanja) imena.add(p.getNaziv());
        return imena;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "";
        json += "{\"fields\": {\"pitanja\": {\"arrayValue\": {\"values\": [";
        for(int i = 0; i < pitanja.size(); i++) {
            json += "{\"stringValue\": \"" + pitanja.get(i).getDocumentID() + "\"}";
            if(i != pitanja.size() - 1) json += ",";
        }
        json += "]}},\"idKategorije\": {\"stringValue\": \"" + getKategorija().getDocumentID() + "\"}," +
                "\"naziv\": {\"stringValue\": \"" + getNaziv() + "\"}}}";
        return json;
    }

    private static String findID(String name) {
        String[] atr = name.split("/");
        return atr[atr.length - 1];
    }

    private static String findName(JSONObject json) throws JSONException {
        return json.getString("stringValue");
    }

    private static ArrayList<Pitanje> getQuestions(JSONArray array) throws JSONException {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Pitanje p = new Pitanje();
            p.setDocumentID(findName(array.getJSONObject(i)));
            pitanja.add(p);
        }
        return pitanja;
    }

    private static Kategorija getCategory(JSONObject json) throws JSONException {
        String ID = findName(json);
        Kategorija k = new Kategorija();
        k.setDocumentID(ID);
        return k;
    }

    public static Kviz convertFromJSON(JSONObject json) {
        Kviz kviz = new Kviz();
        try {
            JSONObject fields = json.getJSONObject("fields");
            kviz.setDocumentID(findID(json.getString("name")));
            kviz.setNaziv(findName(fields.getJSONObject("naziv")));
            kviz.setPitanja(getQuestions(fields.getJSONObject("pitanja").getJSONObject("arrayValue").getJSONArray("values")));
            kviz.setKategorija(getCategory(fields.getJSONObject("idKategorije")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
