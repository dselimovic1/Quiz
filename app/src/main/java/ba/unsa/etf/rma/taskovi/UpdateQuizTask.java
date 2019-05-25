package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Kviz;

public class UpdateQuizTask extends AsyncTask<Kviz, Void, Void> {

    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "PATCH";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL ="https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Kvizovi/";

    public UpdateQuizTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(Kviz... kvizovi) {
        try {
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            URL += kvizovi[0].getDocumentID() + "?access_token=";
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            String document = kvizovi[0].getJSONFormat();
            connectionHelper.writeDocument(conn, document);
            String response = connectionHelper.getResponse(conn.getInputStream());
            Log.d("New ID:", response);
            Log.d("Old ID:", kvizovi[0].getDocumentID());
            kvizovi[0].setDocumentID(connectionHelper.getDocumentID(response));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
