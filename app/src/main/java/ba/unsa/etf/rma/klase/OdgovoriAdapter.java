package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class OdgovoriAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> odgovori;
    private int pozicijaTacnog = -1;

    public OdgovoriAdapter(Context context, ArrayList<String> odgovori) {
        super(context, R.layout.odgovor, odgovori);
        this.context = context;
        this.odgovori = odgovori;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == pozicijaTacnog) ? 1 : 0;
    }

    public int getPozicijaTacnog() {
        return pozicijaTacnog;
    }
}
