package ba.unsa.etf.rma.fragmenti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    private int ukupno = 0;

    private SendData data;

    public PitanjeFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            data = (SendData)getActivity();
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }

        tekstPitanja = (TextView)getView().findViewById(R.id.tekstPitanja);
        odg = (ListView)getView().findViewById(R.id.odgovoriPitanja);

        Kviz k = (Kviz)getActivity().getIntent().getSerializableExtra("kviz");
        pitanja = k.getPitanja();
        preostali = pitanja.size();
        dajRandomPitanje();

        odg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == pozicijaTacnog) brojTacnih++;
                ukupno++;
                data.onQuestionAnswered(brojTacnih, preostali, ukupno);

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(pitanja.size() != 0) dajRandomPitanje();
                else {
                    odgovori.clear();
                    adapterOdgovori.notifyDataSetChanged();
                    tekstPitanja.setText("Kviz je završen!");
                }
            }
        });
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
        odgovori = pitanja.get(index).dajRandomOdgovore();
        pozicijaTacnog = odrediPozicijuTacnog(pitanja.get(index));
        tekstPitanja.setText(pitanja.get(index).getNaziv());
        pitanja.remove(index);
        preostali = pitanja.size();
        adapterOdgovori = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, odgovori);
        odg.setAdapter(adapterOdgovori);
        adapterOdgovori.notifyDataSetChanged();
    }

    public interface SendData {
        void onQuestionAnswered(int correct, int remainder, int total);
    }
}
