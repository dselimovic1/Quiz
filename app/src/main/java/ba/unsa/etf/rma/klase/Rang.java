package ba.unsa.etf.rma.klase;

import android.support.annotation.NonNull;

import java.util.HashMap;


public class Rang {

    private String imeKviza;
    private HashMap<Integer, Par> mapa = new HashMap<>();

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


    public int dajPoziciju(Par par) {
        int pozicija = mapa.size();
        for(Par p : mapa.values()) {
            if(par.procenatTacnih > p.procenatTacnih) pozicija--;
        }
        azurirajMapu(pozicija);
        return pozicija;
    }

    private void azurirajMapu(int pozicija) {
        for(int i = mapa.size(); i>= pozicija; i--)
            mapa.put(i, mapa.get(i - 1));
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
