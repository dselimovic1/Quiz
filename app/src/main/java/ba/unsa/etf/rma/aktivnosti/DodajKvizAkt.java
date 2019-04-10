package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import ba.unsa.etf.rma.adapteri.DodanaPitanjaAdapter;
import ba.unsa.etf.rma.adapteri.MogucaPitanjaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;


public class DodajKvizAkt extends AppCompatActivity {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;
    private static int IMPORT_QUIZ = 3;

    private static ArrayList<Pitanje> pitanja = new ArrayList<>();
    private static ArrayList<Kategorija> kategorije = new ArrayList<>();

    private static ArrayList<String> kategorijeIme = new ArrayList<>();
    private ArrayAdapter<String> kategorijeAdapter;
    private static ArrayList<String> dodanaPitanja = new ArrayList<>();
    private DodanaPitanjaAdapter dodanaAdapter;
    private static ArrayList<String> mogucaPitanja = new ArrayList<>();
    private MogucaPitanjaAdapter mogucaAdapter;
    private static ArrayList<String> kvizoviIme = new ArrayList<>();

    private Spinner spinner;
    private ListView dodanaPitanjaList;
    private ListView mogucaPitanjaList;
    private EditText imeKviz;
    private Button sacuvajKviz;
    private Button importujKviz;

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
        importujKviz = (Button)findViewById(R.id.btnImportKviz);

        dodanaAdapter = new DodanaPitanjaAdapter(this,dodanaPitanja);
        dodanaPitanjaList.setAdapter(dodanaAdapter);

        mogucaAdapter = new MogucaPitanjaAdapter(this, mogucaPitanja);
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
            kvizoviIme.remove(trenutni.getNaziv());
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
                    intent.putExtra("pitanja", izvodjiImenaPitanja());
                    startActivityForResult(intent, ADD_QUESTION);
                }
                else {
                    mogucaPitanja.add(dodanaPitanja.get(i));
                    mogucaAdapter.notifyDataSetChanged();
                    dodanaPitanja.remove(i);
                    dodanaAdapter.notifyDataSetChanged();
                }
            }
        });

        mogucaPitanjaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pozicija = mogucaPitanja.size() - 1;
                dodanaPitanja.add(pozicija, mogucaPitanja.get(i));
                dodanaAdapter.notifyDataSetChanged();
                mogucaPitanja.remove(i);
                mogucaAdapter.notifyDataSetChanged();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == kategorijeIme.size() - 1) {
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
                if(validirajNaslov() == false) {
                    imeKviz.setBackgroundColor(Color.RED);
                }
                else {
                    Intent intent = new Intent(DodajKvizAkt.this, KvizoviAkt.class);
                    imeKviz.setBackgroundColor(Color.WHITE);
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
                    kvizoviIme.add(imeKviz.getText().toString());
                    ArrayList<String> sveKategorije = new ArrayList<>(kategorijeIme);
                    sveKategorije.remove("Svi");
                    sveKategorije.remove("Dodaj Kategoriju");
                    intent.putExtra("sveKategorije", sveKategorije);
                    intent.putExtra("noviKviz", trenutni);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        importujKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(intent, IMPORT_QUIZ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
            if(requestCode == ADD_CATEGORY){
                Kategorija k = (Kategorija)data.getSerializableExtra("kategorija");
                kategorije.add(k);
                int pozicija = kategorijeIme.size() - 1;
                kategorijeIme.add(pozicija, k.getNaziv());
                kategorijeAdapter.notifyDataSetChanged();
                spinner.setSelection(pozicija);
            }
            else if(requestCode == ADD_QUESTION) {
                Pitanje p = (Pitanje)data.getSerializableExtra("pitanje");
                pitanja.add(p);
                int pozicija = dodanaPitanja.size() - 1;
                dodanaPitanja.add(pozicija, p.getNaziv());
                dodanaAdapter.notifyDataSetChanged();
            }
            else if(requestCode == IMPORT_QUIZ) {
                if(data != null) {
                    Uri uri = data.getData();
                    izdvojiTekst(uri);
                }
            }
        }
    }

    private Kategorija odrediKategoriju(String s){
        for(Kategorija k : kategorije) {
            if(k.getNaziv().equals(s)) {
                return k;
            }
        }
        return new Kategorija("Svi", "1");
    }

    private ArrayList<String> izvodjiImenaPitanja() {
        ArrayList<String> temp = new ArrayList<>();
        for(Pitanje p : pitanja) temp.add(p.getNaziv());
        return temp;
    }

    private boolean validirajNaslov(){
        if(imeKviz.getText().toString().equals(""))
            return false;
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
        mogucaPitanja.clear();
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

        kategorijeIme.remove("Svi");
        kategorijeIme.remove("Dodaj Kategoriju");
        kategorijeIme.add(0, "Svi");
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

    private String izdvojiTekst(Uri uri) {
        return null;
    }
}
