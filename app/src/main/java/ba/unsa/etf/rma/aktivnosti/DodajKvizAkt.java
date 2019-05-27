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

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.DodanaPitanjaAdapter;
import ba.unsa.etf.rma.adapteri.MogucaPitanjaAdapter;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.helperi.QuizParser;
import ba.unsa.etf.rma.helperi.WrongParseException;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.singleton.Baza;
import ba.unsa.etf.rma.taskovi.AddItemTask;
import ba.unsa.etf.rma.taskovi.GetListTask;
import ba.unsa.etf.rma.taskovi.UpdateItemTask;

import static ba.unsa.etf.rma.helperi.QuizParser.izdvojiPitanje;


public class DodajKvizAkt extends AppCompatActivity implements GetListTask.OnCategoryLoaded, GetListTask.OnQuestionLoaded {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;
    private static int IMPORT_QUIZ = 3;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;

    private ArrayList<String> kategorijeIme;
    private ArrayAdapter<String> kategorijeAdapter;
    private static ArrayList<String> dodanaPitanja = new ArrayList<>();
    private DodanaPitanjaAdapter dodanaAdapter;
    private static ArrayList<String> mogucaPitanja = new ArrayList<>();
    private MogucaPitanjaAdapter mogucaAdapter;
    private ArrayList<String> kvizoviIme;

    private Spinner spinner;
    private ListView dodanaPitanjaList;
    private ListView mogucaPitanjaList;
    private EditText imeKviz;
    private Button sacuvajKviz;
    private Button importujKviz;


    private Baza baza = Baza.getInstance();
    private Kviz trenutni = new Kviz();
    private boolean firstTime = true;
    private String lastCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        spinner = (Spinner) findViewById(R.id.spKategorije);
        dodanaPitanjaList = (ListView) findViewById(R.id.lvDodanaPitanja);
        mogucaPitanjaList = (ListView) findViewById(R.id.lvMogucaPitanja);
        imeKviz = (EditText) findViewById(R.id.etNaziv);
        sacuvajKviz = (Button) findViewById(R.id.btnDodajKviz);
        importujKviz = (Button) findViewById(R.id.btnImportKviz);

