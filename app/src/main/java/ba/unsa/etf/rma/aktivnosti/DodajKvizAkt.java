package ba.unsa.etf.rma.aktivnosti;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.DodanaPitanjaAdapter;
import ba.unsa.etf.rma.adapteri.MogucaPitanjaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.singleton.Baza;


public class DodajKvizAkt extends AppCompatActivity {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;
    private static int IMPORT_QUIZ = 3;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;

    private ArrayList<String> kategorijeIme;
    private ArrayAdapter<String> kategorijeAdapter;
    private ArrayList<String> dodanaPitanja;
    private DodanaPitanjaAdapter dodanaAdapter;
    private ArrayList<String> mogucaPitanja;
    private MogucaPitanjaAdapter mogucaAdapter;
    private ArrayList<String> kvizoviIme;

    private Spinner spinner;
    private ListView dodanaPitanjaList;
    private ListView mogucaPitanjaList;
    private EditText imeKviz;
    private Button sacuvajKviz;
    private Button importujKviz;

    private Baza baza;

    private Kviz trenutni = new Kviz();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        baza = Baza.getInstance();
        pitanja = baza.dajPitanja();
        kategorije = baza.dajKategorije();
        kategorijeIme = baza.dajImenaKategorija();
        kvizoviIme = baza.dajImenaKvizova();

        spinner = (Spinner)findViewById(R.id.spKategorije);
        dodanaPitanjaList = (ListView)findViewById(R.id.lvDodanaPitanja);
        mogucaPitanjaList = (ListView)findViewById(R.id.lvMogucaPitanja);
        imeKviz = (EditText)findViewById(R.id.etNaziv);

        importujKviz = (Button)findViewById(R.id.btnImportKviz);

        if(getIntent().getIntExtra("add", 0) == 2) {
            trenutni = (Kviz)getIntent().getParcelableExtra("updateKviz");
            imeKviz.setText(trenutni.getNaziv());
            spinner.setSelection(nadjiPozicijuUSpinneru(trenutni.getKategorija().getNaziv()));
            kvizoviIme.remove(trenutni.getNaziv());
        }
        else {
            trenutni = null;
            imeKviz.setText("");
            spinner.setSelection(0);
        }

        dodanaPitanja = baza.dajImenaPitanjaKviza(trenutni);
        dodanaAdapter = new DodanaPitanjaAdapter(this,dodanaPitanja);
        dodanaPitanjaList.setAdapter(dodanaAdapter);

        mogucaPitanja = baza.dajMogucaPitanjaKviza(trenutni);
        mogucaAdapter = new MogucaPitanjaAdapter(this, mogucaPitanja);
        mogucaPitanjaList.setAdapter(mogucaAdapter);

