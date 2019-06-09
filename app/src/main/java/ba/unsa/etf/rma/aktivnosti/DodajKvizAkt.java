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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.DodanaPitanjaAdapter;
import ba.unsa.etf.rma.adapteri.MogucaPitanjaAdapter;
import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.helperi.QuizParser;
import ba.unsa.etf.rma.izuzeci.WrongParseException;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.sqlite.DatabaseHelper;
import ba.unsa.etf.rma.sqlite.Query;
import ba.unsa.etf.rma.taskovi.AddItemTask;
import ba.unsa.etf.rma.taskovi.FilterQuizTask;
import ba.unsa.etf.rma.taskovi.GetListTask;
import ba.unsa.etf.rma.taskovi.UpdateItemTask;

import static ba.unsa.etf.rma.helperi.QuizParser.izdvojiPitanje;


public class DodajKvizAkt extends AppCompatActivity implements GetListTask.OnCategoryLoaded, GetListTask.OnQuestionLoaded, FilterQuizTask.OnListFiltered, GetListTask.OnRangLoaded {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;
    private static int IMPORT_QUIZ = 3;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;
    private ArrayList<Rang> rangliste;

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


    private Kviz trenutni = null;

    private boolean firstTime = true;
    private String lastCategoryAdded = null;
    private static String lastCategoryChosen = null;

    private DatabaseHelper databaseHelper;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        databaseHelper = new DatabaseHelper(this);
        query = new Query(databaseHelper.getWritableDatabase());

        spinner = (Spinner) findViewById(R.id.spKategorije);
        dodanaPitanjaList = (ListView) findViewById(R.id.lvDodanaPitanja);
        mogucaPitanjaList = (ListView) findViewById(R.id.lvMogucaPitanja);
        imeKviz = (EditText) findViewById(R.id.etNaziv);
        sacuvajKviz = (Button) findViewById(R.id.btnDodajKviz);
        importujKviz = (Button) findViewById(R.id.btnImportKviz);

