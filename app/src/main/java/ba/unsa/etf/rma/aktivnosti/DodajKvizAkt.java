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
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;


public class DodajKvizAkt extends AppCompatActivity {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;

    private static ArrayList<Pitanje> pitanja = new ArrayList<>();
    private static ArrayList<Kategorija> kategorije = new ArrayList<>();

    private static ArrayList<String> kategorijeIme = new ArrayList<>();
    private ArrayAdapter<String> kategorijeAdapter;
    private static ArrayList<String> dodanaPitanja = new ArrayList<>();
    private ArrayAdapter<String> dodanaAdapter;
    private static ArrayList<String> mogucaPitanja = new ArrayList<>();
    private ArrayAdapter<String> mogucaAdapter;
    private static ArrayList<String> kvizoviIme = new ArrayList<>();

    private Spinner spinner;
    private ListView dodanaPitanjaList;
    private ListView mogucaPitanjaList;
    private EditText imeKviz;
    private Button sacuvajKviz;

    private Kviz trenutni = new Kviz();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        spinner = (Spinner)findViewById(R.id.spKategorije);
        dodanaPitanjaList = (ListView)findViewById(R.id.lvDodanaPitanja);
        mogucaPitanjaList = (ListView)findViewById(R.id.lvMogucaPitanja);
        imeKviz = (EditText)findViewById(R.id.etNaziv);
        sacuvajKviz = (Button)findViewById(R.id.btnDodajKviz);

        dodanaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dodanaPitanja);
        dodanaPitanjaList.setAdapter(dodanaAdapter);

        mogucaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mogucaPitanja);
        mogucaPitanjaList.setAdapter(mogucaAdapter);

        izdvojiKategorije();
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(kategorijeAdapter);

        if(getIntent().getBooleanExtra("add", false) == false) {
            trenutni = (Kviz)getIntent().getSerializableExtra("updateKviz");
            imeKviz.setText(trenutni.getNaziv());
            spinner.setSelection(nadjiPozicijuUSpinneru(trenutni.getKategorija().getNaziv()));
            izdvojiMogucaPitanja();
            izdvojiDodanaPitanja();
        }
        else {
            trenutni = null;
            imeKviz.setText("");
            spinner.setSelection(0);
            izdvojiMogucaPitanja();
            izdvojiDodanaPitanja();
        }

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
                else {
                    spinner.setSelection(i);
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
                    Intent intent = new Intent(DodajKvizAkt.this, KvizoviAkt.class);
                    spinner.setBackgroundColor(Color.WHITE);
                    imeKviz.setBackgroundColor(Color.WHITE);
                    dodanaPitanjaList.setBackgroundColor(Color.WHITE);
                    if(trenutni == null)
                        trenutni = new Kviz(imeKviz.getText().toString(), izdvojiPitanja(dodanaPitanja),
                                odrediKategoriju(kategorijeIme.get(spinner.getSelectedItemPosition())));
                    else {
                        trenutni.setNaziv(imeKviz.getText().toString());
                        trenutni.setPitanja(izdvojiPitanja(dodanaPitanja));
                        trenutni.setKategorija(odrediKategoriju(kategorijeIme.get(spinner.getSelectedItemPosition())));
                        int pozicija = getIntent().getIntExtra("pozicija", 0);
                        intent.putExtra("pozicija", pozicija);
                    }
                    ArrayList<String> sveKategorije = new ArrayList<>(kategorijeIme);
                    sveKategorije.remove("");
                    sveKategorije.remove("Dodaj Kategoriju");
                    intent.putExtra("sveKategorije", sveKategorije);
                    intent.putExtra("noviKviz", trenutni);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
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

    private int nadjiPozicijuUSpinneru(String s){
        for(int i = 0; i < kategorijeIme.size(); i++) {
            if(s.equals(kategorijeIme.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Pitanje> izdvojiPitanja(ArrayList<String> ime) {
        ArrayList<Pitanje> temp = new ArrayList<>();
        for(Pitanje p : pitanja) {
            for(String s : dodanaPitanja) {
                if(s.equals(p.getNaziv())) {
                    temp.add(p);
                }
            }
        }
        return temp;
    }

    private void izdvojiMogucaPitanja() {
        ArrayList<Pitanje> temp = new ArrayList<>(pitanja);
        mogucaPitanja.clear();
        if(trenutni != null) temp.removeAll(trenutni.getPitanja());
        for(Pitanje p : temp) mogucaPitanja.add(p.getNaziv());
        mogucaAdapter.notifyDataSetChanged();
    }

    private void izdvojiDodanaPitanja() {
        ArrayList<Pitanje> temp = new ArrayList<>();
        dodanaPitanja.clear();
        if(trenutni != null) temp.addAll(trenutni.getPitanja());
        for(Pitanje p : temp) dodanaPitanja.add(p.getNaziv());
        dodanaPitanja.add("Dodaj Pitanje");
        dodanaAdapter.notifyDataSetChanged();
    }

    private void izdvojiKategorije() {

        kategorijeIme.remove("");
        kategorijeIme.remove("Dodaj Kategoriju");
        kategorijeIme.add(0, "");
        kategorijeIme.add("Dodaj Kategoriju");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DodajKvizAkt.this, KvizoviAkt.class);
        intent.putExtra("back", true);
        ArrayList<String> temp = new ArrayList<>(kategorijeIme);
        temp.remove(temp.size() - 1);
        temp.remove(0);
        intent.putExtra("kategorije", temp);
        startActivity(intent);
    }
}
