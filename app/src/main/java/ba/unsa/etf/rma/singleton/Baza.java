package ba.unsa.etf.rma.singleton;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Rang;


public class Baza  {

    public enum TaskType {QUIZ, CATEGORY, QUESTION, RANGLIST};

    private static Baza instance = new Baza();
    private static ArrayList<Rang> rangListe = new ArrayList<>();


    private Baza() {}

    public static Baza getInstance()
    {
        return instance;
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


}
