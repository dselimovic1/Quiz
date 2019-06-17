package ba.unsa.etf.rma.aktivnosti;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.helperi.LocalDBHelper;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.helperi.ViewHelper;
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


public class KvizoviAkt extends AppCompatActivity implements DetailFrag.CategoryAdd, ListaFrag.FilterCategory, GetListTask.OnQuestionLoaded, GetListTask.OnCategoryLoaded, FilterQuizTask.OnListFiltered, GetListTask.OnRangLoaded {

    private static int ADD_QUIZ = 1;
    private static int UPDATE_QUIZ = 2;
    private static int PERMISSION_REQUEST = 0;

    private ArrayList<Kviz> kvizovi;
    private ArrayList<Kategorija> kategorije;
    private ArrayList<String> kategorijeIme = new ArrayList<>();

    private ArrayAdapter<String> kategorijeAdapter;
    private KvizAdapter kvizAdapter;

    private Spinner spinner;
    private ListView list;
    private ArrayList<Rang> rangovi;

    private boolean mode = true;
    private boolean firstTime = true;
    private static int lastSelected = -1;
    private long nextEvent = 0;
    private int numOfQuestions = 0;
    private boolean isPlayable = true;
    private  boolean isConnected = false;

    private LinearLayout layout;

    private static final String[] INSTANCE_PROJECTION = new String[]{CalendarContract.Events.DTSTART};
    private static final String SELECTION = INSTANCE_PROJECTION[0] + " >= ?";
    private static final String SORT_ORDER = INSTANCE_PROJECTION[0] + " ASC";

