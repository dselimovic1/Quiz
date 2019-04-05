package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.OdgovoriAdapter;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajPitanjeAkt extends AppCompatActivity {

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

        pitanja = getIntent().getStringArrayListExtra("pitanja");

        odgovoriList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == pozicijaTacnog) {
                    dodajTacan.setEnabled(true);
                    pozicijaTacnog = -1;
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
                validirajNaziv();
                validirajOdgovore();
                if(validacija) {
                    nazivText.setBackgroundColor(Color.WHITE);
                    odgovorText.setBackgroundColor(Color.WHITE);
                    p.setNaziv(nazivText.getText().toString());
                    p.setTekstPitanja(nazivText.getText().toString());
                    Intent intent = new Intent(DodajPitanjeAkt.this, DodajKvizAkt.class);
                    intent.putExtra("pitanje", p);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
    }

    private void validirajNaziv() {
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
}
