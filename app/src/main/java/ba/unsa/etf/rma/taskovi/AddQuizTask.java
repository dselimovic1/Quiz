package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Kviz;

public class AddQuizTask extends AsyncTask<Kviz, Void, Kviz> {

    private IDSetter setter;
    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "POST";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL ="https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Kvizovi?access_token=";

    public AddQuizTask(InputStream stream, IDSetter setter) {
        this.stream = stream;
        this.setter = setter;
    }

    @Override
    protected Kviz doInBackground(Kviz... kvizovi) {
        try {
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            String document = kvizovi[0].getJSONFormat();
            connectionHelper.writeDocument(conn, document);
            String response = connectionHelper.getResponse(conn.getInputStream());
            kvizovi[0].setDocumentID(connectionHelper.getDocumentID(response));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return kvizovi[0];
    }

    @Override
    protected void onPostExecute(Kviz kviz) {
        setter.setID(kviz);
    }

    public interface IDSetter {
        void setID(Kviz kviz);
    }

}
