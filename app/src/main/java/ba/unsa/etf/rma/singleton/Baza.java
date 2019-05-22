package ba.unsa.etf.rma.singleton;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class Baza {

    private static Baza instance = new Baza();
    private static ArrayList<Kviz> kvizovi = new ArrayList<>();
    private static ArrayList<Kategorija> kategorije = new ArrayList<>();
    private static ArrayList<Pitanje> pitanja = new ArrayList<>();

    private Baza() {}

    public static Baza getInstance()
    {
        return instance;
    }

    public void dodajKviz(Kviz kviz)
    {
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
}
