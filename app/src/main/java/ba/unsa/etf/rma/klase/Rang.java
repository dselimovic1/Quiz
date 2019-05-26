package ba.unsa.etf.rma.klase;

import android.support.annotation.NonNull;

import java.util.HashMap;

import ba.unsa.etf.rma.interfejsi.Loadable;


public class Rang implements Loadable {

    private String imeKviza;
    private HashMap<Integer, Par> mapa = new HashMap<>();
    private String documentID;

    public Rang() {}

    public Rang(String imeKviza) {
        this.imeKviza = imeKviza;
    }


    public String getImeKviza() {
        return imeKviza;
    }

    public void setImeKviza(String imeKviza) {
        this.imeKviza = imeKviza;
    }

    public HashMap<Integer, Par> getMapa() {
        return mapa;
    }


    public void dodajRezultat(Par par) {
        int pozicija = dajPoziciju(par);
        azurirajMapu(pozicija);
        mapa.put(pozicija, par);
    }

    private int dajPoziciju(Par par) {
        int pozicija = mapa.size();
        for(Par p : mapa.values()) {
            if(par.procenatTacnih > p.procenatTacnih) pozicija--;
        }
        return pozicija;
    }

    private void azurirajMapu(int pozicija) {
        for (int i = mapa.size() - 1; i >= pozicija; i--)
            mapa.put(i + 1, mapa.get(i));
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "";

        return json;
    }

    public static class Par implements Comparable<Par>{
        public String imeIgraca;
        public double procenatTacnih;

        public Par(String imeIgraca, double procenatTacnih) {
            this.imeIgraca = imeIgraca;
            this.procenatTacnih = procenatTacnih;
        }

        @Override
        public int compareTo(@NonNull Par par) {
            if(procenatTacnih < par.procenatTacnih) return 1;
            else if(procenatTacnih > par.procenatTacnih) return -1;
            return 0;
        }
    }

}
