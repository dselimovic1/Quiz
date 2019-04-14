package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class OdgovoriFragmentAdapter extends ArrayAdapter<String> {

    private ArrayList<String> odgovori;
    private int odabran = -1;
    private int tacan;

    public OdgovoriFragmentAdapter(Context context, ArrayList<String> odgovori) {
        super(context, );
    }
}
