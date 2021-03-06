package ba.unsa.etf.rma.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;

public class Query {

    private SQLiteDatabase database;

    public Query(SQLiteDatabase database) {
        this.database = database;
    }

    public Pitanje getQuestionFromCursor(Cursor cursor) {
        Pitanje pitanje = new Pitanje();
        pitanje.setID(cursor.getLong(0));
        pitanje.setNaziv(cursor.getString(1));
        pitanje.setTekstPitanja(pitanje.getNaziv());
        pitanje.setOdgovori(getAnswersByID(pitanje.getID()));
        pitanje.setTacan(pitanje.getOdgovori().get(cursor.getInt(2)));
        pitanje.setDocumentID(cursor.getString(3));
        return pitanje;
    }

    public Kategorija getCategoryFromCursor(Cursor cursor) {
        Kategorija kategorija = new Kategorija();
        kategorija.setID(cursor.getLong(0));
        kategorija.setNaziv(cursor.getString(1));
        kategorija.setId(cursor.getString(2));
        kategorija.setDocumentID(cursor.getString(3));
        return kategorija;
    }

    public Kviz getQuizFromCursor(Cursor cursor) {
        Kviz kviz = new Kviz();
        kviz.setID(cursor.getLong(0));
        kviz.setNaziv(cursor.getString(1));
        if(!cursor.isNull(2) && cursor.getLong(2) != 0) kviz.setKategorija(getCategoryByID(cursor.getLong(2)));
        else kviz.setKategorija(new Kategorija("Svi", "1"));
        kviz.setPitanja(getQuestionsByID(kviz.getID()));
        kviz.setDocumentID(cursor.getString(3));
        return kviz;
    }

