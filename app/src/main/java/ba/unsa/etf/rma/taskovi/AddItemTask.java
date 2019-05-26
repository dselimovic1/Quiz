package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.interfejsi.FirestoreStorable;
import ba.unsa.etf.rma.singleton.Baza;

public class AddItemTask extends AsyncTask<FirestoreStorable, Void, Void> {

    private Baza.TaskType type;
    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "POST";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/";

    public AddItemTask(InputStream stream, Baza.TaskType type) {
        this.stream = stream;
        this.type = type;
    }

    @Override
    protected Void doInBackground(FirestoreStorable... firestoreStorables) {
        try {
            URL = connectionHelper.setListURL(type, URL);
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            String document = firestoreStorables[0].getJSONFormat();
            connectionHelper.writeDocument(conn, document);
            String response = connectionHelper.getResponse(conn.getInputStream());
            firestoreStorables[0].setDocumentID(connectionHelper.getDocumentID(response));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
