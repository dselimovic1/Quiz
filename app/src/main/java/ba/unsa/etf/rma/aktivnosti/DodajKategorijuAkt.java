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
import ba.unsa.etf.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {


    private Icon[] ikone;
    private Button iconDialogButton;
    private Button saveButton;
    private EditText icondID;
    private EditText imeKategorije;

    private ArrayList<String> kategorije;
    private Kategorija k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);

        kategorije = new ArrayList<>();

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
                if(provjeriNaziv() == false) {
                    imeKategorije.setBackgroundColor(Color.RED);
                }
                else if(provjeriIDIkone() == false) {
                    icondID.setBackgroundColor(Color.RED);
                }
                else {
                    k = new Kategorija(imeKategorije.getText().toString(), icondID.getText().toString());
                    kategorije.add(k.getNaziv());
                    imeKategorije.setBackgroundColor(Color.WHITE);
                    icondID.setBackgroundColor(Color.WHITE);
                    Intent intent = new Intent(DodajKategorijuAkt.this, DodajKvizAkt.class);
                    intent.putExtra("novaKategorija", imeKategorije.getText().toString());
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

    private boolean provjeriNaziv() {
        for(String s : kategorije) {
            if(s.equals(imeKategorije.getText().toString()))
                return false;
        }
        return true;
    }

    private boolean provjeriIDIkone(){
        return !icondID.getText().toString().equals("");
    }

}