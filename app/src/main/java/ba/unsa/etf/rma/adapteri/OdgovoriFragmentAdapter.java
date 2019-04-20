package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class OdgovoriFragmentAdapter extends ArrayAdapter<String> {

    private ArrayList<String> odgovori;
    private int pozicijaTacnog;
    private int odabran;

    public OdgovoriFragmentAdapter(Context context, ArrayList<String> odgovori, int pozicijaTacnog) {
        super(context, R.layout.odgovor, odgovori);
        this.odgovori = odgovori;
        this.pozicijaTacnog = pozicijaTacnog;
        this.odabran = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String current = odgovori.get(position);

        if(convertView == null) {
            if(getItemViewType(position) == 0)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.odgovor, parent, false);
            else if(getItemViewType(position) == 1)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.tacan_odgovor, parent, false);
            else if(getItemViewType(position) == 2)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.netacan_odgovor, parent, false);
        }

        if(getItemViewType(position) == 0) {
            TextView textView = (TextView)convertView.findViewById(R.id.tekstOdgovora);
            textView.setText(current);
        }
        else if(getItemViewType(position) == 1) {
            TextView textView = (TextView)convertView.findViewById(R.id.tekstOdgovora);
            textView.setText(current);
        }
        else if(getItemViewType(position) == 2) {
            TextView textView = (TextView)convertView.findViewById(R.id.tekstOdgovora);
            textView.setText(current);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(odabran == position) {
            if(odabran != pozicijaTacnog) return 2;
            else return 1;
        }
        if(odabran != position && odabran != -1) {
            if(position != pozicijaTacnog) return 0;
            else return 1;
        }
        return 0;
    }

    public void setOdabran(int odabran) {
        this.odabran = odabran;
    }
}
