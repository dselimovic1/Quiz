package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizAdapter;


public class KvizoviAkt extends AppCompatActivity {


    private ArrayList<String> kategorije;
    private ArrayList<Kviz> kvizovi;
    private ArrayAdapter<String> adapterKategorije;
    private KvizAdapter kvizAdapter;
    private Spinner spinner;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spPostojeceKategorije);
        list = (ListView)findViewById(R.id.lvKvizovi);

        kategorije = new ArrayList<>();
        kategorije.add("Svi");
        kvizovi = new ArrayList<>();

        adapterKategorije = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorije);
        spinner.setAdapter(adapterKategorije);

        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);
    }


}