        if(ConnectionHelper.isNetworkAvailable(this)) new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnRangLoaded) this).execute(Task.TaskType.RANGLIST);
        else readFromDatabase();

        dodanaPitanjaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == dodanaPitanja.size() - 1) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
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
                    startActivityForResult(intent, ADD_CATEGORY);
                } else {
                    spinner.setSelection(i);
                    lastCategoryChosen = kategorijeIme.get(spinner.getSelectedItemPosition());
                    if(trenutni != null)
                    trenutni.setKategorija(MiscHelper.odrediKategoriju(kategorije, kategorijeIme.get(spinner.getSelectedItemPosition())));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sacuvajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionHelper.isNetworkAvailable(DodajKvizAkt.this))
                    new FilterQuizTask(getResources().openRawResource(R.raw.secret), DodajKvizAkt.this).execute("Svi");
                else
                    Toast.makeText(DodajKvizAkt.this, "Uređaj nije konektovan na internet!", Toast.LENGTH_LONG).show();
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
        if(ConnectionHelper.isNetworkAvailable(this)) new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnRangLoaded) this).execute(Task.TaskType.RANGLIST);
        else readFromDatabase();
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_CATEGORY) {
                lastCategoryAdded = data.getStringExtra("kategorija");
            } else if (requestCode == ADD_QUESTION) {
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
                        izdvojiKategoriju(quizData);
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

    public void  readFromDatabase() {
        kategorije = query.getAllCategories();
        pitanja = query.getAllQuestions();
        if (getIntent().getIntExtra("add", 0) == 2) {
            trenutni = (Kviz) getIntent().getParcelableExtra("updateKviz");
            imeKviz.setText(trenutni.getNaziv());
        }
        izdvojiDodanaPitanja();
        izdvojiMogucaPitanja();
        postaviAdapterKategorije();
    }

    private void postaviAdapterKategorije() {
        kategorijeIme = MiscHelper.izdvojiImenaKategorija(kategorije);
        kategorijeIme.add(0, "Svi");
        kategorijeIme.add("Dodaj kategoriju");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(kategorijeAdapter);
        if (trenutni != null) {
            spinner.setSelection(MiscHelper.odrediIndeks(kategorijeIme, trenutni.getKategorija().getNaziv()));
        }
        else if(lastCategoryChosen != null) {
            spinner.setSelection(MiscHelper.odrediIndeks(kategorijeIme, lastCategoryChosen));
        }
        else {
            spinner.setSelection(0);
        }
        if (lastCategoryAdded != null) {
            spinner.setSelection(MiscHelper.odrediIndeks(kategorijeIme, lastCategoryAdded));
            lastCategoryAdded = null;
        }

    }

    private void izdvojiDodanaPitanja() {
        if(firstTime) {
            dodanaPitanja.clear();
            if (trenutni != null) dodanaPitanja.addAll(0, trenutni.dajImenaPitanja());
            dodanaPitanja.add("Dodaj Pitanje");
            dodanaAdapter = new DodanaPitanjaAdapter(this, dodanaPitanja);
            dodanaPitanjaList.setAdapter(dodanaAdapter);
            firstTime = false;
        }
    }

    private void izdvojiMogucaPitanja() {
        mogucaPitanja = MiscHelper.izdvojiImenaPitanja(pitanja);
        mogucaPitanja.removeAll(dodanaPitanja);
        mogucaAdapter = new MogucaPitanjaAdapter(this, mogucaPitanja);
        mogucaPitanjaList.setAdapter(mogucaAdapter);
    }

    private void izdvojiKategoriju(String[] quizData) {
        String imeKategorije = quizData[1];
        int pozicija = MiscHelper.odrediIndeks(kategorijeIme, imeKategorije);
        if (pozicija != -1) {
            spinner.setSelection(pozicija);
        } else {
            Kategorija k = new Kategorija(imeKategorije, "2");
            new AddItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.CATEGORY).execute(k);
            kategorijeIme.add(kategorijeIme.size() - 1, k.getNaziv());
            kategorijeAdapter.notifyDataSetChanged();
            spinner.setSelection(MiscHelper.odrediIndeks(kategorijeIme, k.getNaziv()));
        }
    }

    private void izvdojiSvaPitanja(String[] quizData, ArrayList<String> temp) {
        ArrayList<Pitanje> provjera = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(quizData[2]); i++) {
            Pitanje p = izdvojiPitanje(temp.get(i + 1).split(","));
            provjera.add(p);
        }
        QuizParser.checkDuplicatesAfterImport(provjera, pitanja);
        dodanaPitanja.clear();
        dodanaPitanja.addAll(MiscHelper.izdvojiImenaPitanja(provjera));
        dodanaPitanja.add("Dodaj Pitanje");
        dodanaAdapter.notifyDataSetChanged();
        for(Pitanje p : provjera) new AddItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.QUESTION).execute(p);
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
        izdvojiDodanaPitanja();
        izdvojiMogucaPitanja();
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        postaviAdapterKategorije();
    }

    @Override
    public void loadAllRang(ArrayList<Rang> load) {
        rangliste = load;
        if (getIntent().getIntExtra("add", 0) == 2) {
            trenutni = (Kviz) getIntent().getParcelableExtra("updateKviz");
            imeKviz.setText(trenutni.getNaziv());
        }
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded)this).execute(Task.TaskType.QUESTION);
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded)this).execute(Task.TaskType.CATEGORY);
    }

    @Override
    public void filterList(ArrayList<Kviz> load) {
        kvizoviIme = MiscHelper.izvdojiImenaKvizova(load);
        if(trenutni != null) kvizoviIme.remove(trenutni.getNaziv());
        if (validirajNaslov() == false) {
            imeKviz.setBackgroundColor(Color.RED);
        } else {
            imeKviz.setBackgroundColor(Color.WHITE);
            if (trenutni == null) {
                trenutni = new Kviz(imeKviz.getText().toString(), MiscHelper.izdvojiPitanja(pitanja, dodanaPitanja),
                        MiscHelper.odrediKategoriju(kategorije, kategorijeIme.get(spinner.getSelectedItemPosition())));
                new AddItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.QUIZ).execute(trenutni);
            } else {
                Rang rang = MiscHelper.traziRang(rangliste, trenutni.getNaziv());
                if(rang != null)  {
                    rang.setImeKviza(imeKviz.getText().toString());
                    new UpdateItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(rang);
                }
                trenutni.setNaziv(imeKviz.getText().toString());
                trenutni.setPitanja(MiscHelper.izdvojiPitanja(pitanja, dodanaPitanja));
                trenutni.setKategorija(MiscHelper.odrediKategoriju(kategorije, kategorijeIme.get(spinner.getSelectedItemPosition())));
                new UpdateItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.QUIZ).execute(trenutni);
            }
            lastCategoryChosen = null;
            finish();
        }
    }
}
