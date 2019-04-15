package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;

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
        kviz = (Kviz) getIntent().getSerializableExtra("kviz");
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
}
