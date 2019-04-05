package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class MogucaPitanjaAdapter extends ArrayAdapter<String> {

    ArrayList<String> mogucaPitanja;

    public MogucaPitanjaAdapter(Context context, ArrayList<String> mogucaPitanja) {
        super(context, R.layout.moguca_pitanja, mogucaPitanja);
        this.mogucaPitanja = mogucaPitanja;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String current = mogucaPitanja.get(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.moguca_pitanja, parent,false);

        TextView textView = (TextView)convertView.findViewById(R.id.ime);
        textView.setText(current);
        return convertView;
    }
}
