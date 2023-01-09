package com.shopping.list.ui.share;

import android.app.SearchManager;

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

import android.widget.ListView;

import android.widget.SearchView;

import android.widget.TextView;

import android.widget.Toast;

import com.shopping.list.database.DataBase;

import com.shopping.list.model.Item;

import com.shopping.list.model.ItemList;

import com.shopping.list.adapter.ItemViewAdapter;

import com.shopping.list.model.Location;

import com.shopping.list.MainActivity;

import com.shopping.list.model.MainViewModel;

import com.shopping.list.R;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

public class ShareFragment extends Fragment implements ItemViewAdapter.AddItem
{
    private ArrayList<Item> L;
    private ArrayList<Item> searchList;
    private int shopID;
    private DataBase DB;
    private MainViewModel mainViewModel;
    private String fragmentTitle;
    private ListView listView;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DB = new DataBase(getContext());
        shopID = getArguments().getInt("shopID");
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        fragmentTitle = "Item List";
        mainViewModel.addTitle(fragmentTitle);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        listView = (ListView) root.findViewById(R.id.itemView);
        listView.setEmptyView(root.findViewById(R.id.emptyElement));
        listView.setDivider(null);
        sortView(false, "");
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addItem();
            }
        }
        );
        return root;
    }

    public void addItem()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflat = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflat.inflate(R.layout.dialog_add_item, null);
        builder.setView(myView);
        TextView itemName = (TextView) myView.findViewById(R.id.itemName);
        final EditText itemNameEdit = myView.findViewById(R.id.itemNameEdit);
        final EditText quantity = (EditText) myView.findViewById(R.id.itemQty);
        itemName.setVisibility(View.GONE);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(!itemNameEdit.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty())
                {
                    if(!DB.checkItemExist(itemNameEdit.getText().toString()))
                    {
                        int amount = Integer.parseInt(quantity.getText().toString());
                        int itemID = DB.saveItem(new Item(formatText(itemNameEdit.getText().toString())));
                        if (itemID > 0)
                        {
                            sendItem(amount, itemID, itemNameEdit.getText().toString());
                            dialog.dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Item already added!", Toast.LENGTH_LONG).show();
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

    public String formatText(String name)
    {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.requireActivity().onBackPressed();
                return true;

            case R.id.add:
                addItem();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Search item");
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if(newText != null && !newText.isEmpty())
                {
                    searchList = new ArrayList<Item>();
                    for(int i = 0; i < L.size(); i++)
                    {
                        if(!L.get(i).isSeparator() && L.get(i).getName().toLowerCase().startsWith(newText.toLowerCase()))
                        {
                            searchList.add(L.get(i));
                        }
                    }
                    sortView(true, newText);
                }
                else
                {
                    sortView(false, "");
                }
                return true;
            }
        }
        );
    }

    public void deleteItem()
    {
        sortView(false, "");
    }

    public void sendItem(int quantity, int id, String name)
    {
        searchView.clearFocus();
        searchView.setQuery("", true);
        searchView.setIconified(true);
        ItemList item = new ItemList(quantity, id, shopID);
        int values[] = DB.checkItemListExist(item.getItemID(), item.getShoppingListID());
        if(values[0] == 0)
        {
            if(!DB.saveListItem(item))
            {
                Toast.makeText(getContext(), name + " not added!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), name + " added!", Toast.LENGTH_SHORT).show();
                if(DB.getItemListCount(shopID) == 1)
                {
                    checkLastLocation(id);
                }
            }
        }
        else
        {
            item.setItemListID(values[0]);
            item.setQuantity(item.getQuantity() + values[1]);
            if (DB.updateListItem(item))
            {
                Toast.makeText(getContext(), name + " done!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkLastLocation(int itemID)
    {
        int lastLocationID= DB.getShopList(shopID).getLastLocationID();
        if(lastLocationID!=0)
        {
            if(!DB.checkListIsGeofence(shopID)&&(DB.getLocation(lastLocationID).getLocationID() != -1))
            {
                autoGeofenceHistory(DB.getLocation(lastLocationID), true);
            }
        }
        else
        {
            if(!DB.checkListIsGeofence(shopID))
            {
                Location location= DB.getLocation(DB.getItemLocationHistory(itemID));
                if(location.getLocationID() != -1)
                {
                    autoGeofenceHistory(location,false);
                }
            }
        }
    }

    public void autoGeofenceHistory(final Location location, boolean lastLocation)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String previous = lastLocation ? " previous" : "";
        builder.setTitle("Do you want to Geofence to this" + previous + " places?");
        builder.setMessage(location.getName());
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(location.isGeofence())
                {
                    showError("places already geofence!", getContext());
                }
                else{
                    location.setGeofence(true);
                    location.setShoppingListID(shopID);
                    if (DB.updateLocation(location))
                    {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        ((MainActivity) getActivity()).createGeofence(latLng, location.getLocationID() + "");
                        ((MainActivity) getActivity()).addGeofence(location.getLocationID());
                    }
                }
            }
        }
        );
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
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

    public void showError(String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle("Error");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
            }
        }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sortView(boolean search, String searchText)
    {
        if(search)
        {
            L = searchList;
            if(L.size() < 1)
            {
                Item item = new Item("Tap here to add");
                item.setAddItem(true);
                L.add(item);
            }
        }
        else
        {
            L = DB.retrieveItemsSorted();
        }
        ItemViewAdapter adapter = new ItemViewAdapter(getActivity(), L, searchText, this);
        listView.setAdapter(adapter);
    }
 }