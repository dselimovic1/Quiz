package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Kviz;

public class GetQuizListTask extends AsyncTask<ArrayList<Kviz>, Void, Void> {

    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_CODE = "GET";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Kvizovi?access_token=";

    @Override
    protected Void doInBackground(ArrayList<Kviz>... arrayLists) {
        try {

        }
        catch (IOException e) {

        }
        return null;
    }
}
