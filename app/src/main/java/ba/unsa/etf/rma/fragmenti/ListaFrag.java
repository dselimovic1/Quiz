package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class ListaFrag extends Fragment {


    private ArrayList<String> kategorije;
    private ArrayAdapter<String> kategorijeAdapter;
    private ListView listaKategorije;

    public ListaFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listaKategorije = (ListView)getView().findViewById(R.id.listaKategorija);
        kategorije = getArguments().getStringArrayList("kategorije");
        kategorijeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, kategorije);
        listaKategorije.setAdapter(kategorijeAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

}
