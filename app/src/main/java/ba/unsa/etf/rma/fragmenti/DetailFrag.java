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
import ba.unsa.etf.rma.enumi.Baza;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.taskovi.GetListTask;

public class DetailFrag extends Fragment implements GetListTask.OnCategoryLoaded, GetListTask.OnQuestionLoaded, GetListTask.OnQuizLoaded {


    private static final int ADD_QUIZ = 1;
    private static final int UPDATE_QUIZ = 2;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;
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
            e.printStackTrace();
        }

        kvizGrid = (GridView)getView().findViewById(R.id.gridKvizovi);
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Baza.TaskType.QUESTION);

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
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Baza.TaskType.QUESTION);
    }

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        pitanja = load;
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded) this).execute(Baza.TaskType.CATEGORY);
    }

    @Override
    public void loadAllQuiz(ArrayList<Kviz> load) {
        kvizovi = load;
        MiscHelper.azurirajKvizove(kvizovi, pitanja, kategorije);
        kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija(null, "671")));
        adapter = new GridAdapter(getContext(), kvizovi);
        kvizGrid.setAdapter(adapter);
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnQuizLoaded) this).execute(Baza.TaskType.QUIZ);
    }

    public interface CategoryAdd {
        void onCategoryAdded();
    }
}
