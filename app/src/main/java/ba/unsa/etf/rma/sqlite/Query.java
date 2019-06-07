package ba.unsa.etf.rma.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;

public class Query {

    private SQLiteDatabase database;

    public Query(SQLiteDatabase database) {
        this.database = database;
    }

    public static String getAnswerFromCuror(Cursor cursor) {
        return cursor.getString(0);
    }

    public Pitanje getQuestionFromCursor(Cursor cursor) {
        Pitanje pitanje = new Pitanje();
        pitanje.setID(cursor.getLong(0));
        pitanje.setNaziv(cursor.getString(1));
        pitanje.setTekstPitanja(pitanje.getNaziv());
        String selection = Pitanje.OdgovorEntry.PROJECTION[1] + " = ?";
        String[] selectionArgs = new String[]{Long.toString(pitanje.getID())};
        Cursor answerCursor = database.query(Pitanje.OdgovorEntry.TABLE_NAME, Pitanje.OdgovorEntry.PROJECTION, selection, selectionArgs, null, null, null);
        while(answerCursor.moveToNext()) {
            pitanje.dodajOdgovor(getAnswerFromCuror(answerCursor));
        }
        pitanje.setTacan(pitanje.getOdgovori().get(cursor.getInt(2)));
        return pitanje;
    }

    public Kategorija getCategoryFromCursor(Cursor cursor) {
        Kategorija kategorija = new Kategorija();
        kategorija.setID(cursor.getLong(0));
        kategorija.setNaziv(cursor.getString(1));
        kategorija.setId(cursor.getColumnName(2));
        return kategorija;
    }
}
