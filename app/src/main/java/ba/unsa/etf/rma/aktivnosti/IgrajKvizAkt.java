package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.sqlite.DatabaseHelper;
import ba.unsa.etf.rma.sqlite.Query;
import ba.unsa.etf.rma.taskovi.AddItemTask;
import ba.unsa.etf.rma.taskovi.GetListTask;
import ba.unsa.etf.rma.taskovi.UpdateItemTask;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.SendData, GetListTask.OnRangLoaded {

    private Kviz kviz;
    private Rang.Par current = null;
    private ArrayList<Rang> rangliste;

    private DatabaseHelper databaseHelper;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        databaseHelper = new DatabaseHelper(this);
        query = new Query(databaseHelper.getWritableDatabase());

        FragmentManager fm = getSupportFragmentManager();
        InformacijeFrag informacijeFrag = (InformacijeFrag)fm.findFragmentById(R.id.informacijePlace);
        PitanjeFrag pitanjeFrag = (PitanjeFrag)fm.findFragmentById(R.id.pitanjePlace);
        if(informacijeFrag == null) {
            informacijeFrag = new InformacijeFrag();
            fm.beginTransaction().replace(R.id.informacijePlace, informacijeFrag).commit();
        }
        if(pitanjeFrag == null) {
            pitanjeFrag = new PitanjeFrag();
            fm.beginTransaction().replace(R.id.pitanjePlace, pitanjeFrag).commit();
        }
        kviz = (Kviz) getIntent().getParcelableExtra("kviz");
        setAlarm(kviz.getPitanja().size());
    }

    @Override
    public void onQuestionAnswered(int correct, int remainder, int total) {
        Bundle arg = new Bundle();
        arg.putInt("tacni", correct);
        arg.putInt("preostali", remainder);
        arg.putInt("ukupno", total);
        InformacijeFrag informacijeFrag = new InformacijeFrag();
        informacijeFrag.setArguments(arg);
        getSupportFragmentManager().beginTransaction().replace(R.id.informacijePlace, informacijeFrag).commit();
    }

    @Override
    public void showRangList(Rang.Par par) {
        current = par;
        if(ConnectionHelper.isNetworkAvailable(this))
            new GetListTask(getResources().openRawResource(R.raw.secret), this).execute(Task.TaskType.RANGLIST);
        else {
            rangliste = query.getAllRangLists();
            Rang rang = MiscHelper.traziRang(rangliste, kviz.getNaziv());
            if(rang == null) {
                rang = new Rang(kviz.getNaziv());
                rang.dodajRezultat(current);;
                query.addRangList(rang);
                query.addResult(rang, current);
            }
            else {
                rang.dodajRezultat(current);
                query.addResult(rang, current);
            }
            showRangList(rang);
        }
    }

    @Override
    public void loadAllRang(ArrayList<Rang> load) {
        rangliste = load;
        Rang rang = MiscHelper.traziRang(load, kviz.getNaziv());
        if(rang == null) {
            rang = new Rang(kviz.getNaziv());
            rang.dodajRezultat(current);
            new AddItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(rang);
        }
        else {
            rang.dodajRezultat(current);
            new UpdateItemTask(getResources().openRawResource(R.raw.secret), Task.TaskType.RANGLIST).execute(rang);
        }
        showRangList(rang);
    }

    public void setAlarm(int numOfQuestions) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(true);
        calendar.add(Calendar.MINUTE, numOfQuestions / 2);
        Intent setAlarmActivity = new Intent(AlarmClock.ACTION_SET_ALARM);
        setAlarmActivity.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(calendar.HOUR_OF_DAY));
        setAlarmActivity.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(calendar.MINUTE));
        setAlarmActivity.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        startActivity(setAlarmActivity);
    }

    public void showRangList(Rang rang) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("rang", rang);
        RangLista rangListaFragment = new RangLista();
        rangListaFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.pitanjePlace, rangListaFragment).commit();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
