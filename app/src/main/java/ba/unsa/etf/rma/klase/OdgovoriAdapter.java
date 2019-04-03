package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class OdgovoriAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> odgovori;

    public OdgovoriAdapter(Context context, ArrayList<String> odgovori) {
        super(context, R.layout.odgovor, odgovori);
        this.context = context;
        this.odgovori = odgovori;
    }
}
