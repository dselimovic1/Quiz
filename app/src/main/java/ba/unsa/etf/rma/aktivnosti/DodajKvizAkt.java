package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;


public class DodajKvizAkt extends AppCompatActivity {

    private static int ADD_CATEGORY = 1;
    private static int ADD_QUESTION = 2;

    private ArrayList<String> kategorijeIme;
    private ArrayAdapter<String> kategorijeAdapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        spinner = (Spinner)findViewById(R.id.spKategorije);

        kategorijeIme = new ArrayList<>();
        kategorijeIme.add("Kategorije");
        kategorijeIme.add("Dodaj kategoriju");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorijeIme);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(kategorijeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0 && i == kategorijeIme.size() - 1) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    startActivityForResult(intent, ADD_CATEGORY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ADD_CATEGORY){
            kategorijeIme.add(1, data.getStringExtra("novaKategorija"));
            kategorijeAdapter.notifyDataSetChanged();
        }
    }

}
