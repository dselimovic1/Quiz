package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class OdgovoriFragmentAdapter extends ArrayAdapter<String> {

    private ArrayList<String> odgovori;
    private int odabran = -1;
    private int tacan;

    public OdgovoriFragmentAdapter(Context context, ArrayList<String> odgovori, int tacan) {
        super(context, R.layout.odgovor_fragment, odgovori);
        this.odgovori = odgovori;
        this.tacan = tacan;
    }
}
