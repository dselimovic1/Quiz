package ba.unsa.etf.rma.helper;

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

public class ConnectionHelper {

    public ConnectionHelper() {

    }

    public HttpURLConnection setConnection(String URL, String TOKEN) {
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
            JSONObject object = null;
            object = new JSONObject(response);
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
