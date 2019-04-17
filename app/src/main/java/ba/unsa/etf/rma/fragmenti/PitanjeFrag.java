package ba.unsa.etf.rma.fragmenti;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.OdgovoriFragmentAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {

    private TextView tekstPitanja;
    private ListView odg;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<String> odgovori;
    private OdgovoriFragmentAdapter adapterOdgovori;

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

        Kviz k = (Kviz)getActivity().getIntent().getParcelableExtra("kviz");
        pitanja = k.getPitanja();
        preostali = pitanja.size();
        if(pitanja.size() != 0) dajRandomPitanje();
        else zavrsiKviz();


        odg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                if(i == pozicijaTacnog) brojTacnih++;
                ukupno++;
                adapterOdgovori.setOdabran(i);
                adapterOdgovori.notifyDataSetChanged();
                data.onQuestionAnswered(brojTacnih, preostali, ukupno);
                odg.setClickable(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        odg.setClickable(true);
                        if(pitanja.size() != 0) dajRandomPitanje();
                        else{
                            zavrsiKviz();
                            odgovori.clear();
                            adapterOdgovori.notifyDataSetChanged();
                        }
                    }
                },2000);
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

    private int odrediPozicijuTacnog(ArrayList<String> list, String s) {
        for(int i = 0; i < list.size(); i++) {
            if(s.equals(list.get(i))) return i;
        }
        return -1;
    }

    private void dajRandomPitanje() {
        int index = new Random().nextInt(pitanja.size());
        odgovori = pitanja.get(index).dajRandomOdgovore();
        pozicijaTacnog = odrediPozicijuTacnog(odgovori, pitanja.get(index).getTacan());
        tekstPitanja.setText(pitanja.get(index).getNaziv());
        pitanja.remove(index);
        preostali = pitanja.size();
        adapterOdgovori = new OdgovoriFragmentAdapter(getContext(), odgovori, pozicijaTacnog);
        odg.setAdapter(adapterOdgovori);
        adapterOdgovori.notifyDataSetChanged();
    }

    private void zavrsiKviz() {
        tekstPitanja.setText("Kviz je završen!");
    }

    public interface SendData {
        void onQuestionAnswered(int correct, int remainder, int total);
    }
}
