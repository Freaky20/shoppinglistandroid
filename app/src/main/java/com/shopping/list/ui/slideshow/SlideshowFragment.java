package com.shopping.list.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.shopping.list.DataBase;
import com.shopping.list.DialogBox;
import com.shopping.list.LocationListViewAdapter;
import com.shopping.list.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SlideshowFragment extends Fragment {

    private LocationListViewAdapter adapter;
    private ListView listView;
    private SlideshowViewModel slideshowViewModel;

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
        loadData();
        return root;
    }
    private void loadData() {
        DataBase dataBase = new DataBase(getActivity());
        adapter = new LocationListViewAdapter(getActivity(), dataBase.retrieveLocation());             //List view displaying items
        listView.setAdapter(adapter);
    }
}