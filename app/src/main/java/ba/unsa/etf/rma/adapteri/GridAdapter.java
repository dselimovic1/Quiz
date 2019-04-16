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

        if(view == null) {
            if(getItemViewType(i) == 0)
                view = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout, viewGroup, false);
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout_dodaj, viewGroup, false);
        }

        if(getItemViewType(i) == 0) {
            IconView iconView = (IconView) view.findViewById(R.id.ikona);
            iconView.setIcon(Integer.parseInt(current.getKategorija().getId()));
            TextView ime = (TextView) view.findViewById(R.id.imeKviza);
            ime.setText(current.getNaziv());
            TextView broj = (TextView) view.findViewById(R.id.brojPitanja);
            broj.setText(Integer.toString(current.getPitanja().size() - 1));
        }
        else {
            TextView ime = (TextView) view.findViewById(R.id.imeKviza);
            ime.setText(current.getNaziv());
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == kvizovi.size() - 1) ? 1 : 0;
    }
}
