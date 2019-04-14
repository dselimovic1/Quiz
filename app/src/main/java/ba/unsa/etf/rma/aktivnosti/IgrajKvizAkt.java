package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;

public class IgrajKvizAkt extends AppCompatActivity {

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
    }
}
