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
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.sqlite.DatabaseHelper;
import ba.unsa.etf.rma.sqlite.Query;
import ba.unsa.etf.rma.taskovi.FilterQuizTask;
import ba.unsa.etf.rma.taskovi.GetListTask;

public class DetailFrag extends Fragment implements GetListTask.OnCategoryLoaded, GetListTask.OnQuestionLoaded, FilterQuizTask.OnListFiltered {


    private static final int ADD_QUIZ = 1;
    private static final int UPDATE_QUIZ = 2;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;
    private ArrayList<Kviz> kvizovi;
    private GridAdapter adapter;
    private GridView kvizGrid;

    private CategoryAdd categoryAdd;

    private boolean isConnected = false;

    private DatabaseHelper databaseHelper;
    private Query query;

    public DetailFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            categoryAdd = (CategoryAdd)getActivity();
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }

        databaseHelper = new DatabaseHelper(getContext());
        query = new Query(databaseHelper.getWritableDatabase());

        kvizGrid = (GridView)getView().findViewById(R.id.gridKvizovi);
        isConnected = ConnectionHelper.isNetworkAvailable(getContext());
        if(isConnected)  {
            new FilterQuizTask(getActivity().getResources().openRawResource(R.raw.secret), this).execute(getArguments().getString("filter"));
        }
        else {
            long ID = getArguments().getLong("filter");
            if(ID == 0) kvizovi = query.getAllQuizzes();
            else kvizovi = query.getQuizzesByCategory(ID);
            setGridAdapter();
        }
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        categoryAdd.onCategoryAdded();
        isConnected = ConnectionHelper.isNetworkAvailable(getContext());
        if(isConnected) {
            new FilterQuizTask(getActivity().getResources().openRawResource(R.raw.secret), this).execute("Svi");
        }
        else {
            kvizovi = query.getAllQuizzes();
            setGridAdapter();
        }
    }

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        pitanja = load;
        MiscHelper.azurirajKvizove(kvizovi, pitanja, kategorije);
        setGridAdapter();
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Task.TaskType.QUESTION);
    }

    @Override
    public void filterList(ArrayList<Kviz> load) {
        kvizovi = load;
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded) this).execute(Task.TaskType.CATEGORY);
    }

    public void setGridAdapter() {
        kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija(null, "671")));
        adapter = new GridAdapter(getContext(), kvizovi);
        kvizGrid.setAdapter(adapter);
    }

    public interface CategoryAdd {
        void onCategoryAdded();
    }
}
