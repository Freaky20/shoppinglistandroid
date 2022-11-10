package com.shopping.list.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.shopping.list.DataBase;
import com.shopping.list.DialogBox;
import com.shopping.list.Item;
import com.shopping.list.ItemListViewAdapter;
import com.shopping.list.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment{

    private ItemListViewAdapter adapter;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) root.findViewById(R.id.itemListView);
        loadData();
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBox newFragment = new DialogBox();
                newFragment.show(getActivity().getSupportFragmentManager(), "DIALOG");
            }
        });
        HomeViewModel model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        model.getSelected().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                if (new DataBase(getActivity()).saveItem(item)) {
                    Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Not Saved", Toast.LENGTH_SHORT).show();
                }
                loadData();
            }
        });
        return root;
    }

    private void loadData() {
        DataBase dataBase = new DataBase(getActivity());
        adapter = new ItemListViewAdapter(getActivity(), dataBase.retrieveItems());             //List view displaying items
        listView.setAdapter(adapter);
    }
}