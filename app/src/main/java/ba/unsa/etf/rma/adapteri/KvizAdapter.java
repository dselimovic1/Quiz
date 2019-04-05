package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class KvizAdapter extends ArrayAdapter <Kviz>{

    private ArrayList<Kviz> kvizovi;

    public KvizAdapter(Context context, ArrayList<Kviz> kvizovi) {
        super(context, R.layout.kviz, kvizovi);
        this.kvizovi = kvizovi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            if(getItemViewType(position) == 0)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.kviz, parent, false);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.kviz_dodaj, parent, false);
        }
        Kviz current = kvizovi.get(position);


        if(getItemViewType(position) == 1) {
            TextView ime = (TextView)convertView.findViewById(R.id.ime);
            ime.setText(current.getNaziv());
        }
        else {
            IconView ikona = (IconView)convertView.findViewById(R.id.ikona);
            ikona.setIcon(Integer.parseInt(current.getKategorija().getId()));
            TextView ime = (TextView)convertView.findViewById(R.id.ime);
            ime.setText(current.getNaziv());
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (getCount() - 1 == position) ? 1 : 0;
    }
}
