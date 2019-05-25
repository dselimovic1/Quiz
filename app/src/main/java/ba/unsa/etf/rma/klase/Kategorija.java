package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Kategorija implements Parcelable {

    private String naziv;
    private String id;
    private String documentID;

    public Kategorija() {
    }

    public Kategorija(String naziv, String id) {
        this.naziv = naziv;
        this.id = id;
    }

    protected Kategorija(Parcel parcel) {
        naziv = parcel.readString();
        id = parcel.readString();
        documentID = parcel.readString();
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final Creator<Kategorija> CREATOR = new Creator<Kategorija>() {
        @Override
        public Kategorija createFromParcel(Parcel parcel) {
            return new Kategorija(parcel);
        }

        @Override
        public Kategorija[] newArray(int i) {
            return new Kategorija[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeString(id);
        parcel.writeString(documentID);
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "{\"fields\": {\"idIkonice\": {\"stringValue\": \"" + getId() + "\"}," +
                "\"naziv\": {\"stringValue\": \"" + getNaziv() + "\"}}}";
        return json;
    }

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

    public static Kategorija convertFromJSON(JSONObject json) {
        Kategorija kategorija = new Kategorija();
        try {
            JSONObject fields = json.getJSONObject("fields");
            kategorija.setDocumentID(findID(json.getString("name")));
            kategorija.setId(findCategoryID(fields.getJSONObject("idIkonice")));
            kategorija.setNaziv(findCategoryName(fields.getJSONObject("naziv")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kategorija;
    }
}
