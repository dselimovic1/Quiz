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
import ba.unsa.etf.rma.enumi.Baza;
import ba.unsa.etf.rma.helperi.MiscHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.taskovi.GetListTask;

public class ListaFrag extends Fragment implements GetListTask.OnCategoryLoaded {


    private ArrayList<String> kategorije;
    private ArrayAdapter<String> kategorijeAdapter;
    private ListView listaKategorije;

    private FilterCategory filterCategory;

    public ListaFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            filterCategory = (FilterCategory)getActivity();
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
        listaKategorije = (ListView)getView().findViewById(R.id.listaKategorija);
        new GetListTask(getActivity().getResources().openRawResource(R.raw.secret), (GetListTask.OnCategoryLoaded)this).execute(Baza.TaskType.CATEGORY);

        listaKategorije.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentCategory = null;
                if(i != kategorije.size() - 1)
                    currentCategory = kategorije.get(i);
                filterCategory.onCategorySelected(currentCategory);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void loadAllCategory(ArrayList<Kategorija> load) {
        kategorije = MiscHelper.izdvojiImenaKategorija(load);
        kategorijeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, kategorije);
        listaKategorije.setAdapter(kategorijeAdapter);
        MiscHelper.setListViewHeightBasedOnChildren(listaKategorije);
    }

    public interface FilterCategory{
        void onCategorySelected(String categoryName);
    }
}
