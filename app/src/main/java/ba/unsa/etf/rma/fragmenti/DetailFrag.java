package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class DetailFrag extends Fragment {


    private ArrayList<Kviz> kvizovi;
    private GridView kvizGrid;

    public DetailFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        kvizGrid = (GridView)getView().findViewById(R.id.gridKvizovi);

        kvizovi = getArguments().getParcelableArrayList("kviz");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

}
