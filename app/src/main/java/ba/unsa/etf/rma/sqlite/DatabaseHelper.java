package ba.unsa.etf.rma.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "baza.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Kategorija.KategorijaEntry.CREATE_TABLE);
        sqLiteDatabase.execSQL(Kviz.KvizEntry.CREATE_TABLE);
        sqLiteDatabase.execSQL(Pitanje.PitanjeEntry.CREATE_TABLE);
        sqLiteDatabase.execSQL(Kviz.PitanjaKvizaEntry.CREATE_TABLE);
        sqLiteDatabase.execSQL(Pitanje.OdgovorEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Pitanje.OdgovorEntry.DROP_TABLE);
        sqLiteDatabase.execSQL(Kviz.PitanjaKvizaEntry.DROP_TABLE);
        sqLiteDatabase.execSQL(Pitanje.PitanjeEntry.DROP_TABLE);
        sqLiteDatabase.execSQL(Kviz.KvizEntry.DROP_TABLE);
        sqLiteDatabase.execSQL(Kategorija.KategorijaEntry.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
