package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kviz;

public class GridAdapter extends ArrayAdapter<Kviz> {

    private ArrayList<Kviz> kvizovi;

    public GridAdapter(Context context, ArrayList<Kviz> kvizovi) {
        super(context, android.R.layout.simple_dropdown_item_1line ,kvizovi);
        this.kvizovi = kvizovi;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
