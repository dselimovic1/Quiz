package ba.unsa.etf.rma.fragmenti;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridAdapter;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.helperi.LocalDBHelper;
import ba.unsa.etf.rma.helperi.MiscHelper;
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

public class DetailFrag extends Fragment implements GetListTask.OnCategoryLoaded, GetListTask.OnQuestionLoaded, FilterQuizTask.OnListFiltered, GetListTask.OnRangLoaded {


    private static final int ADD_QUIZ = 1;
    private static final int UPDATE_QUIZ = 2;
    private static final int PERMISSION_REQUEST = 0;

    private ArrayList<Pitanje> pitanja;
    private ArrayList<Kategorija> kategorije;
    private ArrayList<Kviz> kvizovi;
    private GridAdapter adapter;
    private GridView kvizGrid;

    private CategoryAdd categoryAdd;

    private boolean isConnected = false;
    private boolean isPlayable = true;
    private long nextEvent = 0;
    private int numOfQuestions = 0;

    private DatabaseHelper databaseHelper;
    private Query query;

    public DetailFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            categoryAdd = (CategoryAdd)getActivity();
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }

        databaseHelper = new DatabaseHelper(getContext());
        query = new Query(databaseHelper.getWritableDatabase());

        kvizGrid = (GridView)getView().findViewById(R.id.gridKvizovi);
        isConnected = ConnectionHelper.isNetworkAvailable(getContext());
        if(isConnected)  {
            new FilterQuizTask(getActivity().getResources().openRawResource(R.raw.secret), this).execute(getArguments().getString("filter"));
        }
        else {
            long ID = getArguments().getLong("filter");
            if(ID == 0) kvizovi = query.getAllQuizzes();
            else kvizovi = query.getQuizzesByCategory(ID);
            setGridAdapter();
        }
        kvizGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), DodajKvizAkt.class);
                if(position == kvizovi.size() - 1) {
                    intent.putExtra("add", ADD_QUIZ);
                    startActivityForResult(intent, ADD_QUIZ);
                }
                else {
                    Kviz k = kvizovi.get(position);
                    intent.putExtra("add", UPDATE_QUIZ);
                    intent.putExtra("updateKviz",(Parcelable) k);
                    intent.putExtra("pozicija", position);
                    startActivityForResult(intent, UPDATE_QUIZ);
                }
                return true;
            }
        });

        kvizGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == kvizovi.size() - 1) return;
                Intent intent = new Intent(getActivity(), IgrajKvizAkt.class);
                intent.putExtra("kviz",kvizovi.get(position));
                startActivity(intent);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        categoryAdd.onCategoryAdded();
        isConnected = ConnectionHelper.isNetworkAvailable(getContext());
        if(isConnected) {
            new FilterQuizTask(getActivity().getResources().openRawResource(R.raw.secret), this).execute("Svi");
        }
        else {
            kvizovi = query.getAllQuizzes();
            setGridAdapter();
        }
    }

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        pitanja = load;
        MiscHelper.azurirajKvizove(kvizovi, pitanja, kategorije);
        ArrayList<Kviz> temp = kvizovi;
        setGridAdapter();
        updateDatabase(load, temp);
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded) this).execute(Task.TaskType.QUESTION);
    }

    @Override
    public void filterList(ArrayList<Kviz> load) {
        kvizovi = load;
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded) this).execute(Task.TaskType.CATEGORY);
    }

    public void setGridAdapter() {
        kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija(null, "671")));
        adapter = new GridAdapter(getContext(), kvizovi);
        kvizGrid.setAdapter(adapter);
    }

    @Override
    public void loadAllRang(ArrayList<Rang> load) {
        ArrayList<Rang> rangList = query.getAllRangLists();
        updateFirestore(load, rangList);
    }

    public void updateDatabase(ArrayList<Pitanje> load, ArrayList<Kviz> kvizovi) {
        ArrayList<Kviz> localQuiz = query.getAllQuizzes();
        ArrayList<Kviz> kvizoviUpdate = new ArrayList<>(kvizovi);
        ArrayList<Kviz> localUpdate = new ArrayList<>(localQuiz);
        ArrayList<Pitanje> addQuestion = (ArrayList<Pitanje>) LocalDBHelper.getEntriesToAdd(load, query.getAllQuestions());
        ArrayList<Kategorija> addCategory = (ArrayList<Kategorija>) LocalDBHelper.getEntriesToAdd(kategorije, query.getAllCategories());
        ArrayList<Kviz> addQuiz = (ArrayList<Kviz>) LocalDBHelper.getEntriesToAdd(kvizovi, localQuiz);
        ArrayList<Kviz> updateQuiz = LocalDBHelper.getUpdatedEntries(kvizoviUpdate, localUpdate);
        query.addQuestions(addQuestion);
        query.addCategories(addCategory);
        query.addQuizzes(addQuiz);
        updateQuiz = query.setEntriesToUpdate(updateQuiz);
        query.updateQuizzes(updateQuiz);
        new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnRangLoaded) this).execute(Task.TaskType.RANGLIST);
    }

    public void updateFirestore(ArrayList<Rang> load, ArrayList<Rang> rangList) {
        ArrayList<Rang> entriesToAddLocal = LocalDBHelper.rangListsToAdd(load, rangList);
        ArrayList<Rang> entriesToUpdateLocal = LocalDBHelper.rangListsToUpdate(load, rangList, false);
        ArrayList<Rang> entriesToAdd = LocalDBHelper.rangListsToAdd(rangList, load);
        ArrayList<Rang> entriesToUpdate = LocalDBHelper.rangListsToUpdate(rangList, load, true);
        for(Rang add: entriesToAdd)
            new AddItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(add);
        for(Rang update : entriesToUpdate)
            new UpdateItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(update);
        query.addRanglists(entriesToAddLocal);
        query.updateRanglists(entriesToUpdateLocal);
    }

    public interface CategoryAdd {
        void onCategoryAdded();
    }

    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
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

    public void checkEvents() {
        ContentResolver cr = getActivity().getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] selectionArgs = new String[]{Long.toString(Calendar.getInstance().getTimeInMillis())};
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor cursor = cr.query(uri, INSTANCE_PROJECTION, SELECTION, selectionArgs, SORT_ORDER);
        if(cursor != null && cursor.getCount() >= 1) {
            long ID = MiscHelper.getFirstEventTime(cursor);
            cursor.close();
            nextEvent = MiscHelper.getDifferenceInMinutes(ID);
            if(nextEvent < numOfQuestions / 2) {
                isPlayable = false;
                return;
            }

        }
        isPlayable = true;
    }

    @Override
    public void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
