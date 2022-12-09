package com.shopping.list.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.shopping.list.Item;
import com.shopping.list.ItemListViewAdapter;
import com.shopping.list.MainActivity;
import com.shopping.list.R;
import com.shopping.list.ui.ItemList;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    private ItemListViewAdapter adapter;
    private ListView listView;
    private ArrayList<ItemList> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) root.findViewById(R.id.itemListView);
        loadData(0);
/*        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBox newFragment = new DialogBox();
                newFragment.show(getActivity().getSupportFragmentManager(), "DIALOG");
            }
        });*/
        HomeViewModel model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
/*        model.getSelected().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                if (new DataBase(getActivity()).saveListItem(item)) {
                    Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Not Saved", Toast.LENGTH_SHORT).show();
                }
                loadData();
            }
        });*/

        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
                // Refresh Your Fragment
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_asc:
                loadData(1);
                return true;
            case R.id.action_desc:
                loadData(2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(int sort) {
        DataBase dataBase = new DataBase(getActivity());
        list = dataBase.retrieveListItems(sort);
        adapter = new ItemListViewAdapter(getActivity(), list);             //List view displaying items
        listView.setAdapter(adapter);
    }
}