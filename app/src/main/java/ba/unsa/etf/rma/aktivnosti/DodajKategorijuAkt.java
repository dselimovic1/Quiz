package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.enumi.Baza;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.taskovi.AddItemTask;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {


    private Icon[] ikone;
    private Button iconDialogButton;
    private Button saveButton;
    private EditText icondID;
    private EditText imeKategorije;

    private ArrayList<String> kategorije;
    private Kategorija k;
    private boolean validacija = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);
        kategorije = getIntent().getStringArrayListExtra("kategorije");
        final IconDialog dialog = new IconDialog();
        iconDialogButton = (Button) findViewById(R.id.btnDodajIkonu);
        saveButton = (Button) findViewById(R.id.btnDodajKategoriju);
        icondID = (EditText) findViewById(R.id.etIkona);
        imeKategorije = (EditText) findViewById(R.id.etNaziv);

        iconDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setSelectedIcons(ikone);
                dialog.show(getSupportFragmentManager(), "icon_dialog");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                provjeriNaziv();
                provjeriIDIkone();
                if (validacija) {
                    Intent intent = new Intent(DodajKategorijuAkt.this, DodajKvizAkt.class);
                    k = new Kategorija(imeKategorije.getText().toString(), icondID.getText().toString());
                    imeKategorije.setBackgroundColor(Color.WHITE);
                    icondID.setBackgroundColor(Color.WHITE);
                    intent.putExtra("kategorija", k.getNaziv());
                    new AddItemTask(getResources().openRawResource(R.raw.secret), Baza.TaskType.CATEGORY).execute(k);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        ikone = icons;
        icondID.setText(Integer.toString(ikone[0].getId()));
    }

    private void provjeriNaziv() {
        if(imeKategorije.getText().toString().equals("")) {
            validacija = false;
            imeKategorije.setBackgroundColor(Color.RED);
            return;
        }
        for(String s : kategorije) {
            if(s.equals(imeKategorije.getText().toString())) {
                validacija = false;
                imeKategorije.setBackgroundColor(Color.RED);
                return;
            }
        }
        validacija = true;
        imeKategorije.setBackgroundColor(Color.WHITE);
    }

    private void provjeriIDIkone() {
       if(icondID.getText().toString().equals("")) {
           validacija = false;
           icondID.setBackgroundColor(Color.RED);
           return;
       }
       icondID.setBackgroundColor(Color.WHITE);
    }

}