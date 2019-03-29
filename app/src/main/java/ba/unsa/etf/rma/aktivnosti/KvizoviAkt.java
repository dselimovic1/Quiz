package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.KategorijaAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizAdapter;
import ba.unsa.etf.rma.klase.Pitanje;


public class KvizoviAkt extends AppCompatActivity {


    private ArrayList<Kategorija> kategorije;
    private ArrayList<Kviz> kvizovi;
    private ArrayList<Pitanje> pitanja;
    private KategorijaAdapter kategorijaAdapter;
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
        kategorije.add(new Kategorija("Svi" , null));
        kvizovi = new ArrayList<>();
        kvizovi.add(new Kviz("Dodaj Kviz", null,null));
        pitanja = new ArrayList<>();

        kategorijaAdapter = new KategorijaAdapter(this, kategorije);
        spinner.setAdapter(kategorijaAdapter);

        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if(position == kvizovi.size() - 1) {
                    intent.putExtra("novi", true);
                }
                else {
                    ArrayList<Pitanje> temp = new ArrayList<>(pitanja);
                    temp.removeAll(kvizovi.get(position).getPitanja());
                    intent.putExtra("novi", false);
                    intent.putExtra("dodanaPitanja", kvizovi.get(position).getPitanja());
                    intent.putExtra("mogucaPitanja", temp);
                    intent.putExtra("nazivKviza", kvizovi.get(position).getNaziv());
                }
                intent.putExtra("kategorije", kategorije);
                startActivity(intent);
            }
        });
    }


}
