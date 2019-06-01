package ba.unsa.etf.rma.fragmenti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.OdgovoriFragmentAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;

import static ba.unsa.etf.rma.helperi.MiscHelper.odrediIndeks;
import static ba.unsa.etf.rma.helperi.MiscHelper.setListViewHeightBasedOnChildren;

public class PitanjeFrag extends Fragment {

    private TextView tekstPitanja;
    private ListView odg;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<String> odgovori;
    private OdgovoriFragmentAdapter adapterOdgovori;

    private String imeKviza;
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
        imeKviza = k.getNaziv();
        pitanja = k.getPitanja();
        preostali = pitanja.size() - 1;
        if(preostali < 0 ) preostali = 0;
        if(pitanja.size() != 0) {
            dajRandomPitanje();
            setListViewHeightBasedOnChildren(odg);
        }
        else zavrsiKviz();


        odg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                if(i == pozicijaTacnog) brojTacnih++;
                ukupno++;
                adapterOdgovori.setOdabran(i);
                adapterOdgovori.notifyDataSetChanged();
                data.onQuestionAnswered(brojTacnih, preostali, ukupno);
                odg.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        odg.setEnabled(true);
                        adapterOdgovori.setOdabran(-1);
                        if(pitanja.size() != 0)  {
                            dajRandomPitanje();
                            data.onQuestionAnswered(brojTacnih, preostali, ukupno);
                            setListViewHeightBasedOnChildren(odg);
                        }
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

    private void dajRandomPitanje() {
        int index = new Random().nextInt(pitanja.size());
        odgovori = pitanja.get(index).dajRandomOdgovore();
        pozicijaTacnog = odrediIndeks(odgovori, pitanja.get(index).getTacan());
        tekstPitanja.setText(pitanja.get(index).getNaziv());
        pitanja.remove(index);
        preostali = pitanja.size();
        adapterOdgovori = new OdgovoriFragmentAdapter(getContext(), odgovori, pozicijaTacnog);
        odg.setAdapter(adapterOdgovori);
        adapterOdgovori.notifyDataSetChanged();
    }

    private void zavrsiKviz() {
        tekstPitanja.setText("Kviz je završen!");
        prikaziAlertDialog();
    }

    private void prikaziAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Unesite ime: ");
        final EditText input = new EditText(getContext());
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(input.getText().toString().equals("")) return;
                Rang.Par par = new Rang.Par(input.getText().toString(), (double)brojTacnih / ukupno * 100);
                data.showRangList(par);
            }
        });

        alertDialog.show();
    }

    public interface SendData {
        void onQuestionAnswered(int correct, int remainder, int total);
        void showRangList(Rang.Par par);
    }
}
