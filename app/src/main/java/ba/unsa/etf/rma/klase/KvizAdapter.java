package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class KvizAdapter extends ArrayAdapter <Kviz>{

    private Context context;
    private ArrayList<Kviz> kvizovi;

    public KvizAdapter(Context context, ArrayList<Kviz> kvizovi) {
        super(context, R.layout.kviz, kvizovi);

        this.context = context;
        this.kvizovi = kvizovi;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        return converView;
    }
}
