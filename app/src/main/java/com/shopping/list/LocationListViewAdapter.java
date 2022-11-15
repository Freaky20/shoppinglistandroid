package com.shopping.list;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

public class LocationListViewAdapter extends BaseAdapter {
    //Declaration and Initialisation
    private ArrayList<Location> data;                       //Array list of items
    private Context context;                            //Context of passed activity
    private static LayoutInflater inflater = null;         //Layout inflater
    //Constructor
    public LocationListViewAdapter(Context context, ArrayList<Location> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override //Return array list size
    public int getCount() {
        return data.size();
    }

    @Override       //Get item
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override       //Get item id
    public long getItemId(int position) {
        return position;
    }

    //Method for adding an item to the list view
    public void add(Location item){
        data.add(item);
        notifyDataSetChanged();
    }
    //Method for deleting an item in the list view
    public void delete(int position){
        data.remove(position);
        notifyDataSetChanged();
    }
    //Method for handling of displaying the list view and on click listener
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // See if the view needs to be inflated
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.location_list, null);
        }
        final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.nameLabel);
        final ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);
        // Get the data item
        final Location location = data.get(position);
        if(location.isGeofence()){
            simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_on_holo);
            simpleCheckedTextView.setChecked(true);
        }
        else {
            simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_off_holo);
            simpleCheckedTextView.setChecked(false);
        }

        //Toast.makeText(this, "You selected the place: " + place.getName(), Toast.LENGTH_SHORT).show();
        // Display the data item's properties
        simpleCheckedTextView.setText(location.getName());
        // perform on Click Event Listener on CheckedTextView
        simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleCheckedTextView.isChecked()) {
                    // set cheek mark drawable and set checked property to false
                    DataBase dataBase = new DataBase(context);
                    location.setGeofence(false);
                    if(dataBase.updateLocation(location)){
                        simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_off_holo);
                        simpleCheckedTextView.setChecked(false);
                        data.get(position).setGeofence(false);
                    }
                } else {
                    // set cheek mark drawable and set checked property to true
                    location.setGeofence(true);
                    DataBase dataBase = new DataBase(context);
                    if(dataBase.updateLocation(location)){
                        simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_on_holo);
                        simpleCheckedTextView.setChecked(true);
                        data.get(position).setGeofence(true);
                    }
                }
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       //Delete image click listener that displays a dialog to delete a location
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete?");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {           //Yes
                        DataBase dataBase = new DataBase(context);
                        if(dataBase.deleteLocation(location)){
                            delete(position);
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {       //No
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return view;
    }
}