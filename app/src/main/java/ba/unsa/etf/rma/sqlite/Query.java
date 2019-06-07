package ba.unsa.etf.rma.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class Query {

    private SQLiteDatabase database;

    public Query(SQLiteDatabase database) {
        this.database = database;
    }

    public ArrayList<String> getAnswersByID(long ID) {
        ArrayList<String> answers = new ArrayList<>();
        String selection = Pitanje.OdgovorEntry.PROJECTION[1] + " = ?";
        String[] selectionArgs = new String[]{Long.toString(ID)};
        Cursor cursor = database.query(Pitanje.OdgovorEntry.TABLE_NAME, Pitanje.OdgovorEntry.PROJECTION, selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()) {
            answers.add(cursor.getString(0));
        }
        return answers;
    }

    public Pitanje getQuestionFromCursor(Cursor cursor) {
        Pitanje pitanje = new Pitanje();
        pitanje.setID(cursor.getLong(0));
        pitanje.setNaziv(cursor.getString(1));
        pitanje.setTekstPitanja(pitanje.getNaziv());
        pitanje.setOdgovori(getAnswersByID(pitanje.getID()));
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

    public ArrayList<Pitanje> getQuestionsByID(long ID) {
        ArrayList<Pitanje> questions = new ArrayList<>();
        return questions;
    }

    public Kviz getQuizFromCursor(Cursor cursor) {
        Kviz kviz = new Kviz();
        return kviz;
    }

    public ArrayList<Kategorija> getAllCategories() {
        ArrayList<Kategorija> categories = new ArrayList<>();
        Cursor cursor = database.query(Kategorija.KategorijaEntry.TABLE_NAME, Kategorija.KategorijaEntry.PROJECTION, null, null, null, null, null);
        while(cursor.moveToNext()) {
            categories.add(getCategoryFromCursor(cursor));
        }
        return categories;
    }

    public ArrayList<Pitanje> getAllQuestions() {
        ArrayList<Pitanje> questions = new ArrayList<>();
        Cursor cursor = database.query(Pitanje.PitanjeEntry.TABLE_NAME, Pitanje.PitanjeEntry.PROJECTION, null, null, null, null, null);
        while(cursor.moveToNext()) {
            questions.add(getQuestionFromCursor(cursor));
        }
        return questions;
    }
}
