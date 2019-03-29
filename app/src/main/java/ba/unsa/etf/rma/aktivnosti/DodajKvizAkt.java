package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;


public class DodajKvizAkt extends AppCompatActivity {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;

    private ArrayList<String> kategorijeIme;
    private ArrayAdapter<String> kategorijeAdapter;
    private ArrayList<String> dodanaPitanja;
    private ArrayAdapter<String> dodanaAdapter;
    private ArrayList<String> mogucaPitanja;
    private ArrayAdapter<String> mogucaAdapter;
    private ArrayList<String> kvizoviIme;

    private Spinner spinner;
    private ListView dodanaPitanjaList;
    private ListView mogucaPitanjaList;
    private EditText imeKviz;
    private Button sacuvajKviz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        pitanja = new ArrayList<>();
        kategorije = new ArrayList<>();
        kvizoviIme = new ArrayList<>();

        spinner = (Spinner)findViewById(R.id.spKategorije);
        dodanaPitanjaList = (ListView)findViewById(R.id.lvDodanaPitanja);
        mogucaPitanjaList = (ListView)findViewById(R.id.lvMogucaPitanja);
        imeKviz = (EditText)findViewById(R.id.etNaziv);
        sacuvajKviz = (Button)findViewById(R.id.btnDodajKviz);

        dodanaPitanja = new ArrayList<>();
        dodanaPitanja.add("Dodaj Pitanje");
        dodanaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dodanaPitanja);
        dodanaPitanjaList.setAdapter(dodanaAdapter);

        mogucaPitanja = new ArrayList<>();
        mogucaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mogucaPitanja);
        mogucaPitanjaList.setAdapter(mogucaAdapter);

        kategorijeIme = new ArrayList<>();
        kategorijeIme.add("");
        kategorijeIme.add("Dodaj kategoriju");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(kategorijeAdapter);

        dodanaPitanjaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == dodanaPitanja.size() - 1){
                    Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    startActivityForResult(intent, ADD_QUESTION);
                }
                else {
                    mogucaPitanja.add(0, dodanaPitanja.get(i));
                    mogucaAdapter.notifyDataSetChanged();
                    dodanaPitanja.remove(i);
                    dodanaAdapter.notifyDataSetChanged();
                }
            }
        });

        mogucaPitanjaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dodanaPitanja.add(0, mogucaPitanja.get(i));
                dodanaAdapter.notifyDataSetChanged();
                mogucaPitanja.remove(i);
                mogucaAdapter.notifyDataSetChanged();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0 && i == kategorijeIme.size() - 1) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    startActivityForResult(intent, ADD_CATEGORY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sacuvajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner.getSelectedItemPosition() == 0) {
                    spinner.setBackgroundColor(Color.RED);
                }
                else if(validirajNaslov() == false) {
                    imeKviz.setBackgroundColor(Color.RED);
                }
                else if (dodanaPitanja.size() == 1) {
                    dodanaPitanjaList.setBackgroundColor(Color.RED);
                }
                else {
                    spinner.setBackgroundColor(Color.WHITE);
                    imeKviz.setBackgroundColor(Color.WHITE);
                    dodanaPitanjaList.setBackgroundColor(Color.WHITE);

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ADD_CATEGORY){
            Kategorija k = (Kategorija)data.getSerializableExtra("kategorija");
            kategorije.add(k);
            kategorijeIme.add(1, k.getNaziv());
            kategorijeAdapter.notifyDataSetChanged();
            spinner.setSelection(1);
        }
        else if(requestCode == ADD_QUESTION) {
            Pitanje p = (Pitanje)data.getSerializableExtra("pitanje");
            pitanja.add(p);
            dodanaPitanja.add(0, p.getNaziv());
            dodanaAdapter.notifyDataSetChanged();
        }
    }

    private Kategorija odrediKategoriju(String s){
        for(Kategorija k : kategorije) {
            if(k.getNaziv().equals(s)) {
                return k;
            }
        }
        return null;
    }

    private boolean validirajNaslov(){
        for(String s : kvizoviIme) {
            if(s.equals(imeKviz.getText().toString()))
                return false;
        }
        return true;
    }

}
