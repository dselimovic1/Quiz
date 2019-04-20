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
import java.util.ListIterator;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridAdapter;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

import static android.app.Activity.RESULT_OK;

public class DetailFrag extends Fragment {


    private static final int ADD_QUIZ = 1;
    private static final int UPDATE_QUIZ = 2;

    private ArrayList<Kviz> kvizovi;
    private GridAdapter adapter;
    private GridView kvizGrid;

    private CategoryAdd categoryAdd;

    public DetailFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            categoryAdd = (CategoryAdd)getActivity();
        }
        catch (ClassCastException e) {

        }

        kvizGrid = (GridView)getView().findViewById(R.id.gridKvizovi);
        kvizovi = getArguments().getParcelableArrayList("kviz");
        clearQuiz();
        kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija("",  "671")));
        adapter = new GridAdapter(getContext(), kvizovi);
        kvizGrid.setAdapter(adapter);

        kvizGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
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
                return true;
            }
        });

        kvizGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == kvizovi.size() - 1) return;
                Intent intent = new Intent(getActivity(), IgrajKvizAkt.class);
                intent.putExtra("kviz",kvizovi.get(position));
                startActivity(intent);
            }
        });
    }

    private void clearQuiz() {
        Kviz k = new Kviz("Dodaj Kviz", null, null);
        for(ListIterator<Kviz> iterator = kvizovi.listIterator(); iterator.hasNext();) {
            Kviz temp = iterator.next();
            if(k.equals(temp)) iterator.remove();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Kviz k = (Kviz)data.getParcelableExtra("noviKviz");
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
            ArrayList<String> cat = data.getStringArrayListExtra("sveKategorije");
            categoryAdd.onCategoryAdded(cat);
        }
    }

    public interface CategoryAdd{
        void onCategoryAdded(ArrayList<String> categories);
    }

}
