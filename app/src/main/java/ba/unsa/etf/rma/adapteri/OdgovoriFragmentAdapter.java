package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class OdgovoriFragmentAdapter extends ArrayAdapter<String> {

    private ArrayList<String> odgovori;
    private int pozicijaTacnog;

    public OdgovoriFragmentAdapter(Context context, ArrayList<String> odgovori) {
        super(context, android.R.layout.simple_list_item_2, odgovori);
        this.odgovori = odgovori;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String current = odgovori.get(position);


        return convertView;
    }
}
