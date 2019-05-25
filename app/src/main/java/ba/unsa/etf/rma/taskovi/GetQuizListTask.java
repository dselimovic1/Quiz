package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helperi.ConnectionHelper;

public class GetQuizListTask extends AsyncTask<GetQuizListTask.ListType, Void, String> {

    public enum ListType {QUIZ, CATEGORY, QUESTION};

    private OnResponseGet responseGetter;
    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "GET";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/";

    public GetQuizListTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected String doInBackground(GetQuizListTask.ListType... enums) {
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

    public interface OnResponseGet {
        void setResponse(String response);
    }
}
