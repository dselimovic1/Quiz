package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class OdgovoriAdapter extends ArrayAdapter<String> {

    private ArrayList<String> odgovori;
    private int pozicijaTacnog = -1;

    public OdgovoriAdapter(Context context, ArrayList<String> odgovori) {
        super(context, R.layout.odgovor, odgovori);
        this.odgovori = odgovori;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String current = odgovori.get(position);

        if(convertView == null)
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.odgovor, parent, false);



        TextView tekst = (TextView)convertView.findViewById(R.id.tekstOdgovora);
        tekst.setText(current);
        if(position == pozicijaTacnog) tekst.setText("Ovo je tačan odgovor");

        return convertView;
    }

    public void remove(int position) {
        if(position < pozicijaTacnog) {
            pozicijaTacnog--;
        }
        else if(position == pozicijaTacnog) {
            pozicijaTacnog = -1;
        }
        odgovori.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public void remove(String position) {
        odgovori.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public void add(String position) {
        odgovori.add(position);
        this.notifyDataSetChanged();
    }

    public void setPozicijaTacnog(int pozicijaTacnog) {
        this.pozicijaTacnog = pozicijaTacnog;
    }
}
