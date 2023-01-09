package com.shopping.list.ui.home;

import android.content.Context;

import android.content.DialogInterface;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.Menu;

import android.view.MenuInflater;

import android.view.MenuItem;

import android.view.View;

import android.view.ViewGroup;

import android.widget.EditText;

import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import com.shopping.list.database.DataBase;

import com.shopping.list.MainActivity;

import com.shopping.list.R;

import com.shopping.list.model.ShoppingList;

import com.shopping.list.adapter.ShoppingListViewAdapter;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

 public class HomeFragment extends Fragment
 {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ShoppingList> list;
    private View root;
    private TextView emptyView;
    private DataBase dataBase;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
         db=FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.shopping_recycler_view);
        emptyView = (TextView) root.findViewById(R.id.emptyElement);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        dataBase = new DataBase(getActivity());
        loadData(0);
        HomeViewModel model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add:
                addShopList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addShopList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add new List");
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.dialog_add_shop_list, null);
        builder.setView(myView);
        final EditText shopName = (EditText) myView.findViewById(R.id.shopName);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(!shopName.getText().toString().isEmpty())
                {
                    if(!dataBase.checkShoppingListExist(shopName.getText().toString()))
                    {
                        int itemID = dataBase.saveShopList(new ShoppingList(shopName.getText().toString()));
                        if (itemID > 0)
                        {
                            loadData(0);
                            dialog.dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "list already exists!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.notifyDataSetChanged();
        ((ShoppingListViewAdapter) adapter).setOnItemClickListener(new ShoppingListViewAdapter.MyClickListener()
        {
            @Override
            public void onItemClick(int position, View v)
            {
                ((MainActivity) getActivity()).openShopListFragment(root, list.get(position).getShoppingListID());
            }
        }
        );
    }

    private void loadData(int sort)
    {
        list = dataBase.retrieveShopList();
        if (list.isEmpty())
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        adapter = new ShoppingListViewAdapter(getContext(), list);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();
                checkEmpty();
            }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount)
            {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }
            void checkEmpty()
            {
                emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        }
        );
        recyclerView.setAdapter(adapter);
    }
 }