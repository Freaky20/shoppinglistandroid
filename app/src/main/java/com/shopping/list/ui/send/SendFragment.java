package com.shopping.list.ui.send;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.Menu;

import android.view.MenuInflater;

import android.view.MenuItem;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ListView;

import com.shopping.list.database.DataBase;

import com.shopping.list.model.Item;

import com.shopping.list.model.ItemList;

import com.shopping.list.adapter.ItemListViewAdapter;

import com.shopping.list.MainActivity;

import com.shopping.list.model.MainViewModel;

import com.shopping.list.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

public class SendFragment extends Fragment
{
    private ItemListViewAdapter adapter;
    private ListView listView;
    private ArrayList<ItemList> L;
    private int shopID;
    private String fragmentTitle;
    private MainViewModel mainViewModel;
    private DataBase DB;
    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DB = new DataBase(getContext());
        shopID = getArguments().getInt("shopID");
        String title = DB.getShopListName(shopID);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        fragmentTitle = title + " list";
        mainViewModel.addTitle(fragmentTitle);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
         root = inflater.inflate(R.layout.fragment_send, container, false);
        listView = (ListView) root.findViewById(R.id.itemListView);
        listView.setEmptyView(root.findViewById(R.id.emptyElement));
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
                               {
                                   @Override
                                   public void onClick(View view)
                                   {
                                       addItem(root);
                                   }
                               }
                               );
        L = DB.retrieveListItems(0, shopID);
        if(L.size() > 0)
        {
            loadData(0, shopID);
        }
        return root;
    }

    public void addItem(View root)
    {
        ((MainActivity) getActivity()).openAddItemFragment(root, shopID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.requireActivity().onBackPressed();
                return true;

            case R.id.action_asc:
                loadData(1, shopID);
                return true;

            case R.id.action_desc:
                loadData(2, shopID);
                return true;

            case R.id.add:
                addItem(root);
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(int sort, int shopID) {
        L = DB.retrieveListItems(sort, shopID);
        adapter = new ItemListViewAdapter(getActivity(), L);
        listView.setAdapter(adapter);
    }
 }
