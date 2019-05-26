package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.InputStream;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Rang;

public class AddRangTask extends AsyncTask<Rang, Void, Void> {


    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "POST";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Pitanja?access_token=";

    public AddRangTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(Rang... rangs) {
        return null;
    }
}
