package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.OdgovoriAdapter;
import ba.unsa.etf.rma.enumi.Baza;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.taskovi.AddItemTask;
import ba.unsa.etf.rma.taskovi.GetListTask;

public class DodajPitanjeAkt extends AppCompatActivity implements GetListTask.OnQuestionLoaded {

    private ArrayList<String> odgovori = new ArrayList<>();
    private OdgovoriAdapter odgovoriAdapter;
    private ArrayList<String> pitanja;

    private ListView odgovoriList;
    private EditText nazivText;
    private EditText odgovorText;
    private Button dodaj;
    private Button dodajTacan;
    private Button sacuvajPitanje;

    private boolean validacija = true;
    private Pitanje p = new Pitanje();
    private int pozicijaTacnog = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        odgovoriList = (ListView)findViewById(R.id.lvOdgovori);
        nazivText = (EditText)findViewById(R.id.etNaziv);
        odgovorText = (EditText)findViewById(R.id.etOdgovor);
        dodaj = (Button)findViewById(R.id.btnDodajOdgovor);
        dodajTacan = (Button)findViewById(R.id.btnDodajTacan);
        sacuvajPitanje = (Button)findViewById(R.id.btnDodajPitanje);

        odgovoriAdapter = new OdgovoriAdapter(this,odgovori);
        odgovoriList.setAdapter(odgovoriAdapter);


        odgovoriList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == pozicijaTacnog) {
                    dodajTacan.setEnabled(true);
                    pozicijaTacnog = -1;
                    p.setTacan(null);
                }
                odgovoriAdapter.remove(i);
                p.getOdgovori().remove(i);
            }
        });

        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(odgovorText.getText().toString().equals("")) return;
                odgovoriAdapter.add(odgovorText.getText().toString());
                p.dodajOdgovor(odgovorText.getText().toString());
                odgovorText.setText("");
            }
        });


        dodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(odgovorText.getText().toString().equals("")) return;
                odgovoriAdapter.add(odgovorText.getText().toString());
                pozicijaTacnog = odgovoriAdapter.getCount() - 1;
                odgovoriAdapter.setPozicijaTacnog(pozicijaTacnog);
                p.dodajOdgovor(odgovorText.getText().toString());
                p.setTacan(odgovorText.getText().toString());
                odgovorText.setText("");
                view.setEnabled(false);
            }
        });

        sacuvajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetListTask(getResources().openRawResource(R.raw.secret), (GetListTask.OnQuestionLoaded)DodajPitanjeAkt.this).execute(Baza.TaskType.QUESTION);
            }
        });
    }

    private void validirajNaziv() {
        if(nazivText.getText().toString().equals("")) {
            validacija = false;
            nazivText.setBackgroundColor(Color.RED);
            return;
        }
        for(String s : pitanja) {
            if(s.equals(nazivText.getText().toString())) {
                validacija = false;
                nazivText.setBackgroundColor(Color.RED);
                return;
            }
        }
        validacija = true;
        nazivText.setBackgroundColor(Color.WHITE);
    }

    private void validirajOdgovore() {
        if(p.getTacan() == null) {
            odgovorText.setBackgroundColor(Color.RED);
            validacija = false;
            return;
        }
        odgovorText.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void loadAllQuestion(ArrayList<Pitanje> load) {
        pitanja = MiscHelper.izdvojiImenaPitanja(load);
        validirajNaziv();
        validirajOdgovore();
        if(validacija) {
            Intent sendQuestion = new Intent(DodajPitanjeAkt.this, DodajKvizAkt.class);
            nazivText.setBackgroundColor(Color.WHITE);
            odgovorText.setBackgroundColor(Color.WHITE);
            p.setNaziv(nazivText.getText().toString());
            p.setTekstPitanja(nazivText.getText().toString());
            new AddItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.QUESTION).execute(p);
            sendQuestion.putExtra("pitanje", p.getNaziv());
            setResult(RESULT_OK, sendQuestion);
            finish();
        }
    }
}
