package ba.unsa.etf.rma.singleton;

import android.util.Log;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Rang;


public class Baza  {

    public enum TaskType {QUIZ, CATEGORY, QUESTION, RANGLIST};

    private static Baza instance = new Baza();
    private static ArrayList<Kviz> kvizovi = new ArrayList<>();
    private static ArrayList<Kategorija> kategorije = new ArrayList<>();
    private static ArrayList<Rang> rangListe = new ArrayList<>();


    private Baza() {}

    public static Baza getInstance()
    {
        return instance;
    }

    public void dodajKategoriju(Kategorija kategorija)
    {
        kategorije.add(kategorija);
    }


    public Rang dajRang(String imeKviz) {
        for(Rang r : rangListe) {
            if(r.getImeKviza().equals(imeKviz)) return r;
        }
        Rang noviRang = new Rang(imeKviz);
        rangListe.add(noviRang);
        return noviRang;
    }

    public void dodajRezultat(String imeKviza, Rang.Par par) {
        Rang temp = dajRang(imeKviza);
        temp.dodajRezultat(par);
    }

    public ArrayList<Kviz> dajKvizove() {
        return new ArrayList<>(kvizovi);
    }


    public ArrayList<String> dajImenaKategorija() {
        ArrayList<String> imena = new ArrayList<>();
        for(Kategorija k : kategorije) imena.add(k.getNaziv());
        Log.d("SIZE 3:", Integer.toString(kategorije.size()));
        return imena;
    }


    public ArrayList<Kviz> dajFiltriranuListu(String filter) {
        ArrayList<Kviz> temp = new ArrayList<>();
        for(Kviz k : kvizovi) if(k.getKategorija().getNaziv().equals(filter)) temp.add(k);
        return temp;
    }
}
