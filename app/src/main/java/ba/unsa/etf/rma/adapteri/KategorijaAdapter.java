package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class KategorijaAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Kategorija> kategorije;

    public KategorijaAdapter(Context context, ArrayList<Kategorija> kategorije) {
        super(context, R.layout.kategorija, kategorije);
        this.context = context;
        this.kategorije = kategorije;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.kategorija, parent, false);

        Kategorija current = kategorije.get(position);

        TextView ime = (TextView)convertView.findViewById(R.id.imeKategorije);
        ime.setText(current.getNaziv());
        return convertView;
    }
}
