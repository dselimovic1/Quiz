package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizAdapter;


public class KvizoviAkt extends AppCompatActivity {

    private static int ADD_QUIZ = 1;
    private static int UPDATE_QUIZ = 2;

    private static ArrayList<Kviz> kvizovi = new ArrayList<>();
    private static ArrayList<String> kategorijeIme = new ArrayList<>();
    private ArrayAdapter<String> kategorijeAdapter;
    private KvizAdapter kvizAdapter;
    private Spinner spinner;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        list = (ListView) findViewById(R.id.lvKvizovi);

        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        spinner.setAdapter(kategorijeAdapter);

        boolean back = getIntent().getBooleanExtra("back", false);
        if(back == true) {
            kategorijeIme.addAll(0, getIntent().getStringArrayListExtra("kategorije"));
            kategorijeAdapter.notifyDataSetChanged();
        }
        else {
            kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija("ok",Integer.toString(671))));
            kvizAdapter.notifyDataSetChanged();
            kategorijeIme.add("Svi");
            kategorijeAdapter.notifyDataSetChanged();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if(position == kvizovi.size() - 1) {
                    intent.putExtra("add", true);
                    startActivityForResult(intent, ADD_QUIZ);
                }
                else {
                    Kviz k = kvizovi.get(position);
                    intent.putExtra("add", false);
                    intent.putExtra("updateKviz", k);
                    intent.putExtra("pozicija", position);
                    startActivityForResult(intent, UPDATE_QUIZ);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Kviz k = (Kviz) data.getSerializableExtra("noviKviz");
            if (requestCode == ADD_QUIZ) {
                int pozicija = kvizovi.size() - 1;
                if (pozicija < 0) pozicija = 0;
                kvizovi.add(pozicija, k);
            } else if (requestCode == UPDATE_QUIZ) {
                int pozicija = data.getIntExtra("pozicija", 0);
                kvizovi.set(pozicija, k);
            }
            ArrayList<String> sveKategorije = data.getStringArrayListExtra("sveKategorije");
            kategorijeIme.addAll(0, sveKategorije);
            kategorijeAdapter.notifyDataSetChanged();
            int pozicija = kategorijeAdapter.getCount() - 1;
            if (pozicija < 0) pozicija = 0;
            spinner.setSelection(pozicija);
            kvizAdapter.notifyDataSetChanged();
        }
    }
}