    private DatabaseHelper databaseHelper;
    private Query queryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout listPlace = (FrameLayout) findViewById(R.id.listPlace);
        if (listPlace == null) mode = false;
        if (mode == false) {
            spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
            list = (ListView) findViewById(R.id.lvKvizovi);
            databaseHelper = new DatabaseHelper(this);
            queryHelper = new Query(databaseHelper.getWritableDatabase());

            isConnected = ConnectionHelper.isNetworkAvailable(this);
            if(isConnected) {
                ViewHelper.setInvisible(spinner, list);
                layout = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
                new FilterQuizTask(getResources().openRawResource(R.raw.secret), (FilterQuizTask.OnListFiltered) this, layout).execute("Svi");
            }
            else {
                readFromDatabase();
            }
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    if (position == kvizovi.size() - 1) {
                        intent.putExtra("add", ADD_QUIZ);
                        startActivityForResult(intent, ADD_QUIZ);
                    } else {
                        Kviz k = kvizovi.get(position);
                        intent.putExtra("add", UPDATE_QUIZ);
                        intent.putExtra("updateKviz", k);
                        startActivityForResult(intent, UPDATE_QUIZ);
                    }
                    return true;
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position == kvizovi.size() - 1) return;
                    numOfQuestions = kvizovi.get(position).getPitanja().size();
                    getPermission();
                    if (isPlayable) {
                        Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                        intent.putExtra("kviz", kvizovi.get(position));
                        startActivity(intent);
                    } else {
                        showAlertDialog("Imate događaj u kalendaru za " + nextEvent + " minuta!");
                    }
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != lastSelected) {
                        lastSelected = i;
                        if(isConnected) {
                            String filter = "";
                            if (i == kategorijeIme.size() - 1) filter = "Svi";
                            else filter = kategorije.get(i).getDocumentID();
                            new FilterQuizTask(getResources().openRawResource(R.raw.secret), KvizoviAkt.this, layout).execute(filter);
                        }
                        else {
                            long ID = 0;
                            if(i < kategorije.size())
                                ID = kategorije.get(i).getID();
                            if(ID == 0) kvizovi = queryHelper.getAllQuizzes();
                            else kvizovi = queryHelper.getQuizzesByCategory(ID);
                            setQuizAdapter();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            FragmentManager fm = getSupportFragmentManager();
            ListaFrag listaFrag = (ListaFrag) fm.findFragmentById(R.id.listPlace);
            if (listaFrag == null) {
                listaFrag = new ListaFrag();
            }
            fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
            DetailFrag detailFrag = (DetailFrag) fm.findFragmentById(R.id.detailPlace);
            if (detailFrag == null) {
                detailFrag = new DetailFrag();
            }
            Bundle bundle = new Bundle();
            bundle.putString("filter", "Svi");
            bundle.putLong("filter", 0);
            detailFrag.setArguments(bundle);
            fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mode == false) {
            firstTime = true;
            isConnected = ConnectionHelper.isNetworkAvailable(this);
            if(isConnected) {
                ViewHelper.setInvisible(spinner, list);
                new FilterQuizTask(getResources().openRawResource(R.raw.secret), (FilterQuizTask.OnListFiltered) this, layout).execute("Svi");
            }
            else {
                readFromDatabase();
            }
        }
    }

    public void readFromDatabase() {
        ViewHelper.setVisible(spinner, list);
        kvizovi = queryHelper.getAllQuizzes();
        setQuizAdapter();
        setCategoryAdapter(queryHelper.getAllCategories());
    }

    @Override
    public void onCategoryAdded() {
        FragmentManager fm = getSupportFragmentManager();
        ListaFrag listaFrag = new ListaFrag();
        fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
    }

    @Override
    public void onCategorySelected(String categoryName) {
        FragmentManager fm = getSupportFragmentManager();
        DetailFrag detailFrag = new DetailFrag();
        Bundle bundle = new Bundle();
        bundle.putString("filter", categoryName);
        detailFrag.setArguments(bundle);
        fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
    }

    @Override
    public void onCategorySelected(long ID) {
        FragmentManager fm = getSupportFragmentManager();
        DetailFrag detailFrag = new DetailFrag();
        Bundle bundle = new Bundle();
        bundle.putLong("filter", ID);
        detailFrag.setArguments(bundle);
        fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
    }

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        MiscHelper.azurirajKvizove(kvizovi, load, kategorije);
        ArrayList<Kviz> temp2 = new ArrayList<>(kvizovi);
        setQuizAdapter();
        layout.setVisibility(View.GONE);
        ViewHelper.setVisible(spinner, list);
        updateDatabase(load, temp2);
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        setCategoryAdapter(load);
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Task.TaskType.QUESTION);
    }

    private void setQuizAdapter() {
        Kviz temp = new Kviz("Dodaj Kviz", null, new Kategorija(null, "671"));
        kvizovi.add(temp);
        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);
    }

    private void setCategoryAdapter(ArrayList<Kategorija> list) {
        kategorije = list;
        kategorijeIme = MiscHelper.izdvojiImenaKategorija(kategorije);
        kategorijeIme.add("Svi");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kategorijeIme);
        spinner.setAdapter(kategorijeAdapter);
        if (firstTime == true) {
            spinner.setSelection(kategorijeIme.size() - 1);
            firstTime = false;
        } else {
            spinner.setSelection(lastSelected);
        }
    }

    @Override
    public void filterList(ArrayList<Kviz> load) {
        kvizovi = load;
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded) this).execute(Task.TaskType.CATEGORY);
    }

    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
        else {
            checkEvents();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkEvents();
            }
        }
    }

    private void showAlertDialog(String message) {
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

    public void checkEvents() {
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] selectionArgs = new String[]{Long.toString(Calendar.getInstance().getTimeInMillis())};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Nemate pristup kalendaru!", Toast.LENGTH_LONG);
            return;
        }
        Cursor cursor = cr.query(uri, INSTANCE_PROJECTION, SELECTION, selectionArgs, SORT_ORDER);
        if(cursor != null && cursor.getCount() >= 1) {
            long ID = MiscHelper.getFirstEventTime(cursor);
            cursor.close();
            nextEvent = MiscHelper.getDifferenceInMinutes(ID) + 1;
            if(nextEvent < numOfQuestions / 2) {
                isPlayable = false;
                return;
            }

        }
        isPlayable = true;
    }

    public void updateDatabase(ArrayList<Pitanje> load, ArrayList<Kviz> kvizovi) {
        ArrayList<Kviz> localQuiz = queryHelper.getAllQuizzes();
        ArrayList<Kviz> kvizoviUpdate = new ArrayList<>(kvizovi);
        ArrayList<Kviz> localUpdate = new ArrayList<>(localQuiz);
        ArrayList<Pitanje> addQuestion = (ArrayList<Pitanje>) LocalDBHelper.getEntriesToAdd(load, queryHelper.getAllQuestions());
        ArrayList<Kategorija> addCategory = (ArrayList<Kategorija>) LocalDBHelper.getEntriesToAdd(kategorije, queryHelper.getAllCategories());
        ArrayList<Kviz> addQuiz = (ArrayList<Kviz>) LocalDBHelper.getEntriesToAdd(kvizovi, localQuiz);
        ArrayList<Kviz> updateQuiz = LocalDBHelper.getUpdatedEntries(kvizoviUpdate, localUpdate);
        queryHelper.addQuestions(addQuestion);
        queryHelper.addCategories(addCategory);
        queryHelper.addQuizzes(addQuiz);
        updateQuiz = queryHelper.setEntriesToUpdate(updateQuiz);
        queryHelper.updateQuizzes(updateQuiz);
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnRangLoaded) this).execute(Task.TaskType.RANGLIST);
    }

    public void updateFirestore(ArrayList<Rang> load, ArrayList<Rang> rangList) {
        ArrayList<Rang> entriesToAddLocal = LocalDBHelper.rangListsToAdd(load, rangList);
        ArrayList<Rang> entriesToUpdateLocal = LocalDBHelper.rangListsToUpdate(load, rangList, false);
        ArrayList<Rang> entriesToAdd = LocalDBHelper.rangListsToAdd(rangList, load);
        ArrayList<Rang> entriesToUpdate = LocalDBHelper.rangListsToUpdateFir(rangList, load, true);
        for(Rang add: entriesToAdd)
            new AddItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(add);
        for(Rang update : entriesToUpdate)
            new UpdateItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(update);
        queryHelper.addRanglists(entriesToAddLocal);
        queryHelper.updateRanglists(entriesToUpdateLocal);
    }

    @Override
    public void loadAllRang(ArrayList<Rang> load) {
        ArrayList<Rang> rangList = queryHelper.getAllRangLists();
        updateFirestore(load, rangList);
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