        izdvojiKategorije();
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
                int pozicija = dodanaPitanja.size() - 1;
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
                    imeKviz.setBackgroundColor(Color.WHITE);
                    if(trenutni == null) {
                        trenutni = new Kviz(imeKviz.getText().toString(), izdvojiPitanja(dodanaPitanja), odrediKategoriju(kategorijeIme.get(spinner.getSelectedItemPosition())));
                        baza.dodajKviz(trenutni);
                    }
                    else {
                        trenutni.setNaziv(imeKviz.getText().toString());
                        trenutni.setPitanja(izdvojiPitanja(dodanaPitanja));
                        trenutni.setKategorija(odrediKategoriju(kategorijeIme.get(spinner.getSelectedItemPosition())));
                        int pozicija = getIntent().getIntExtra("pozicija", 0);
                        baza.dodajKviz(pozicija, trenutni);
                    }
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
                int pozicija = kategorijeIme.size() - 1;
                spinner.setSelection(pozicija);
            }
            else if(requestCode == ADD_QUESTION) {
            }
            else if(requestCode == IMPORT_QUIZ) {
                if(data != null) {
                    Uri uri = data.getData();
                    ArrayList<String> temp = izdvojiTekst(uri);
                    if(temp.size() == 0) {
                        prikaziAlertDialog("Datoteka kviza kojeg importujete nema ispravan format!");
                        return;
                    }
                    try {
                        String[] quizData = temp.get(0).split(",");
                        if (quizData.length != 3) {
                            prikaziAlertDialog("Datoteka kviza kojeg importujete nema ispravan format!");
                            return;
                        }
                        if (validirajImeKvizaImport(quizData[0]) == false) {
                            prikaziAlertDialog("Kviz kojeg importujete već postoji!");
                            return;
                        }
                        if (Integer.parseInt(quizData[2]) != temp.size() - 1) {
                            prikaziAlertDialog("Kviz kojeg importujete ima neispravan broj pitanja!");
                            return;
                        }
                        for (int i = 1; i <= Integer.parseInt(quizData[2]); i++) {
                            String[] questionData = temp.get(i).split(",");
                            int brojOdogovora = Integer.parseInt(questionData[1]);
                            if (brojOdogovora + 3 != questionData.length) {
                                prikaziAlertDialog("Kviz kojeg importujete ima neispravan broj odgovora!");
                                return;
                            }
                            int index = Integer.parseInt(questionData[2]);
                            if (index < 0 || index >= brojOdogovora) {
                                prikaziAlertDialog("Kviz kojeg importujete ima neispravan index tačnog odgovora!");
                                return;
                            }
                        }
                        ArrayList<String> checkPitanje = new ArrayList<>();
                        for (int i = 1; i <= Integer.parseInt(quizData[2]); i++) {
                            Pitanje p = izdvojiPitanje(temp.get(i).split(","));
                            if (duplicateCheck(p.getOdgovori()) == false) {
                                prikaziAlertDialog("Kviz kojeg importujete nije ispravan postoji ponavljanje odgovora!");
                                return;
                            }
                            checkPitanje.add(p.getNaziv());
                        }
                        if (duplicateCheck(checkPitanje) == false) {
                            prikaziAlertDialog("Kviz nije ispravan postoje dva pitanja sa istim nazivom!");
                            return;
                        }
                        izvdojiSvaPitanja(quizData, temp);
                        imeKviz.setText(quizData[0]);
                        izdvojiKategorijuImport(quizData);
                    }
                    catch (NumberFormatException e) {
                        prikaziAlertDialog("Datoteka kviza kojeg importujete nema ispravan format!");
                    }
                }
            }
        }
    }

    private void izdvojiKategorijuImport(String[] quizData) {
        String imeKategorije = quizData[1];
        if(nadjiPozicijuUSpinneru(imeKategorije) != -1) {
            spinner.setSelection(nadjiPozicijuUSpinneru(imeKategorije));
        }
        else {
            Kategorija k = new Kategorija(imeKategorije,"2");
            kategorije.add(k);
            kategorijeIme.add(k.getNaziv());
            izdvojiKategorije();
            kategorijeAdapter.notifyDataSetChanged();
            spinner.setSelection(nadjiPozicijuUSpinneru(k.getNaziv()));
        }
    }

    private void izvdojiSvaPitanja(String[] quizData, ArrayList<String> temp) {
        dodanaPitanja.clear();
        pitanja.clear();
        for(int i = 0; i < Integer.parseInt(quizData[2]); i++) {
            Pitanje p = izdvojiPitanje(temp.get(i + 1).split(","));
            pitanja.add(p);
            dodanaPitanja.add(p.getNaziv());
        }
        dodanaPitanja.add("Dodaj Pitanje");
        dodanaAdapter.notifyDataSetChanged();
    }

    private boolean duplicateCheck(ArrayList<String> temp) {
        Set<String> duplicateCheck = new HashSet<>();
        for(String s : temp) {
            if(!duplicateCheck.add(s)) return false;
        }
        return true;
    }

    private Pitanje izdvojiPitanje(String[] s) {
        Pitanje p = new Pitanje();
        p.setNaziv(s[0]);
        p.setTekstPitanja(s[0]);
        ArrayList<String> odgovori = new ArrayList<>();
        int brojOdgovora = Integer.parseInt(s[1]);
        int indexTacnog = Integer.parseInt(s[2]);
        for(int i = 0; i < brojOdgovora; i++) odgovori.add(s[i + 3]);
        p.setOdgovori(odgovori);
        p.setTacan(odgovori.get(indexTacnog));
        return p;
    }

    private boolean validirajImeKvizaImport(String ime) {
        for(String s : kvizoviIme) {
            if(s.equals(ime)) {
                return false;
            }
        }
        return true;
    }

    private Kategorija odrediKategoriju(String s){
        for(Kategorija k : kategorije) {
            if(k.getNaziv().equals(s)) {
                return k;
            }
        }
        return new Kategorija("Svi", "1");
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


    private void izdvojiKategorije() {

        kategorijeIme.remove("Svi");
        kategorijeIme.remove("Dodaj Kategoriju");
        kategorijeIme.add(0, "Svi");
        kategorijeIme.add("Dodaj Kategoriju");
    }


    private ArrayList<String> izdvojiTekst(Uri uri) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }
            inputStream.close();
            bufferedReader.close();
        } catch (FileNotFoundException e ) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return list;
    }

    private void prikaziAlertDialog(String message) {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.cancel();
            }
        });
        alert.show();
    }
}
