package ba.unsa.etf.rma.helperi;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class MiscHelper {

    public static ArrayList<String> izdvojiImenaPitanja(ArrayList<Pitanje> pitanja) {
        ArrayList<String> imena = new ArrayList<>();
        for(Pitanje p : pitanja) imena.add(p.getNaziv());
        return imena;
    }

    public static ArrayList<String> izdvojiImenaKategorija(ArrayList<Kategorija> kategorije) {
        ArrayList<String> imena = new ArrayList<>();
        for(Kategorija k : kategorije) imena.add(k.getNaziv());
        return imena;
    }

    public static ArrayList<String> izvdojiImenaKvizova(ArrayList<Kviz> kvizovi) {
        ArrayList<String> imena = new ArrayList<>();
        for(Kviz k : kvizovi) imena.add(k.getNaziv());
        return imena;
    }

    public static ArrayList<Pitanje> izdvojiPitanja(ArrayList<Pitanje> svaPitanja, ArrayList<String> imena) {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        for(Pitanje p : svaPitanja) {
            for(String s : imena) {
                if(s.equals(p.getNaziv())) pitanja.add(p);
            }
        }
        return pitanja;
    }

    public static int nadjiPozicijuUSpinneru(ArrayList<String> spinner, String imeKategorije) {
        for(int i = 0; i < spinner.size(); i++) {
            if(spinner.get(i).equals(imeKategorije)) return i;
        }
        return -1;
    }

    public static Kategorija odrediKategoriju(ArrayList<Kategorija> kategorije, String s) {
        for (Kategorija k : kategorije) {
            if (k.getNaziv().equals(s)) {
                return k;
            }
        }
        return new Kategorija("Svi", "1");
    }

    private static Kategorija nadjiKategorijuPoID(ArrayList<Kategorija> kategorije, String ID) {
        for (Kategorija k : kategorije) {
            if (k.getDocumentID().equals(ID)) {
                return k;
            }
        }
        return new Kategorija("Svi", "1");
    }

    private static ArrayList<Pitanje> nadjiPitanjaPoID(ArrayList<Pitanje> pitanja, ArrayList<Pitanje> pitanjaID) {
        ArrayList<Pitanje> novaPitanja = new ArrayList<>();
        for(Pitanje p : pitanja) {
            for(Pitanje temp : pitanjaID) {
                if(temp.getDocumentID().equals(p.getDocumentID())) {
                    novaPitanja.add(p);
                    break;
                }
            }
        }
        return novaPitanja;
    }

    public static void azurirajKvizove(ArrayList<Kviz> kvizovi, ArrayList<Pitanje> pitanja, ArrayList<Kategorija> kategorije) {
        for(int i = 0; i < kvizovi.size(); i++) {
            kvizovi.get(i).setKategorija(nadjiKategorijuPoID(kategorije, kvizovi.get(i).getKategorija().getDocumentID()));
            kvizovi.get(i).setPitanja(nadjiPitanjaPoID(pitanja, kvizovi.get(i).getPitanja()));
        }
    }
}
