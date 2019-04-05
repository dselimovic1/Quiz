package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class DodanaPitanjaAdapter extends ArrayAdapter<String> {

    ArrayList<String> dodanaPitanja;

    public DodanaPitanjaAdapter(Context context, ArrayList<String> dodanaPitanja) {
        super(context, R.layout.dodana_pitanja, dodanaPitanja);
        this.dodanaPitanja = dodanaPitanja;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String current = dodanaPitanja.get(position);

        if(convertView == null) {
            if(getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dodana_pitanja, parent, false);
            }
            else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.moguca_pitanja, parent, false);
            }
        }

        if(getItemViewType(position) == 0) {
            TextView textView = (TextView)convertView.findViewById(R.id.ime);
            textView.setText(current);
        }
        else {
            TextView textView = (TextView)convertView.findViewById(R.id.ime);
            textView.setText(current);
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
