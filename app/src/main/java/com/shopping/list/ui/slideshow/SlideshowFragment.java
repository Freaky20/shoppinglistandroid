package com.shopping.list.ui.slideshow;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.shopping.list.DataBase;
import com.shopping.list.Location;
import com.shopping.list.LocationListViewAdapter;
import com.shopping.list.R;
import android.widget.SearchView;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private LocationListViewAdapter adapter;
    private ListView listView;
    private ArrayList<Location> list;
    private ArrayList<Location> searchList;
    private SlideshowViewModel slideshowViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        slideshowViewModel =
 //               ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
 //       final TextView textView = root.findViewById(R.id.text_slideshow);
 //       slideshowViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//
//                textView.setText(s);
//            }
//        }
 //       );
        listView = (ListView) root.findViewById(R.id.locationListView);
        loadData(false);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){                                  //If the search text is not null or empty
                    searchList = new ArrayList<Location>();                                    //Search array list
                    for(int i = 0; i < list.size(); i++){               //Looping through every anime object in the array list
                        if(list.get(i).getName().toLowerCase().contains(newText)){    //If the anime name contains the word of search text, add it to the search array list
                            searchList.add(list.get(i));
                        }
                    }
                    loadData(true);                                           //Repopulate list with search array list
                }
                else{
                    loadData(false);                                           //If there is no text in the search view then reset view
                }
                return true;
            }
        });
    }

    private void loadData(boolean search) {

        if(search){
            list = searchList;
        }
        else {
            DataBase dataBase = new DataBase(getActivity());
            list = dataBase.retrieveLocations();
        }
        adapter = new LocationListViewAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }
}