    public Rang.Par getPairFromCursor(Cursor cursor) {
        return new Rang.Par(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2));
    }

    public Rang getRangListFromCursor(Cursor cursor) {
        Rang rang = new Rang(cursor.getLong(0), cursor.getString(1));
        rang.setHashMap(getResultsByID(rang.getID()));
        return rang;
    }

    public ArrayList<Rang.Par> getResultsByID(long ID) {
        ArrayList<Rang.Par> pairs = new ArrayList<>();
        String selection = Rang.ParEntry.PROJECTION[3] + " = ?";
        String[] selectionArgs = new String[]{Long.toString(ID)};
        Cursor cursor = database.query(Rang.ParEntry.TABLE_NAME, Rang.ParEntry.PROJECTION, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            pairs.add(getPairFromCursor(cursor));
        }
        cursor.close();
        return pairs;
    }

    public ArrayList<String> getAnswersByID(long ID) {
        ArrayList<String> answers = new ArrayList<>();
        String selection = Pitanje.OdgovorEntry.PROJECTION[1] + " = ?";
        String[] selectionArgs = new String[]{Long.toString(ID)};
        Cursor cursor = database.query(Pitanje.OdgovorEntry.TABLE_NAME, Pitanje.OdgovorEntry.PROJECTION, selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()) {
            answers.add(cursor.getString(0));
        }
        cursor.close();
        return answers;
    }

    public ArrayList<Pitanje> getQuestionsByID(long ID) {
        ArrayList<Pitanje> questions = new ArrayList<>();
        String query = "SELECT p.id, p.naziv, p.index_tacnog, p.document_id FROM Pitanja p, PitanjaKviza pk WHERE pk.id_kviza = ? AND pk.id_pitanja = p.id";
        String[] selection = new String[]{Long.toString(ID)};
        Cursor cursor = database.rawQuery(query, selection);
        while (cursor.moveToNext()) {
            questions.add(getQuestionFromCursor(cursor));
        }
        cursor.close();
        return questions;
    }

    public Kategorija getCategoryByID(long ID) {
        Kategorija category = null;
        String selection = Kategorija.KategorijaEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(ID)};
        Cursor cursor = database.query(Kategorija.KategorijaEntry.TABLE_NAME, Kategorija.KategorijaEntry.PROJECTION, selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()) {
            category = getCategoryFromCursor(cursor);
        }
        cursor.close();
        return category;
    }

    public ArrayList<Kategorija> getAllCategories() {
        ArrayList<Kategorija> categories = new ArrayList<>();
        Cursor cursor = database.query(Kategorija.KategorijaEntry.TABLE_NAME, Kategorija.KategorijaEntry.PROJECTION, null, null, null, null, null);
        while(cursor.moveToNext()) {
            categories.add(getCategoryFromCursor(cursor));
        }
        cursor.close();
        return categories;
    }

    public ArrayList<Pitanje> getAllQuestions() {
        ArrayList<Pitanje> questions = new ArrayList<>();
        Cursor cursor = database.query(Pitanje.PitanjeEntry.TABLE_NAME, Pitanje.PitanjeEntry.PROJECTION, null, null, null, null, null);
        while(cursor.moveToNext()) {
            questions.add(getQuestionFromCursor(cursor));
        }
        cursor.close();
        return questions;
    }

    public ArrayList<Kviz> getAllQuizzes() {
        ArrayList<Kviz> quizzes = new ArrayList<>();
        Cursor cursor = database.query(Kviz.KvizEntry.TABLE_NAME, Kviz.KvizEntry.PROJECTION, null, null, null, null, null);
        while (cursor.moveToNext()) {
            quizzes.add(getQuizFromCursor(cursor));
        }
        cursor.close();
        return quizzes;
    }

    public ArrayList<Kviz> getQuizzesByCategory(long ID) {
        ArrayList<Kviz> quizzes = new ArrayList<>();
        String selection = Kviz.KvizEntry.COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(ID)};
        Cursor cursor = database.query(Kviz.KvizEntry.TABLE_NAME, Kviz.KvizEntry.PROJECTION, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            quizzes.add(getQuizFromCursor(cursor));
        }
        cursor.close();
        return quizzes;
    }

    public ArrayList<Rang> getAllRangLists() {
        ArrayList<Rang> rangs = new ArrayList<>();
        Cursor cursor = database.query(Rang.RangEntry.TABLE_NAME, Rang.RangEntry.PROJECTION, null, null, null, null, null);
        while (cursor.moveToNext()) {
            rangs.add(getRangListFromCursor(cursor));
        }
        cursor.close();
        return rangs;
    }

    public void addCategory(Kategorija category) {
        long ID = database.insert(Kategorija.KategorijaEntry.TABLE_NAME, null, category.getContentValues());
        category.setID(ID);
    }

    public void addQuestion(Pitanje question) {
        long ID = database.insert(Pitanje.PitanjeEntry.TABLE_NAME, null, question.getContentValues());
        question.setID(ID);
        for(String answer : question.getOdgovori()) {
            database.insert(Pitanje.OdgovorEntry.TABLE_NAME, null, question.setContentValuesForAnswer(answer));
        }
    }

    public void addQuiz(Kviz quiz) {
        long ID = database.insert(Kviz.KvizEntry.TABLE_NAME, null, quiz.getContentValues());
        quiz.setID(ID);
        for(Pitanje question : quiz.getPitanja()) {
            database.insert(Kviz.PitanjaKvizaEntry.TABLE_NAME, null, quiz.setContentValuesForQuestion(question.getID()));
        }
    }

    public void updateQuiz(Kviz quiz) {
        String whereClause = Kviz.PitanjaKvizaEntry.COLUMN_QUIZ_ID + " = ?";
        String updateClause = Kviz.KvizEntry.COLUMN_ID + " = ?";
        String[] whereArgs = new String[]{Long.toString(quiz.getID())};
        database.delete(Kviz.PitanjaKvizaEntry.TABLE_NAME, whereClause, whereArgs);
        database.update(Kviz.KvizEntry.TABLE_NAME, quiz.getContentValues(), updateClause, whereArgs);
        for(Pitanje question : quiz.getPitanja()) {
            database.insert(Kviz.PitanjaKvizaEntry.TABLE_NAME, null, quiz.setContentValuesForQuestion(question.getID()));
        }
    }

    public void addRangList(Rang rangList) {
        long ID = database.insert(Rang.RangEntry.TABLE_NAME, null, rangList.getContentValues());
        rangList.setID(ID);
    }

    public void addResult(Rang rang, Rang.Par result) {
        long ID = database.insert(Rang.ParEntry.TABLE_NAME, null, result.getContentValues(rang.getID()));
        result.ID = ID;
    }

    public void updateRanglist(Rang rang) {
        String where = Rang.ParEntry.COLUMN_RANG_ID + " = ?";
        String[] whereArgs = new String[]{Long.toString(rang.getID())};
        database.delete(Rang.ParEntry.TABLE_NAME, where, whereArgs);
        for(Rang.Par p : rang.getMapa().values()) {
            database.insert(Rang.ParEntry.TABLE_NAME, null, p.getContentValues(rang.getID()));
        }
    }

    public void addQuizzes(ArrayList<Kviz> list) {
        for(Kviz k : list) addQuiz(k);
    }

    public void addCategories(ArrayList<Kategorija> list) {
        for(Kategorija k : list) addCategory(k);
    }

    public void addQuestions(ArrayList<Pitanje> list) {
        for(Pitanje p : list) addQuestion(p);
    }

    public void updateQuizzes(ArrayList<Kviz> list) {
        for(Kviz k : list) updateQuiz(k);
    }

    public void addRanglists(ArrayList<Rang> list) {
        for(Rang r : list) {
            addRangList(r);
            for(Rang.Par p : r.getMapa().values()) addResult(r, p);
        }
    }

    public void updateRanglists(ArrayList<Rang> list) {
        for(Rang r: list) {
            updateRanglist(r);
        }
    }

    public long setQuestionIDByName(String questionName) {
        Pitanje p = null;
        String selection = Pitanje.PitanjeEntry.PROJECTION[1] + " = ?";
        String[] args = new String[]{questionName};
        Cursor cursor = database.query(Pitanje.PitanjeEntry.TABLE_NAME, Pitanje.PitanjeEntry.PROJECTION, selection, args, null, null, null);
        while(cursor.moveToNext()) {
            p = getQuestionFromCursor(cursor);
        }
        cursor.close();
        return p.getID();
    }

    public long setCategoryIDByName(String categoryName) {
        Kategorija k = null;
        String selection = Kategorija.KategorijaEntry.PROJECTION[1] + " = ?";
        String[] args = new String[]{categoryName};
        Cursor cursor = database.query(Kategorija.KategorijaEntry.TABLE_NAME, Kategorija.KategorijaEntry.PROJECTION, selection, args, null, null, null);
        while(cursor.moveToNext()) {
            k = getCategoryFromCursor(cursor);
        }
        cursor.close();
        return k.getID();
    }

    public Kviz setQuestionIDs(Kviz kviz) {
        for(Pitanje p : kviz.getPitanja()) {
            p.setID(setQuestionIDByName(p.getNaziv()));
        }
        return kviz;
    }

    public ArrayList<Kviz> setEntriesToUpdate(ArrayList<Kviz> entries) {
        for(Kviz k : entries) {
            k = setQuestionIDs(k);
            if(k.getKategorija().getNaziv().equals("Svi") == false)
                k.getKategorija().setID(setCategoryIDByName(k.getKategorija().getNaziv()));
        }
        return entries;
    }
}
