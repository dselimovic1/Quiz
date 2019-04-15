package ba.unsa.etf.rma.fragmenti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {

    private ArrayList<Pitanje> pitanja;
    private int pozicijaTacnog;

    public PitanjeFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Kviz k = (Kviz)getActivity().getIntent().getSerializableExtra("kviz");
        pitanja = k.getPitanja();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pitanje, container, false);
    }

    private int odrediPozicijuTacnog(Pitanje p) {
        for(int i = 0; i < p.getOdgovori().size(); i++) {
            if(p.getTacan().equals(p.getOdgovori().get(i))) return i;
        }
        return -1;
    }

    public interface OnItemClick {
        public void onItemClicked(int position);
    }
}
