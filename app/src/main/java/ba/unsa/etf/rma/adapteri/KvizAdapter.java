package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class KvizAdapter extends ArrayAdapter <Kviz> implements Filterable {

    private ArrayList<Kviz> kvizovi;
    private ArrayList<Kviz> filter;
    private KvizFilter filterKvizova = new KvizFilter();

    public KvizAdapter(Context context, ArrayList<Kviz> kvizovi) {
        super(context, R.layout.kviz, kvizovi);
        this.kvizovi = kvizovi;
        this.filter = kvizovi;
    }

    @Override
    public Filter getFilter() {
        return filterKvizova;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.kviz, parent, false);
        }
        Kviz current = filter.get(position);

        IconView ikona = (IconView)convertView.findViewById(R.id.ikona);
        ikona.setIcon(Integer.parseInt(current.getKategorija().getId()));
        TextView ime = (TextView)convertView.findViewById(R.id.ime);
        ime.setText(current.getNaziv());

        return convertView;
    }


    public class KvizFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                filterResults.count = kvizovi.size();
                filterResults.values = kvizovi;
            }
            else {
                ArrayList<Kviz> filtriranaLista = new ArrayList<>();
                for(Kviz k : kvizovi) {
                    if(k.getKategorija().getNaziv().equals(charSequence))
                        filtriranaLista.add(k);
                }
                filterResults.count = filtriranaLista.size();
                filterResults.values = filtriranaLista;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(filterResults.count == 0)
                notifyDataSetInvalidated();
            else {
                filter = (ArrayList<Kviz>) filterResults.values;
                notifyDataSetChanged();
            }

        }
    }
}
