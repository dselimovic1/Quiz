package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;

public class GetListTask extends AsyncTask<GetListTask.ListType, Void, String> {

    public enum ListType {QUIZ, CATEGORY, QUESTION};

    private OnResponseAdded responseAdder;
    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "GET";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/";

    public GetListTask(InputStream stream, OnResponseAdded responseAdder) {
        this.stream = stream;
        this.responseAdder = responseAdder;
    }

    @Override
    protected String doInBackground(GetListTask.ListType... enums) {
        String response = null;
        try {
            setURL(enums[0]);
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            response = connectionHelper.getResponse(conn.getInputStream());
            Log.d("RESPONSE", response);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        responseAdder.addResponse(response);
    }

    private void setURL(ListType type) {
        String listType = "";
        switch (type) {
            case QUIZ:
                listType = "Kvizovi";
                break;
            case CATEGORY:
                listType = "Kategorije";
                break;
            case QUESTION:
                listType = "Pitanja";
                break;
        }
        URL += listType + "?access_token=";
    }

    public interface OnResponseAdded {
        void addResponse(String response);
    }
}
