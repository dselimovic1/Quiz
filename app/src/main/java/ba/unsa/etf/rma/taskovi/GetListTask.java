package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.singleton.Baza;

public class GetListTask extends AsyncTask<Baza.TaskType, Void, String> {


    private OnListResponse responseAdder;
    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "GET";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/";

    public GetListTask(InputStream stream, OnListResponse responseAdder) {
        this.stream = stream;
        this.responseAdder = responseAdder;
    }

    @Override
    protected String doInBackground(Baza.TaskType... enums) {
        String response = null;
        try {
            URL = connectionHelper.setListURL(enums[0], URL);
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            response = connectionHelper.getResponse(conn.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        responseAdder.setResponse(response);
    }

    public interface OnListResponse {
        void setResponse(String response);
    }
}
