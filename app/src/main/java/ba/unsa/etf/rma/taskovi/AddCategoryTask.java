package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Kategorija;

public class AddCategoryTask extends AsyncTask<Kategorija, Void, Void> {

    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "POST";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Kategorije?access_token=";

    public AddCategoryTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(Kategorija... kategorije) {
        try {
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            String document = kategorije[0].getJSONFormat();
            connectionHelper.writeDocument(conn, document);
            String response = connectionHelper.getResponse(conn.getInputStream());
            kategorije[0].setDocumentID(connectionHelper.getDocumentID(response));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
