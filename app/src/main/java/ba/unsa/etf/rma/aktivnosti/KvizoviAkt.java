package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

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
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.singleton.Baza;


public class KvizoviAkt extends AppCompatActivity implements DetailFrag.CategoryAdd, ListaFrag.FilterCategory {

    private static int ADD_QUIZ = 1;
    private static int UPDATE_QUIZ = 2;

    private ArrayList<Kviz> kvizovi;
    private ArrayList<String> kategorijeIme;
    private ArrayAdapter<String> kategorijeAdapter;
    private KvizAdapter kvizAdapter;
    private Spinner spinner;
    private ListView list;

    private boolean mode = true;

    private Baza baza = Baza.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new ProbaTask().execute("rmaSpirala");

        FrameLayout listPlace = (FrameLayout)findViewById(R.id.listPlace);
        if(listPlace == null) mode = false;

        if(mode == false) {
            spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
            list = (ListView) findViewById(R.id.lvKvizovi);

            ucitajKvizove();
            ucitajKategorije();

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    if (position == kvizovi.size() - 1) {
                        intent.putExtra("add", ADD_QUIZ);
                        startActivityForResult(intent, ADD_QUIZ);
                    } else {
                        Kviz k = kvizovi.get(position);
                        intent.putExtra("add", UPDATE_QUIZ);
                        intent.putExtra("updateKviz",k);
                        intent.putExtra("pozicija",position);
                        startActivityForResult(intent, UPDATE_QUIZ);
                    }
                    return true;
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position == kvizovi.size() - 1) return;
                    Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                    intent.putExtra("kviz", (Parcelable) kvizovi.get(position));
                    startActivity(intent);
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String filter = spinner.getSelectedItem().toString();
                    if (filter.equals("Svi")) kvizovi = baza.dajKvizove();
                    else kvizovi = baza.dajFiltriranuListu(filter);
                    Kviz temp = new Kviz("Dodaj Kviz", null, new Kategorija(null, "671"));
                    if(kvizovi.contains(temp) == false) kvizovi.add(temp);
                    kvizAdapter = new KvizAdapter(KvizoviAkt.this, kvizovi);
                    list.setAdapter(kvizAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            ListaFrag listaFrag = (ListaFrag)fm.findFragmentById(R.id.listPlace);
            if (listaFrag == null) {
                listaFrag = new ListaFrag();
            }
            fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
            DetailFrag detailFrag = (DetailFrag)fm.findFragmentById(R.id.detailPlace);
            if(detailFrag == null) {
                detailFrag = new DetailFrag();
            }
            fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(mode == false) {
            ucitajKvizove();
            ucitajKategorije();
        }
    }

    @Override
    public void onCategoryAdded() {
        FragmentManager fm = getSupportFragmentManager();
        ListaFrag listaFrag = new ListaFrag();
        fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
    }

    @Override
    public void onCategorySelected(String categoryName) {
        FragmentManager fm = getSupportFragmentManager();
        DetailFrag detailFrag = new DetailFrag();
        Bundle bundle = new Bundle();
        if(categoryName != null)  bundle.putString("filter", categoryName);
        detailFrag.setArguments(bundle);
        fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
    }

    private void ucitajKvizove() {
        kvizovi = baza.dajKvizove();
        kvizovi.add(new Kviz("Dodaj Kviz", null, new Kategorija(null, "671")));
        kvizAdapter = new KvizAdapter(this, kvizovi);
        list.setAdapter(kvizAdapter);
    }

    private void ucitajKategorije() {
        kategorijeIme = baza.dajImenaKategorija();
        kategorijeIme.add("Svi");
        kategorijeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, kategorijeIme);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(kategorijeAdapter);
        int pozicija = kategorijeIme.size() - 1;
        if(pozicija < 0) pozicija = 0;
        spinner.setSelection(pozicija);
    }

    public class ProbaTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            GoogleCredential credentials;
            try {
                InputStream stream = getResources().openRawResource(R.raw.secret);
                credentials = GoogleCredential.fromStream(stream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

                credentials.refreshToken();
                String TOKEN = credentials.getAccessToken();

                String url = "https://firestore.googleapis.com/v1/projects/rmaspirala-2a3e2/databases/(default)/documents/probnaKolekcija?access_token=";
                URL urlObj = new URL(url + URLEncoder.encode(TOKEN, "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                String dokument = "{ \"fields\": { \"vrijednost\": {\"stringValue\": \"novi\"}}}";

                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = dokument.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();

                InputStream odgovor = conn.getInputStream();

                try(BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, "utf-8"))){
                    StringBuilder response = new StringBuilder();
                    String responsline = null;
                    while((responsline = br.readLine()) != null) response.append(responsline.trim());
                    Log.d("ODGOVOR", response.toString());
                }
                Log.d("TOKEN", TOKEN);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
