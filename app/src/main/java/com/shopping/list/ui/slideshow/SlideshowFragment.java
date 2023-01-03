package com.shopping.list.ui.slideshow;

import android.app.Activity;

import android.app.ProgressDialog;

import android.app.SearchManager;

import android.content.Context;

import android.content.DialogInterface;

import android.content.Intent;

import android.location.LocationManager;

import android.os.Build;

import android.os.Bundle;

import android.provider.Settings;

import android.view.LayoutInflater;

import android.view.Menu;

import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ListView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import com.shopping.list.database.DataBase;

import com.shopping.list.model.Location;

import com.shopping.list.adapter.LocationListViewAdapter;

import com.shopping.list.MainActivity;

import com.shopping.list.model.MainViewModel;

import com.shopping.list.R;

import com.shopping.list.model.ShoppingList;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.model.Place;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.rtchagas.pingplacepicker.PingPlacePicker;

import android.widget.SearchView;

import android.widget.Toast;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment
{
    private LocationListViewAdapter adapter;
    private ListView listView;
    private ArrayList<Location> list;
    private ArrayList<Location> searchList;
    private int shopID;
    private String shopName;
    private MainViewModel mainViewModel;
    private ProgressDialog progressDialog;
    private DataBase dataBase;
    private static final int REQUEST_CODE_PLACE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Loading place picker, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
        if(getArguments() != null && getArguments().getInt("shopListID", -1) != -1)
        {
            shopID = getArguments().getInt("shopListID");
            shopName = getArguments().getString("shopListName");
        }
        else
        {
            shopID = 0;
        }
        mainViewModel=ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        String title="Shop Locations ";
        if(shopName!=null){
            title+=shopName;
        }
        mainViewModel.addTitle(title);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        dataBase = new DataBase(getContext());
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isLocationEnabled(getContext()))
                {
                    progressDialog.show();
                    showPlacePicker();
                }
                else
                {
                    Toast.makeText(getActivity(), "check Location services or google maps.", Toast.LENGTH_LONG).show();
                }
            }
        }
        );
        listView = (ListView) root.findViewById(R.id.locationListView);
        listView.setEmptyView(root.findViewById(R.id.emptyElement));
        listView.setDivider(null);
        loadData(false);
        return root;
    }

    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        }
        else
        {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    private void showPlacePicker() {
        PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
        builder.setAndroidApiKey(getString(R.string.ANDROID_API_KEY))
                .setMapsApiKey(getString(R.string.MAPS_API_KEY));
        try
        {
            Intent placeIntent = builder.build(getActivity());
            startActivityForResult(placeIntent, REQUEST_CODE_PLACE);
        }
        catch(Exception ex)
        {
            Toast.makeText(getActivity(), "Google map is not working...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Search location name here");
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
                    searchList = new ArrayList<Location>();
                    for(int i = 0; i < list.size(); i++)
                    {
                        if(list.get(i).getName().toLowerCase().contains(newText))
                        {
                            searchList.add(list.get(i));
                        }
                    }
                    loadData(true);
                }
                else
                {
                    loadData(false);
                }
                return true;
            }
        }
        );
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
                if(isLocationEnabled(getContext()))
                {
                    progressDialog.show();
                    showPlacePicker();
                }
                else
                {
                    Toast.makeText(getActivity(), "Location services are disabled. Please enable location services.", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_PLACE) && (resultCode == Activity.RESULT_OK))
        {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null)
            {
                Location location = new Location(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
                if(!dataBase.checkLocationExist(location))
                {
                    int id = dataBase.saveLocation(location);
                    if (id != 0)
                    {
                        if(shopID > 0)
                        {
                            geofenceLocation(place.getLatLng(), id);
                        }
                        loadData(false);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Not Saved", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Place already added!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void geofenceLocation(final LatLng latLng, final int locationID)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("want to geofence this location?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Location location = dataBase.getLocation(locationID);
                if(dataBase.checkListIsGeofence(shopID))
                {
                    showError("Shopping List already geofence!", getContext());
                }
                else
                {
                    ShoppingList shoppingList = dataBase.getShopList(shopID);
                    shoppingList.setLastLocationID(location.getLocationID());
                    dataBase.updateShopList(shoppingList);
                    location.setGeofence(true);
                    location.setShoppingListID(shopID);
                    if (dataBase.updateLocation(location))
                    {
                        ((MainActivity) getActivity()).createGeofence(latLng, locationID + "");
                        ((MainActivity) getActivity()).addGeofence(locationID);
                        loadData(false);
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

    public void showError(String message, Context context)
    {
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

    private void loadData(boolean search)
    {
        if(search)
        {
            list=searchList;
        }
        else
        {
            list=dataBase.retrieveLocations();
        }
        adapter=new LocationListViewAdapter(getActivity(), list, shopID);             //List view displaying items
        listView.setAdapter(adapter);
    }
}