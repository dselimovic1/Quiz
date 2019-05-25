package ba.unsa.etf.rma.klase;

import java.util.HashMap;

public class Rang {

    private String imeKviza;
    private HashMap<Integer, HashMap<String, Double>> lista = new HashMap<>();

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

    public HashMap<Integer, HashMap<String, Double>> getLista() {
        return lista;
    }
}
