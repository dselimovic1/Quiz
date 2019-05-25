package ba.unsa.etf.rma.klase;

import android.support.annotation.NonNull;

import java.util.HashSet;

public class Rang {

    private String imeKviza;
    private HashSet<Par> set = new HashSet<>();

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

    public HashSet<Par> getSet() {
        return set;
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
            if(procenatTacnih < par.procenatTacnih) return -1;
            else if(procenatTacnih > par.procenatTacnih) return 1;
            return 0;
        }
    }

}
