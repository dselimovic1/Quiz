package ba.unsa.etf.rma.aktivnosti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import ba.unsa.etf.rma.R;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback{


    private Icon[] ikone;
    private Button iconDialogButton;
    private Button saveButton;
    private EditText icondID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);


        final IconDialog dialog = new IconDialog();
        iconDialogButton = (Button)findViewById(R.id.btnDodajIkonu);
        saveButton = (Button)findViewById(R.id.btnDodajKategoriju);
        icondID = (EditText)findViewById(R.id.etIkona);
        iconDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setSelectedIcons(ikone);
                dialog.show(getSupportFragmentManager(),"icon_dialog");
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        ikone = icons;
        icondID.setText(Integer.toString(ikone[0].getId()));
    }

}