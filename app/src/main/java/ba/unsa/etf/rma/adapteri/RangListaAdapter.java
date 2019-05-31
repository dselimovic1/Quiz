package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Rang;

public class RangListaAdapter extends ArrayAdapter<Rang.Par> {

    private ArrayList<Rang.Par> lista;

    public RangListaAdapter(Context context, ArrayList<Rang.Par> lista) {
        super(context, R.layout.ranglista, lista);
        this.lista = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranglista, parent, false);

        Rang.Par current = lista.get(position);
        String proc = String.format("%.2f", current.procenatTacnih);
        TextView pozicija = (TextView)convertView.findViewById(R.id.pozicija);
        TextView igrac = (TextView)convertView.findViewById(R.id.igrac);
        TextView procenat = (TextView)convertView.findViewById(R.id.procenat);
        pozicija.setText(Integer.toString(position + 1) + ".");
        igrac.setText(current.imeIgraca);
        procenat.setText(proc + " %");
        return convertView;
    }
}
