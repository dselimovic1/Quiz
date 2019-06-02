package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.interfejsi.FirestoreStorable;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;

public class GetListTask extends AsyncTask<Task.TaskType, Void, String> {


    private OnCategoryLoaded categoryLoaded;
    private OnQuestionLoaded questionLoaded;
    private OnQuizLoaded quizLoaded;
    private OnRangLoaded rangLoaded;
    private InputStream stream;
    private Task.TaskType type;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String REQUEST_TYPE = "GET";
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/";

    public GetListTask(InputStream stream, OnQuizLoaded quizLoaded) {
        this.stream = stream;
        this.quizLoaded = quizLoaded;
    }

    public GetListTask(InputStream stream, OnQuestionLoaded questionLoaded) {
        this.stream = stream;
        this.questionLoaded = questionLoaded;
    }

    public GetListTask(InputStream stream, OnCategoryLoaded categoryLoaded) {
        this.stream = stream;
        this.categoryLoaded = categoryLoaded;
    }

    public GetListTask(InputStream stream, OnRangLoaded rangLoaded) {
        this.stream = stream;
        this.rangLoaded = rangLoaded;
    }

    @Override
    protected String doInBackground(Task.TaskType... enums) {
        String response = null;
        try {
            type = enums[0];
            URL = connectionHelper.setListURL(type, URL);
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
        ArrayList<FirestoreStorable> store = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray array = obj.getJSONArray("documents");
            for (int i = 0; i < array.length(); i++) {
                store.add(storeValue(array.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
        }
        callback(store);
    }

    private void callback(ArrayList<? extends FirestoreStorable> store) {
        switch (type) {
            case QUESTION:
                questionLoaded.loadAllQuestion((ArrayList<Pitanje>) store);
                break;
            case QUIZ:
                quizLoaded.loadAllQuiz((ArrayList<Kviz>) store);
                break;
            case CATEGORY:
                categoryLoaded.loadAllCategory((ArrayList<Kategorija>) store);
                break;
            case RANGLIST:
                rangLoaded.loadAllRang((ArrayList<Rang>) store);
        }
    }

    private FirestoreStorable storeValue(JSONObject json) {
        switch (type) {
            case RANGLIST:
                return Rang.convertFromJSON(json);
            case QUIZ:
                return Kviz.convertFromJSON(json);
            case CATEGORY:
                return Kategorija.convertFromJSON(json);
            case QUESTION:
                return Pitanje.convertFromJSON(json);
        }
        throw new IllegalArgumentException("Wrong Type");
    }

    public interface OnQuestionLoaded {
        void loadAllQuestion(ArrayList<Pitanje> load);
    }

    public interface OnQuizLoaded {
        void loadAllQuiz(ArrayList<Kviz> load);
    }

    public interface OnCategoryLoaded {
        void loadAllCategory(ArrayList<Kategorija> load);
    }

    public interface OnRangLoaded {
        void loadAllRang(ArrayList<Rang> load);
    }
}
