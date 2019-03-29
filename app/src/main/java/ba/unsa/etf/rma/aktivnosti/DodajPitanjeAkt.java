package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajPitanjeAkt extends AppCompatActivity {

    private ArrayList<String> odgovori;
    private ArrayAdapter<String> odgovoriAdapter;

    private ListView odgovoriList;
    private EditText nazivText;
    private EditText odgovorText;
    private Button dodaj;
    private Button dodajTacan;

    private Pitanje p = new Pitanje();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        odgovoriList = (ListView)findViewById(R.id.lvOdgovori);
        nazivText = (EditText)findViewById(R.id.etNaziv);
        odgovorText = (EditText)findViewById(R.id.etOdgovor);
        dodaj = (Button)findViewById(R.id.btnDodajOdgovor);
        dodajTacan = (Button)findViewById(R.id.btnDodajTacan);

        odgovori = new ArrayList<>();
        odgovoriAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, odgovori);
        odgovoriList.setAdapter(odgovoriAdapter);

        odgovoriList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                odgovori.remove(i);
                odgovoriAdapter.notifyDataSetChanged();
            }
        });

        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(odgovorText.getText().toString().equals("")) return;
                odgovori.add( odgovorText.getText().toString());
                odgovoriAdapter.notifyDataSetChanged();
                p.dodajOdgovor(odgovorText.getText().toString());
                odgovorText.setText("");
                odgovoriAdapter.notifyDataSetChanged();
            }
        });


        dodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(odgovorText.getText().toString().equals("")) return;
                odgovori.add(odgovorText.getText().toString());
                odgovoriAdapter.notifyDataSetChanged();
                p.dodajOdgovor(odgovorText.getText().toString());
                p.setTacan(odgovorText.getText().toString());
                odgovorText.setText("");
                odgovoriAdapter.notifyDataSetChanged();
            }
        });
    }
}