        ucitajKvizove();

        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded)this).execute(Baza.TaskType.QUESTION);
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded)this).execute(Baza.TaskType.CATEGORY);

        dodanaPitanjaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == dodanaPitanja.size() - 1) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    intent.putExtra("pitanja", MiscHelper.izdvojiImenaPitanja(pitanja));
                    startActivityForResult(intent, ADD_QUESTION);
                } else {
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
                if (i == kategorijeIme.size() - 1) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    intent.putExtra("kategorije", MiscHelper.izdvojiImenaKategorija(kategorije));
                    startActivityForResult(intent, ADD_CATEGORY);
                } else {
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
                if (validirajNaslov() == false) {
                    imeKviz.setBackgroundColor(Color.RED);
                } else {
                    imeKviz.setBackgroundColor(Color.WHITE);
                    if (trenutni == null) {
                        trenutni = new Kviz(imeKviz.getText().toString(), MiscHelper.izdvojiPitanja(pitanja, dodanaPitanja),
                                MiscHelper.odrediKategoriju(kategorije, kategorijeIme.get(spinner.getSelectedItemPosition())));
                        //baza.dodajKviz(trenutni);
                        new AddItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.QUIZ).execute(trenutni);
                    } else {
                        trenutni.setNaziv(imeKviz.getText().toString());
                        trenutni.setPitanja(MiscHelper.izdvojiPitanja(pitanja, dodanaPitanja));
                        trenutni.setKategorija(MiscHelper.odrediKategoriju(kategorije, kategorijeIme.get(spinner.getSelectedItemPosition())));
                        //int pozicija = getIntent().getIntExtra("pozicija", 0);
                        //baza.azurirajKviz(pozicija, trenutni);
                        new UpdateItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.QUIZ).execute(trenutni);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        kvizoviIme = getIntent().getStringArrayListExtra("kvizovi");
        if (getIntent().getIntExtra("add", 0) == 2) {
            trenutni = (Kviz) getIntent().getParcelableExtra("updateKviz");
            imeKviz.setText(trenutni.getNaziv());
            kvizoviIme.remove(trenutni.getNaziv());
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_CATEGORY) {
                lastCategory = data.getStringExtra("kategorija");
                new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded) this).execute(Baza.TaskType.CATEGORY);
            } else if (requestCode == ADD_QUESTION) {
                new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Baza.TaskType.QUESTION);
                int position = dodanaPitanja.size() - 1;
                if (position < 0) position = 0;
                dodanaPitanja.add(position, data.getStringExtra("pitanje"));
                dodanaAdapter.notifyDataSetChanged();
            } else if (requestCode == IMPORT_QUIZ) {
                if (data != null) {
                    Uri uri = data.getData();
                    ArrayList<String> temp = izdvojiTekst(uri);
                    try {
                        QuizParser.doParse(temp, kvizoviIme);
                        String[] quizData = temp.get(0).split(",");
                        izvdojiSvaPitanja(quizData, temp);
                        imeKviz.setText(quizData[0]);
                        izdvojiKategorijuImport(quizData);
                    }
                    catch (NumberFormatException e) {
                        prikaziAlertDialog("Datoteka kviza kojeg importujete nema ispravan format!");
                    }
                    catch (WrongParseException exception) {
                        prikaziAlertDialog(exception.getMessage());
                    }
                }
            }
        }
    }

    private void postaviAdapterKategorije() {
        kategorijeIme.add(0, "Svi");
        kategorijeIme.add("Dodaj kategoriju");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(kategorijeAdapter);
        if (trenutni != null)
            spinner.setSelection(MiscHelper.nadjiPozicijuUSpinneru(kategorijeIme, trenutni.getKategorija().getNaziv()));
        if (lastCategory != null) {
            spinner.setSelection(MiscHelper.nadjiPozicijuUSpinneru(kategorijeIme, lastCategory));
            lastCategory = null;
        }
    }

    public void izdvojiDodanaPitanja() {
        dodanaPitanja.clear();
        if(trenutni != null) dodanaPitanja.addAll(0, trenutni.dajImenaPitanja());
        dodanaPitanja.add("Dodaj Pitanje");
        dodanaAdapter = new DodanaPitanjaAdapter(this, dodanaPitanja);
        dodanaPitanjaList.setAdapter(dodanaAdapter);
    }

    private void izdvojiMogucaPitanja() {
        mogucaPitanja = MiscHelper.izdvojiImenaPitanja(pitanja);
        mogucaPitanja.removeAll(dodanaPitanja);
        mogucaAdapter = new MogucaPitanjaAdapter(this, mogucaPitanja);
        mogucaPitanjaList.setAdapter(mogucaAdapter);
    }

    private void ucitajKvizove() {
        kvizoviIme = getIntent().getStringArrayListExtra("kvizovi");
        if (getIntent().getIntExtra("add", 0) == 2) {
            trenutni = (Kviz) getIntent().getParcelableExtra("updateKviz");
            imeKviz.setText(trenutni.getNaziv());
            kvizoviIme.remove(trenutni.getNaziv());
        }
        else {
            trenutni = null;
            imeKviz.setText("");
            spinner.setSelection(0);
        }
    }

    private void izdvojiKategorijuImport(String[] quizData) {
        String imeKategorije = quizData[1];
        int pozicija = MiscHelper.nadjiPozicijuUSpinneru(kategorijeIme, imeKategorije);
        if (pozicija != -1) {
            spinner.setSelection(pozicija);
        } else {
            Kategorija k = new Kategorija(imeKategorije, "2");
            new AddItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.CATEGORY).execute(k);
            kategorijeIme.add(kategorijeIme.size() - 1, k.getNaziv());
            kategorijeAdapter.notifyDataSetChanged();
            spinner.setSelection(MiscHelper.nadjiPozicijuUSpinneru(kategorijeIme, k.getNaziv()));
        }
    }

    private void izvdojiSvaPitanja(String[] quizData, ArrayList<String> temp) {
        dodanaPitanja.clear();
        for (int i = 0; i < Integer.parseInt(quizData[2]); i++) {
            Pitanje p = izdvojiPitanje(temp.get(i + 1).split(","));
            pitanja.add(p);
            dodanaPitanja.add(p.getNaziv());
        }
        dodanaPitanja.add("Dodaj Pitanje");
        dodanaAdapter.notifyDataSetChanged();
    }

    private boolean validirajNaslov() {
        if (imeKviz.getText().toString().equals(""))
            return false;
        for (String s : kvizoviIme) {
            if (s.equals(imeKviz.getText().toString()))
                return false;
        }
        return true;
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
        } catch (FileNotFoundException e) {
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

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        pitanja = load;
        if(firstTime) {
            izdvojiDodanaPitanja();
            firstTime = false;
        }
        izdvojiMogucaPitanja();
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        kategorijeIme = MiscHelper.izdvojiImenaKategorija(kategorije);
        postaviAdapterKategorije();
    }
}
