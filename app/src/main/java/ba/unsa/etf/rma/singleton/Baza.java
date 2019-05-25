package ba.unsa.etf.rma.singleton;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.taskovi.GetItemTask;
import ba.unsa.etf.rma.taskovi.GetListTask;

public class Baza implements GetItemTask.OnItemResponse, GetListTask.OnListResponse {

    public enum TaskType {QUIZ, CATEGORY, QUESTION};

    private static String instanceResponse = "";
    private static String listResponse = "";
    private static Baza instance = new Baza();
    private static ArrayList<Kviz> kvizovi = new ArrayList<>();
    private static ArrayList<Kategorija> kategorije = new ArrayList<>();
    private static ArrayList<Pitanje> pitanja = new ArrayList<>();
    private static ArrayList<Rang> rangListe = new ArrayList<>();

    private Baza() {}

    public static Baza getInstance()
    {
        return instance;
    }

    public void dodajKviz(Kviz kviz) {
        kvizovi.add(kviz);
    }

    public void azurirajKviz(int pozicija, Kviz kviz)
    {
        kvizovi.set(pozicija, kviz);
    }

    public void dodajKategoriju(Kategorija kategorija)
    {
        kategorije.add(kategorija);
    }

    public void dodajPitanje(Pitanje pitanje)
    {
        pitanja.add(pitanje);
    }

    public Rang dajRang(String imeKviz) {
        for(Rang r : rangListe) {
            if(r.getImeKviza().equals(imeKviz)) return r;
        }
        Rang noviRang = new Rang(imeKviz);
        rangListe.add(noviRang);
        return noviRang;
    }

    public ArrayList<Kviz> dajKvizove() {
        return new ArrayList<>(kvizovi);
    }

    public ArrayList<Rang> dajRangliste() {
        return new ArrayList<>(rangListe);
    }

    public ArrayList<Kategorija> dajKategorije() {
        return new ArrayList<>(kategorije);
    }

    public ArrayList<Pitanje> dajPitanja() {
        return new ArrayList<>(pitanja);
    }

    public ArrayList<String> dajImenaKategorija() {
        ArrayList<String> imena = new ArrayList<>();
        for(Kategorija k : kategorije) imena.add(k.getNaziv());
        return imena;
    }

    public ArrayList<String> dajImenaPitanja() {
        ArrayList<String> imena = new ArrayList<>();
        for(Pitanje p : pitanja) imena.add(p.getNaziv());
        return imena;
    }

    public ArrayList<String> dajImenaKvizova() {
        ArrayList<String> imena = new ArrayList<>();
        for(Kviz k : kvizovi) imena.add(k.getNaziv());
        return imena;
    }

    public ArrayList<Kviz> dajFiltriranuListu(String filter) {
        ArrayList<Kviz> temp = new ArrayList<>();
        for(Kviz k : kvizovi) if(k.getKategorija().getNaziv().equals(filter)) temp.add(k);
        return temp;
    }

    @Override
    public void setJSONString(String response) {
        instanceResponse = response;
    }

    @Override
    public void setResponse(String response) {
        listResponse = response;
    }
}
