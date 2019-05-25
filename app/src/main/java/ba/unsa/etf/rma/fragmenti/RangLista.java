package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.singleton.Baza;

public class RangLista extends Fragment {


    private Rang rang;
    private ListView rangList;
    private Baza baza;

    public RangLista() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rangList = (ListView)getView().findViewById(R.id.rangLista);
        baza = Baza.getInstance();

        Bundle argument = getArguments();
        rang = baza.dajRang(argument.getString("imeKviza"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rang_lista, container, false);
    }

}
