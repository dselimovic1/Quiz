package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Kviz;

public class GetQuizListTask extends AsyncTask<ArrayList<Kviz>, Void, Void> {

    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "GET";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Kvizovi?access_token=";

    public GetQuizListTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(ArrayList<Kviz>... arrayLists) {
        try {
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            String response = connectionHelper.getResponse(conn.getInputStream());
            Log.d("RESPONSE", response);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
