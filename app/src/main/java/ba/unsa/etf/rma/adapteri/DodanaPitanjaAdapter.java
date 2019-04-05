package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class DodanaPitanjaAdapter extends ArrayAdapter<String> {

    ArrayList<String> dodanaPitanja;

    public DodanaPitanjaAdapter(Context context, ArrayList<String> dodanaPitanja) {
        super(context, R.layout.dodana_pitanja, dodanaPitanja);
        this.dodanaPitanja = dodanaPitanja;
    }


}
