package ba.unsa.etf.rma.fragmenti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class InformacijeFrag extends Fragment {

    private TextView imeKviza;
    private TextView brojTacnih;
    private TextView preostali;
    private TextView procenat;
    private Button btnZavrsiKviz;

    public InformacijeFrag() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Kviz k = (Kviz)getActivity().getIntent().getParcelableExtra("kviz");

        imeKviza = (TextView)getView().findViewById(R.id.infNazivKviza);
        brojTacnih = (TextView)getView().findViewById(R.id.infBrojTacnihPitanja);
        preostali = (TextView)getView().findViewById(R.id.infBrojPreostalihPitanja);
        procenat = (TextView)getView().findViewById(R.id.infProcenatTacni);
        btnZavrsiKviz = (Button)getView().findViewById(R.id.btnKraj);
        imeKviza.setText(k.getNaziv());
        if(getArguments() == null) {
            brojTacnih.setText("0");
            int pr = k.getPitanja().size() - 1;
            if(pr < 0) pr = 0;
            preostali.setText(Integer.toString(pr));
        }
        else {
            int tacni = getArguments().getInt("tacni");
            int pre = getArguments().getInt("preostali");
            int ukupno = getArguments().getInt("ukupno");
            brojTacnih.setText(Integer.toString(tacni));
            preostali.setText(Integer.toString(pre));
            String proc = String.format("%.2f", (100 * (double)tacni / ukupno));
            procenat.setText(proc);
        }

        btnZavrsiKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_informacije, container, false);
    }
}
