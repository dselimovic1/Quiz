package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.enumi.Baza;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.taskovi.FilterQuizTask;
import ba.unsa.etf.rma.taskovi.GetListTask;


public class KvizoviAkt extends AppCompatActivity implements DetailFrag.CategoryAdd, ListaFrag.FilterCategory, GetListTask.OnQuestionLoaded, GetListTask.OnCategoryLoaded, FilterQuizTask.OnListFiltered {

    private static int ADD_QUIZ = 1;
    private static int UPDATE_QUIZ = 2;

    private ArrayList<Kviz> kvizovi;
    private ArrayList<Kategorija> kategorije;
    private ArrayList<String> kategorijeIme = new ArrayList<>();

    private ArrayAdapter<String> kategorijeAdapter;
    private KvizAdapter kvizAdapter;

    private Spinner spinner;
    private ListView list;
    private ArrayList<Rang> rangovi;

    private boolean mode = true;
    private boolean firstTime = true;
    private static int lastSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout listPlace = (FrameLayout)findViewById(R.id.listPlace);
        if(listPlace == null) mode = false;

        if(mode == false) {
            spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
            list = (ListView) findViewById(R.id.lvKvizovi);

            new FilterQuizTask(getResources().openRawResource(R.raw.secret),(FilterQuizTask.OnListFiltered)this).execute("Svi");

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    if (position == kvizovi.size() - 1) {
                        intent.putExtra("add", ADD_QUIZ);
                        startActivityForResult(intent, ADD_QUIZ);
                    } else {
                        Kviz k = kvizovi.get(position);
                        intent.putExtra("add", UPDATE_QUIZ);
                        intent.putExtra("updateKviz",k);
                        startActivityForResult(intent, UPDATE_QUIZ);
                    }
                    return true;
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position == kvizovi.size() - 1) return;
                    Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                    intent.putExtra("kviz", kvizovi.get(position));
                    startActivity(intent);
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i != lastSelected) {
                        lastSelected = i;
                        String filter = "";
                        if (i == kategorijeIme.size() - 1) filter = "Svi";
                        else filter = kategorije.get(i).getDocumentID();
                        new FilterQuizTask(getResources().openRawResource(R.raw.secret), KvizoviAkt.this).execute(filter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            ListaFrag listaFrag = (ListaFrag)fm.findFragmentById(R.id.listPlace);
            if (listaFrag == null) {
                listaFrag = new ListaFrag();
            }
            fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
            DetailFrag detailFrag = (DetailFrag)fm.findFragmentById(R.id.detailPlace);
            if(detailFrag == null) {
                detailFrag = new DetailFrag();
            }
            Bundle bundle = new Bundle();
            bundle.putString("filter", "Svi");
            detailFrag.setArguments(bundle);
            fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(mode == false) {
            firstTime = true;
            new FilterQuizTask(getResources().openRawResource(R.raw.secret),(FilterQuizTask.OnListFiltered)this).execute("Svi");
        }
    }

    @Override
    public void onCategoryAdded() {
        FragmentManager fm = getSupportFragmentManager();
        ListaFrag listaFrag = new ListaFrag();
        fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
    }

    @Override
    public void onCategorySelected(String categoryName) {
        FragmentManager fm = getSupportFragmentManager();
        DetailFrag detailFrag = new DetailFrag();
        Bundle bundle = new Bundle();
        bundle.putString("filter", categoryName);
        detailFrag.setArguments(bundle);
        fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
    }

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        MiscHelper.azurirajKvizove(kvizovi, load, kategorije);
        Kviz temp = new Kviz("Dodaj Kviz", null, new Kategorija(null, "671"));
        kvizovi.add(temp);
        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);
    }


    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        kategorijeIme = MiscHelper.izdvojiImenaKategorija(kategorije);
        kategorijeIme.add("Svi");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kategorijeIme);
        spinner.setAdapter(kategorijeAdapter);
        if(firstTime == true) {
            spinner.setSelection(kategorijeIme.size() - 1);
            firstTime = false;
        }
        else {
            spinner.setSelection(lastSelected);
        }
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Baza.TaskType.QUESTION);
    }

    @Override
    public void filterList(ArrayList<Kviz> load) {
        kvizovi = load;
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded)this).execute(Baza.TaskType.CATEGORY);
    }
}
