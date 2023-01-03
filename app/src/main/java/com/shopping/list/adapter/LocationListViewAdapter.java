package com.shopping.list.adapter;

import android.content.Context;

import android.content.DialogInterface;

import android.location.LocationManager;

import android.os.Build;

import android.provider.Settings;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.CheckedTextView;

import android.widget.ImageView;

import android.widget.TextView;

import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import com.shopping.list.model.Location;

import com.shopping.list.model.ShoppingList;

import com.shopping.list.database.DataBase;

import com.shopping.list.R;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

 public class LocationListViewAdapter extends BaseAdapter
 {
    private ArrayList<Location> L;
    private Context Text;
    private static LayoutInflater inflater = null;
    public GeoFenceInterface geoFenceInterface;
    private int shopID;
    private int chosenShopID;

     @Override
     public int getCount()
     {
         return L.size();
     }

     @Override
     public Object getItem(int p)
     {
         return L.get(p);
     }

     @Override
     public long getItemId(int p)
     {
         return p;
     }

     public void add(Location l)
     {
         L.add(l);
         notifyDataSetChanged();
     }

     public void delete(int p)
     {
         L.remove(p);
         notifyDataSetChanged();
     }

    public interface GeoFenceInterface
    {
        void createGeofenceData(LatLng latLng, int id);
        void removeGeofenceData(int id);
    }

    public LocationListViewAdapter(Context text, ArrayList<Location> l, int shopID)
    {
        this.Text = text;
        this.L = l;
        this.shopID = shopID;
        inflater = (LayoutInflater)text.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int p, View convertView, ViewGroup parent)
    {
        View view=convertView;
        if (view == null)
        {
            view=inflater.inflate(R.layout.location_list, null);
        }
        final CheckedTextView simpleCheckedTextView=(CheckedTextView) view.findViewById(R.id.nameLabel);
        final ImageView deleteImage=(ImageView) view.findViewById(R.id.delete_button);
        final TextView shopName=view.findViewById(R.id.shopNameLabel);
        final DataBase dataBase=new DataBase(Text);
        final Location location= L.get(p);
        final SwipeRevealLayout swipeRevealLayout = (SwipeRevealLayout)view.findViewById(R.id.swipe_layout);
        String shopNameText="";
        if(location.isGeofence())
        {
            //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_on);
            simpleCheckedTextView.setChecked(true);
            shopNameText=dataBase.getShopListName(location.getShoppingListID());
        }
        else
        {
            //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_button);
            simpleCheckedTextView.setChecked(false);
        }
        shopName.setText(shopNameText);
        simpleCheckedTextView.setText(location.getName());
        simpleCheckedTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isLocationEnabled(Text))
                {
                    if(simpleCheckedTextView.isChecked())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Text);
                        builder.setTitle("you want to remove from Geofence?");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                removeGeofence(location, p);
                                dialog.dismiss();
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
                    else
                    {
                        if(!L.get(p).isGeofence())
                        {
                            if(shopID != 0)
                            {
                                if(!dataBase.checkListIsGeofence(shopID))
                                {
                                    dataBase.saveHistory(shopID, location.getLocationID());
                                    addGeofence(shopID, location, p);
                                }
                                else
                                {
                                    Error("Only 1 shop to a list at a time");
                                }
                            }
                            else
                            {
                                loadOpenShopList(location, p);
                            }
                        }
                        else
                        {
                            Error("Location already linked to a list");
                        }
                    }
                }
                else
                {
                    Toast.makeText(Text, "check Location services or google maps.", Toast.LENGTH_LONG).show();
                }
            }
        }
        );

        deleteImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Text);
                builder.setTitle("you want to delete?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(deleteLocation(location))
                        {
                            delete(p);
                            swipeRevealLayout.close(false);
                            dialog.dismiss();
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
        }
        );
        return view;
    }

     public void geofenceShopList(ArrayList<ShoppingList> shoppingListArrayList, Location l, int p)
     {
         DataBase dataBase=new DataBase(Text);
         dataBase.saveHistory(shoppingListArrayList.get(chosenShopID).getShoppingListID(),l.getLocationID());
         addGeofence(shoppingListArrayList.get(chosenShopID).getShoppingListID(),l,p);
     }

     public void addGeofence(int shopID, Location l, int p)
     {
         DataBase dataBase=new DataBase(Text);
         l.setGeofence(true);
         l.setShoppingListID(shopID);
         ShoppingList shoppingList=dataBase.getShopList(shopID);
         shoppingList.setLastLocationID(l.getLocationID());
         dataBase.updateShopList(shoppingList);
         if (dataBase.updateLocation(l))
         {
             L.get(p).setGeofence(true);
             LatLng latLng=new LatLng(l.getLatitude(), l.getLongitude());
             geoFenceInterface=(GeoFenceInterface) Text;
             geoFenceInterface.createGeofenceData(latLng, l.getLocationID());
             notifyDataSetChanged();
         }
     }

    public void loadOpenShopList(Location l, int p)
    {
        DataBase dataBase = new DataBase(Text);
        ArrayList<ShoppingList> shoppingListArrayList = dataBase.retrieveShopList();
        ArrayList<ShoppingList> filteredList = new ArrayList<>();
        filteredList.clear();
        for (ShoppingList shopList:shoppingListArrayList)
        {
            if(!dataBase.checkListIsGeofence(shopList.getShoppingListID()))
            {
                filteredList.add(shopList);
            }
        }
        if(filteredList.size() > 0)
        {
            displaySuggested(filteredList, l, p);
        }
        else
        {
            Error("No list to link");
        }
    }

    public void displaySuggested(final ArrayList<ShoppingList> itemArrayList, final Location l, final int p)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Text);
        builder.setTitle("list to link");
        String[] items = new String[itemArrayList.size()];
        chosenShopID=0;
        for(int i = 0; i < items.length; i++)
        {
            items[i] = itemArrayList.get(i).getName();
        }
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selectedShopList(which);
            }
        }
        );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                geofenceShopList(itemArrayList, l, p);
            }
        }
        );
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void selectedShopList(int which)
     {
         chosenShopID = which;
     }

    public void removeGeofence(Location l, int p){
        DataBase dataBase = new DataBase(Text);
        l.setGeofence(false);
        if (dataBase.updateLocation(l))
        {
            L.get(p).setGeofence(false);
            geoFenceInterface  = (GeoFenceInterface) Text;
            geoFenceInterface.removeGeofenceData(l.getLocationID());
            notifyDataSetChanged();
        }
    }

    public static Boolean isLocationEnabled(Context text)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            LocationManager lm = (LocationManager) text.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        }
        else
        {
            int mode = Settings.Secure.getInt(text.getContentResolver(),Settings.Secure.LOCATION_MODE,Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

     public boolean deleteLocation(Location l)
     {
         DataBase dataBase=new DataBase(Text);
         if(dataBase.deleteLocation(l))
         {
             dataBase.deleteHistoryLocationID(l.getLocationID());
             geoFenceInterface=(GeoFenceInterface) Text;
             geoFenceInterface.removeGeofenceData(l.getLocationID());
             return true;
         }
         return false;
     }

     public void Error(String message)
     {
         AlertDialog.Builder builder = new AlertDialog.Builder(Text);
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
 }
