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
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizAdapter;


public class KvizoviAkt extends AppCompatActivity {

    private static int ADD_QUIZ = 1;
    private static int UPDATE_QUIZ = 2;

    private ArrayList<Kviz> kvizovi;
    private ArrayList<String> kategorijeIme;
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

        kvizovi = new ArrayList<>();
        kvizovi.add(new Kviz("Dodaj Kviz", null, null));

        kategorijeIme = new ArrayList<>();
        kategorijeIme.add("Svi");
        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        spinner.setAdapter(kategorijeAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if(position == kvizovi.size() - 1) {
                    startActivityForResult(intent, ADD_QUIZ);
                }
                else {
                    Kviz k = kvizovi.get(position);
                    intent.putExtra("updateKviz", k);
                    startActivityForResult(intent, UPDATE_QUIZ);
                }
            }
        });
    }


}
