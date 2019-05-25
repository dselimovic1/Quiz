package ba.unsa.etf.rma.taskovi;

import android.os.AsyncTask;
import android.util.Log;

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

import ba.unsa.etf.rma.klase.Kviz;

public class AddQuizTask extends AsyncTask<Kviz, Void, Void> {

    InputStream stream;

    public AddQuizTask(InputStream stream) {
        this.stream = stream;
    }

    @Override
    protected Void doInBackground(Kviz... kvizovi) {
        GoogleCredential credentials;
        try {
            credentials = GoogleCredential.fromStream(stream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            String URL = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/Kviz?access_token=";
            java.net.URL urlObj = new URL(URL + URLEncoder.encode(TOKEN, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            String document = "{ \"fields\": { \"vrijednost\": {\"stringValue\": \"NOTOK\"}, \"atr\" : {\"stringValue\": \"OK\"}}}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = document.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            int code = conn.getResponseCode();
            InputStream odg = conn.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(odg, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null)
                    response.append(responseLine.trim());
                Log.d("odgovor", response.toString());
            }
            Log.d("TOKEN", TOKEN);
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        return null;
    }
}
