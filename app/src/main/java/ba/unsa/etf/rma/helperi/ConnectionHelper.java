package ba.unsa.etf.rma.helperi;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ba.unsa.etf.rma.singleton.Baza;

public class ConnectionHelper {


    public ConnectionHelper() { }

    public String setAccessToken(InputStream stream, String AUTH) {
        GoogleCredential credentials;
        String TOKEN = "";
        try {
            credentials = GoogleCredential.fromStream(stream).createScoped(Lists.newArrayList(AUTH));
            credentials.refreshToken();
            TOKEN = credentials.getAccessToken();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return TOKEN;
    }

    public String setListURL(Baza.TaskType type, String URL) {
        URL = setURL(type, URL);
        return URL + "?access_token=";
    }

    public String setDocumentURL(Baza.TaskType type, String URL, String documentID) {
        URL = setURL(type, URL);
        return URL + "/" + documentID + "?access_token=";
    }

    public String setURL(Baza.TaskType type, String URL) {
        switch (type) {
            case QUESTION:
                URL += "Pitanja";
                break;
            case CATEGORY:
                URL += "Kategorije";
                break;
            case QUIZ:
                URL += "Kvizovi";
                break;
        }
        return URL;
    }

    public HttpURLConnection setConnection(String URL, String TOKEN, String REQUEST_TYPE) {
        HttpURLConnection conn = null;
        try {
            URL urlObj = new URL(URL + URLEncoder.encode(TOKEN, "UTF-8"));
            conn = (HttpURLConnection) urlObj.openConnection();
            if(REQUEST_TYPE.equals("GET") == false) conn.setDoOutput(true);
            conn.setRequestMethod(REQUEST_TYPE);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
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

    public String getDocumentID(String response) {
        try {
            JSONObject object = new JSONObject(response);
            String path = object.getString("name");
            String[] atr = path.split("/");
            return atr[atr.length - 1];
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
