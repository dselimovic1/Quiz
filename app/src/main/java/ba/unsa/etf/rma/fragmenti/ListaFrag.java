package ba.unsa.etf.rma.fragmenti;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.ListIterator;

import ba.unsa.etf.rma.R;

public class ListaFrag extends Fragment {


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
        if(getArguments().containsKey("kategorije"))
            kategorije = getArguments().getStringArrayList("kategorije");
        else
            kategorije = getArguments().getStringArrayList("kategorijes");
        clearCategories();
        kategorije.add("Svi");
        kategorijeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, kategorije);
        listaKategorije.setAdapter(kategorijeAdapter);
        setListViewHeightBasedOnChildren(listaKategorije);

        listaKategorije.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentCategory = kategorije.get(i);
                filterCategory.onCategorySelected(currentCategory);
            }
        });
    }

    private void clearCategories() {
        String temp = "Svi";
        for(ListIterator<String> iterator = kategorije.listIterator(); iterator.hasNext();) {
            String s = iterator.next();
            if(s.equals(temp)) {
                iterator.remove();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public interface FilterCategory{
        void onCategorySelected(String categoryName);
    }
}
