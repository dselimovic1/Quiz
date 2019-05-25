package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ba.unsa.etf.rma.klase.Pitanje;

public class AddQuestionTask extends AsyncTask<Pitanje, Void, Void> {

    private InputStream stream;
    private static String TOKEN;
    private static String AUTH = "https://www.googleapis.com/auth/datastore";
    private static String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Pitanja?access_token=";

    public AddQuestionTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(Pitanje... pitanja) {
        try {
            setAccessToken();
            HttpURLConnection conn = setConnection();
            String document = getJSONFormat(pitanja[0]);
            writeDocument(conn, document);
            String response = getResponse(conn.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getResponse(InputStream inputStream) {
        StringBuilder response = new StringBuilder();
        try {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                String responseLine = null;
                while((responseLine = br.readLine()) != null) response.append(responseLine.trim());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public void writeDocument(HttpURLConnection connection, String document) {
        try {
            try(OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = document.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAccessToken() {
        GoogleCredential credentials;
        try {
            credentials = GoogleCredential.fromStream(stream).createScoped(Lists.newArrayList(AUTH));
            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpURLConnection setConnection() {
        HttpURLConnection conn = null;
        try {
            URL urlObj = new URL(URL + URLEncoder.encode(TOKEN, "UTF-8"));
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public String getJSONFormat(Pitanje pitanje) {
        String json = "";
        int index = pitanje.getOdgovori().indexOf(pitanje.getTacan());
        json += "\"fields\": {\"naziv\": {\"stringValue\": \"" + pitanje.getNaziv() + "\"}," +
                "\"odgovori\": {\"arrayValue\": {\"values\": [";
        for(int i = 0; i < pitanje.getOdgovori().size(); i++) {
            json += "{\"stringValue\": \"" +  pitanje.getOdgovori().get(i) + "\"}";
        }
        json += "]}}, \"indexTacnog\": {\"integerValue\": \"" + index + "\"}}}";
        return json;
    }
}
