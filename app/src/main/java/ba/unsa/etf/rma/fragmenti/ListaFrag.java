package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.enumi.Task;
import ba.unsa.etf.rma.helperi.ConnectionHelper;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.sqlite.DatabaseHelper;
import ba.unsa.etf.rma.sqlite.Query;
import ba.unsa.etf.rma.taskovi.GetListTask;

public class ListaFrag extends Fragment implements GetListTask.OnCategoryLoaded {


    private ArrayList<Kategorija> kategorije;
    private ArrayAdapter<String> kategorijeAdapter;
    private ListView listaKategorije;

    private FilterCategory filterCategory;
    private boolean isConnected = false;

    private DatabaseHelper databaseHelper;
    private Query query;

    public ListaFrag() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseHelper = new DatabaseHelper(getActivity());
        query = new Query(databaseHelper.getWritableDatabase());

        try {
            filterCategory = (FilterCategory)getActivity();
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
        listaKategorije = (ListView)getView().findViewById(R.id.listaKategorija);
        isConnected = ConnectionHelper.isNetworkAvailable(getActivity());
        if(isConnected) {
            new GetListTask(getContext().getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded) this).execute(Task.TaskType.CATEGORY);
        }
        else {
            kategorije = query.getAllCategories();
            setCategoryAdapter();
        }
        listaKategorije.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentCategory = "";
                long ID = 0;
                if(i != kategorijeAdapter.getCount() - 1) {
                    currentCategory = kategorije.get(i).getDocumentID();
                    ID = kategorije.get(i).getID();
                }
                else {
                    currentCategory = "Svi";
                    ID = 0;
                }
                if(isConnected) filterCategory.onCategorySelected(currentCategory);
                else filterCategory.onCategorySelected(ID);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = load;
        setCategoryAdapter();
    }

    public void setCategoryAdapter() {
        ArrayList<String> kategorijeZaAdapter = MiscHelper.izdvojiImenaKategorija(kategorije);
        kategorijeZaAdapter.add("Svi");
        kategorijeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, kategorijeZaAdapter);
        listaKategorije.setAdapter(kategorijeAdapter);
        MiscHelper.setListViewHeightBasedOnChildren(listaKategorije);
    }

    public interface FilterCategory{
        void onCategorySelected(String categoryName);
        void onCategorySelected(long ID);
    }

    @Override
    public void onDetach() {
        databaseHelper.close();
        super.onDetach();
    }
}
