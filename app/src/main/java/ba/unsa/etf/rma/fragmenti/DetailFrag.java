package ba.unsa.etf.rma.fragmenti;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridAdapter;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.klase.Kviz;

import static android.app.Activity.RESULT_OK;

public class DetailFrag extends Fragment {


    private static final int ADD_QUIZ = 1;
    private static final int UPDATE_QUIZ = 2;

    private ArrayList<Kviz> kvizovi;
    private GridAdapter adapter;
    private GridView kvizGrid;

    public DetailFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        kvizGrid = (GridView)getView().findViewById(R.id.gridKvizovi);
        kvizovi = getArguments().getParcelableArrayList("kviz");
        adapter = new GridAdapter(getContext(), kvizovi);
        kvizGrid.setAdapter(adapter);

        kvizGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), DodajKvizAkt.class);
                if(position == kvizovi.size() - 1) {
                    intent.putExtra("add", ADD_QUIZ);
                    startActivityForResult(intent, ADD_QUIZ);
                }
                else {
                    Kviz k = kvizovi.get(position);
                    intent.putExtra("add", UPDATE_QUIZ);
                    intent.putExtra("updateKviz",(Parcelable) k);
                    intent.putExtra("pozicija", position);
                    startActivityForResult(intent, UPDATE_QUIZ);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Kviz k = (Kviz)data.getSerializableExtra("noviKviz");
            if(requestCode == ADD_QUIZ) {
                int pozicija = kvizovi.size() - 1;
                if(pozicija < 0 ) pozicija = 0;
                kvizovi.add(pozicija, k);
                adapter.notifyDataSetChanged();
            }
            else if(requestCode == UPDATE_QUIZ) {
                int pozicija = data.getIntExtra("pozicija", 0);
                kvizovi.set(pozicija, k);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
