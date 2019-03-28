package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;


public class KvizoviAkt extends AppCompatActivity {


    private ArrayList<String> kategorije;
    private ArrayList<Kviz> kvizovi;
    private ArrayAdapter<String> adapterKategorije;
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

        adapterKategorije = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,kategorije);
        spinner.setAdapter(adapterKategorije);
    }

    private ArrayList<String> filterKategorije(String naziv) {
        if(naziv == null) return kategorije;
        ArrayList<String> temp = new ArrayList();

        for(int i = 0; i < kategorije.size(); i++) {
            if(naziv.equals(kategorije.get(i))) {
                temp.add(kategorije.get(i));
            }
        }
        return temp;
    }
}
