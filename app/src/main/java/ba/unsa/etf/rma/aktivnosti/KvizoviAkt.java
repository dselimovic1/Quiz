package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.ListIterator;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.singleton.Baza;


public class KvizoviAkt extends AppCompatActivity implements DetailFrag.CategoryAdd, ListaFrag.FilterCategory {

    private static int ADD_QUIZ = 1;
    private static int UPDATE_QUIZ = 2;

    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<String> kategorijeIme = new ArrayList<>();
    private ArrayAdapter<String> kategorijeAdapter;
    private KvizAdapter kvizAdapter;
    private Spinner spinner;
    private ListView list;

    private Baza baza;

    private boolean mode = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baza = Baza.getInstance();

        FrameLayout listPlace = (FrameLayout)findViewById(R.id.listPlace);
        if(listPlace == null) mode = false;

        boolean back = getIntent().getBooleanExtra("back", false);

        if(mode == false) {
            spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
            list = (ListView) findViewById(R.id.lvKvizovi);

            kvizAdapter = new KvizAdapter(this, kvizovi);
            list.setAdapter(kvizAdapter);
            kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, kategorijeIme);
            kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinner.setAdapter(kategorijeAdapter);
            clearQuiz();
            clearCategory();
            if (back == true) {
                kategorijeIme.clear();
                kategorijeIme.addAll(0, getIntent().getStringArrayListExtra("kategorije"));
                kategorijeIme.add("Svi");
                kategorijeAdapter.notifyDataSetChanged();
                spinner.setSelection(kategorijeIme.size() - 1);
            }
            kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija("ok", Integer.toString(671))));
            kategorijeAdapter.notifyDataSetChanged();
            kategorijeIme.add("Svi");
            kategorijeAdapter.notifyDataSetChanged();

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    if (position == kvizovi.size() - 1) {
                        intent.putExtra("add", ADD_QUIZ);
                        intent.putExtra("mode", mode);
                        startActivityForResult(intent, ADD_QUIZ);
                    } else {
                        Kviz k = kvizovi.get(position);
                        intent.putExtra("add", UPDATE_QUIZ);
                        intent.putExtra("updateKviz",k);
                        intent.putExtra("pozicija",position);
                        startActivityForResult(intent, UPDATE_QUIZ);
                        intent.putExtra("mode", mode);
                    }
                    return true;
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position == kvizovi.size() - 1) return;
                    Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                    intent.putExtra("kviz", (Parcelable) kvizovi.get(position));
                    startActivity(intent);
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String s = spinner.getSelectedItem().toString();
                    if (s.equals("Svi")) {
                        kvizAdapter = new KvizAdapter(KvizoviAkt.this, kvizovi);
                        list.setAdapter(kvizAdapter);
                    } else {
                        kvizAdapter = new KvizAdapter(KvizoviAkt.this, filterListe(s));
                        list.setAdapter(kvizAdapter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            if(back == false) {
                ListaFrag listaFrag = (ListaFrag)fm.findFragmentById(R.id.listPlace);
                if (listaFrag == null) {
                    listaFrag = new ListaFrag();
                }
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("kategorije", kategorijeIme);
                listaFrag.setArguments(bundle1);
                fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
                DetailFrag detailFrag = (DetailFrag)fm.findFragmentById(R.id.detailPlace);
                if(detailFrag == null) {
                    detailFrag = new DetailFrag();
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("kviz", kvizovi);
                detailFrag.setArguments(bundle);
                fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
            }
            else {
                ListaFrag listaFragBack = new ListaFrag();
                Bundle bund = new Bundle();
                kategorijeIme.clear();
                kategorijeIme.addAll(0, getIntent().getStringArrayListExtra("kategorije"));
                bund.putStringArrayList("kategorije", kategorijeIme);
                listaFragBack.setArguments(bund);
                fm.beginTransaction().replace(R.id.listPlace, listaFragBack).commit();
                DetailFrag detailFrag = new DetailFrag();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("kviz", kvizovi);
                detailFrag.setArguments(bundle);
                fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
            }
        }
    }

    private void clearQuiz() {
        Kviz k = new Kviz("Dodaj Kviz", null, null);
        for(ListIterator<Kviz> iterator = kvizovi.listIterator(); iterator.hasNext();) {
            Kviz temp = iterator.next();
            if(k.equals(temp)) iterator.remove();
        }
    }

    private void clearCategory() {
        String s = "Svi";
        for(ListIterator<String> iterator = kategorijeIme.listIterator(); iterator.hasNext();){
            String temp = iterator.next();
            if(temp.equals(s)) iterator.remove();
        }
    }

    private ArrayList<Kviz> filterListe(String s) {
        ArrayList<Kviz> lista = new ArrayList<>();
        for(Kviz k : kvizovi) {
            if(k.getKategorija().getNaziv().equals(s)) lista.add(k);
        }
        lista.add(new Kviz("Dodaj Kviz", null, new Kategorija("",Integer.toString(671))));
        return lista;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Kviz k = (Kviz) data.getParcelableExtra("noviKviz");
            if (requestCode == ADD_QUIZ) {
                int pozicija = kvizovi.size() - 1;
                if (pozicija < 0) pozicija = 0;
                kvizovi.add(pozicija, k);
            } else if (requestCode == UPDATE_QUIZ) {
                int pozicija = data.getIntExtra("pozicija", 0);
                kvizovi.set(pozicija, k);
            }
            if(mode == false) {
                ArrayList<String> sveKategorije = data.getStringArrayListExtra("sveKategorije");
                kategorijeIme.clear();
                kategorijeIme.addAll(0, sveKategorije);
                kategorijeIme.add("Svi");
                kategorijeAdapter.notifyDataSetChanged();
                int pozicija = kategorijeAdapter.getCount() - 1;
                if (pozicija < 0) pozicija = 0;
                spinner.setSelection(pozicija);
                kvizAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCategoryAdded(ArrayList<String> categories) {
        FragmentManager fm = getSupportFragmentManager();
        ListaFrag listaFrag = new ListaFrag();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("kategorijes", categories);
        listaFrag.setArguments(bundle);
        fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
    }

    @Override
    public void onCategorySelected(String categoryName) {
        ArrayList<Kviz> kvizFilter = new ArrayList<>();
        if(categoryName.equals("Svi") == false)
            kvizFilter = filterListe(categoryName);
        else
            kvizFilter = kvizovi;
        FragmentManager fm = getSupportFragmentManager();
        DetailFrag detailFrag = new DetailFrag();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("kviz", kvizFilter);
        detailFrag.setArguments(bundle);
        fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
    }

}
