package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class OdgovoriFragmentAdapter extends ArrayAdapter<String> {

    private ArrayList<String> odgovori;
    private int pozicijaTacnog;
    private int odabran;

    public OdgovoriFragmentAdapter(Context context, ArrayList<String> odgovori, int pozicijaTacnog) {
        super(context, R.layout.odgovor_u_fragmentu, odgovori);
        this.odgovori = odgovori;
        this.pozicijaTacnog = pozicijaTacnog;
        this.odabran = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String current = odgovori.get(position);


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(odabran == -1) return 0;
        return (position == pozicijaTacnog) ? 1 : 2;
    }

    public void setOdabran(int odabran) {
        this.odabran = odabran;
    }
}
