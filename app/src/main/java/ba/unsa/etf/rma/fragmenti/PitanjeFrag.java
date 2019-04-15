package ba.unsa.etf.rma.fragmenti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {

    private ArrayList<Pitanje> pitanja;
    private int pozicijaTacnog;
    private int brojTacnih = 0;
    private int preostali = 0;

    public PitanjeFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Kviz k = (Kviz)getActivity().getIntent().getSerializableExtra("kviz");
        pitanja = k.getPitanja();
        preostali = pitanja.size();
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

    private Pitanje dajRandomPitanje (){
        int index = new Random().nextInt(pitanja.size());
        Pitanje p = pitanja.get(index);
        pitanja.remove(index);
        return p;
    }

    public interface SendData {
        public void onQuestionAnswered(int correct, int remainder);
    }
}
