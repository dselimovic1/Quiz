package ba.unsa.etf.rma.klase;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;

import static ba.unsa.etf.rma.converteri.JSONCategoryConverter.findCategoryID;
import static ba.unsa.etf.rma.converteri.JSONCategoryConverter.findCategoryName;
import static ba.unsa.etf.rma.converteri.JSONCategoryConverter.findID;

public class Kategorija implements Parcelable, FirestoreStorable {

    private String naziv;
    private String id;
    private String documentID;
    private long ID;

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
        ID = parcel.readLong();
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
        parcel.writeLong(ID);
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

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KategorijaEntry.COLUMN_NAME, naziv);
        contentValues.put(KategorijaEntry.COLUMN_ICON, id);
        contentValues.put(KategorijaEntry.DOCUMENT_ID, documentID);
        return contentValues;
    }

    public static class KategorijaEntry {
        private KategorijaEntry() {}

        public static final String TABLE_NAME = "Kategorije";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "naziv";
        public static final String COLUMN_ICON = "id_ikone";
        public static final String DOCUMENT_ID = "document_id";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                DOCUMENT_ID + " TEXT NOT NULL," +
                COLUMN_ICON + " TEXT NOT NULL);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_ICON, DOCUMENT_ID};
    }
}
