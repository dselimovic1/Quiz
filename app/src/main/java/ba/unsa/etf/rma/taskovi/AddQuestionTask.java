package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ba.unsa.etf.rma.helper.ConnectionHelper;
import ba.unsa.etf.rma.klase.Pitanje;

public class AddQuestionTask extends AsyncTask<Pitanje, Void, Void> {

    private InputStream stream;
    private ConnectionHelper connectionHelper = new ConnectionHelper();
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Pitanja?access_token=";

    public AddQuestionTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(Pitanje... pitanja) {
        try {
            String TOKEN = connectionHelper.setAccessToken(stream, AUTH);
            HttpURLConnection conn = connectionHelper.setConnection(URL, TOKEN);
            String document = getJSONFormat(pitanja[0]);
            connectionHelper.writeDocument(conn, document);
            String response = connectionHelper.getResponse(conn.getInputStream());
            pitanja[0].setDocumentID(connectionHelper.getDocumentID(response));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getJSONFormat(Pitanje pitanje) {
        String json = "";
        int index = pitanje.getOdgovori().indexOf(pitanje.getTacan());
        json += "{\"fields\": {\"naziv\": {\"stringValue\": \"" + pitanje.getNaziv() + "\"}," +
                "\"odgovori\": {\"arrayValue\": {\"values\": [";
        for(int i = 0; i < pitanje.getOdgovori().size(); i++) {
            json += "{\"stringValue\": \"" +  pitanje.getOdgovori().get(i) + "\"}";
        }
        json += "]}}, \"indexTacnog\": {\"integerValue\": \"" + index + "\"}}}";
        return json;
    }
}
