package ba.unsa.etf.rma.klase;

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

    public class Par {
        public String imeIgraca;
        public double procenatTacnih;

        public Par(String imeIgraca, double procenatTacnih) {
            this.imeIgraca = imeIgraca;
            this.procenatTacnih = procenatTacnih;
        }
    }

}
