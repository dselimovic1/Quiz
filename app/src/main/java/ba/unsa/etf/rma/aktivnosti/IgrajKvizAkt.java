package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Rang;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.SendData{

    private Kviz kviz;

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
    public void showRangList(String quizName, Rang.Par par) {
        Bundle arg = new Bundle();
        arg.putString("imeKviza", quizName);
        RangLista fragment = new RangLista();
        fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().replace(R.id.pitanjePlace, fragment).commit();
    }
}
