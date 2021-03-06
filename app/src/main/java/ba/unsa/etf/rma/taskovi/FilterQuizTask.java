package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.klase.Kviz;

public class FilterQuizTask extends AsyncTask<String, Void, String> {

    private InputStream stream;
    private OnListFiltered listFiltered;
    private LinearLayout layout;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "POST";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents:runQuery?access_token=";

    public FilterQuizTask(InputStream stream, OnListFiltered listFiltered) {
        this.stream = stream;
        this.listFiltered = listFiltered;
    }

    public FilterQuizTask(InputStream stream, OnListFiltered listFiltered, LinearLayout layout) {
        this.stream = stream;
        this.listFiltered = listFiltered;
        this.layout = layout;
    }

    @Override
    protected void onPreExecute() {
        if(layout != null) layout.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = null;
        try{
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN, REQUEST_TYPE);
            String document = connectionHelper.setQuery(strings[0]);
            connectionHelper.writeDocument(conn, document);
            response = connectionHelper.getResponse(conn.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        ArrayList<Kviz> filter = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(response);
            for(int i = 0; i < array.length(); i++)  {
                if(array.getJSONObject(i).has("document"))
                filter.add(Kviz.convertFromJSON(array.getJSONObject(i).getJSONObject("document")));
            }
        }
        catch (JSONException e) {
        }
        listFiltered.filterList(filter);
    }

    public interface OnListFiltered {
        void filterList(ArrayList<Kviz> load);
    }
}
