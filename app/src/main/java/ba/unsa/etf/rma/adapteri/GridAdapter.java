package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class GridAdapter extends ArrayAdapter<Kviz> {

    private ArrayList<Kviz> kvizovi;

    public GridAdapter(Context context, ArrayList<Kviz> kvizovi) {
        super(context, R.layout.grid_layout ,kvizovi);
        this.kvizovi = kvizovi;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Kviz current = kvizovi.get(i);

        if(view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout, viewGroup, false);

        IconView iconView = (IconView)view.findViewById(R.id.ikona);
        iconView.setIcon(Integer.parseInt(current.getKategorija().getId()));
        TextView ime = (TextView)view.findViewById(R.id.imeKviza);
        ime.setText(current.getNaziv());
        TextView broj = (TextView)view.findViewById(R.id.brojPitanja);
        broj.setText("0");

        return view;
    }
}
