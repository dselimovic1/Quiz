package ba.unsa.etf.rma.fragmenti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {

    private TextView tekstPitanja;
    private ListView odg;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<String> odgovori;
    private ArrayAdapter<String> adapterOdgovori;

    private int pozicijaTacnog;
    private int brojTacnih = 0;
    private int preostali = 0;

    public PitanjeFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tekstPitanja = (TextView)getView().findViewById(R.id.tekstPitanja);
        odg = (ListView)getView().findViewById(R.id.odgovoriPitanja);

        Kviz k = (Kviz)getActivity().getIntent().getSerializableExtra("kviz");
        pitanja = k.getPitanja();
        preostali = pitanja.size();
        dajRandomPitanje();

        adapterOdgovori = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, odgovori);
        odg.setAdapter(adapterOdgovori);
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

    private void dajRandomPitanje() {
        int index = new Random().nextInt(pitanja.size());
        odgovori = pitanja.get(index).getOdgovori();
        pozicijaTacnog = odrediPozicijuTacnog(pitanja.get(index));
        tekstPitanja.setText(pitanja.get(index).getNaziv());
        pitanja.remove(index);
    }

    public interface SendData {
        public void onQuestionAnswered(int correct, int remainder);
    }
}
