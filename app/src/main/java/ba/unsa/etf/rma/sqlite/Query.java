package ba.unsa.etf.rma.sqlite;

import android.database.Cursor;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;

public class Query {

    private Query() {}

    public static String getAnswerFromCuror(Cursor cursor) {
        return cursor.getString(0);
    }

    public static Pitanje getQuestionFromCursor(Cursor questionCursor, Cursor answerCursor) {
        Pitanje pitanje = new Pitanje();
        pitanje.setID(questionCursor.getLong(0));
        pitanje.setNaziv(questionCursor.getString(1));
        pitanje.setTekstPitanja(pitanje.getNaziv());
        int indexTacnog = questionCursor.getInt(2);
        while(answerCursor.moveToNext()) {
            pitanje.dodajOdgovor(getAnswerFromCuror(answerCursor));
        }
        pitanje.setTacan(pitanje.getOdgovori().get(indexTacnog));
        return pitanje;
    }

    public static Kategorija getCategoryFromCursor(Cursor cursor) {
        Kategorija kategorija = new Kategorija();
        kategorija.setID(cursor.getLong(0));
        kategorija.setNaziv(cursor.getString(1));
        kategorija.setId(cursor.getColumnName(2));
        return kategorija;
    }
}
