package ba.unsa.etf.rma.helperi;

import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

public class ViewHelper {


    public static void setInvisible(Spinner spinner, ListView listView) {
        spinner.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }

    public static void setVisible(Spinner spinner, ListView listView) {
        spinner.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
    }
}
