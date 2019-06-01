package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.enumi.Baza;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.taskovi.AddItemTask;
import ba.unsa.etf.rma.taskovi.GetListTask;
import ba.unsa.etf.rma.taskovi.UpdateItemTask;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.SendData, GetListTask.OnRangLoaded {

    private Kviz kviz;
    private Rang.Par current = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

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
        new GetListTask(getResources().openRawResource(R.raw.secret), this).execute(Baza.TaskType.RANGLIST);
        current = par;
    }

    @Override
    public void loadAllRang(ArrayList<Rang> load) {
        Rang rang = MiscHelper.traziRang(load, kviz.getNaziv());
        if(rang == null) {
            rang = new Rang(kviz.getNaziv());
            rang.dodajRezultat(current);
            new AddItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.RANGLIST).execute(rang);
        }
        else {
            rang.dodajRezultat(current);
            new UpdateItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.RANGLIST).execute(rang);
        }
        current = null;
        Bundle bundle = new Bundle();
        bundle.putSerializable("rang", rang);
        RangLista rangListaFragment = new RangLista();
        rangListaFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.pitanjePlace, rangListaFragment).commit();
    }
}
