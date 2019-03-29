package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class KategorijaAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Kategorija> kategorije;

    public KategorijaAdapter(Context context, ArrayList<Kategorija> kategorije) {
        super(context, R.layout.kategorija, kategorije);
        this.context = context;
        this.kategorije = kategorije;
    }
}
