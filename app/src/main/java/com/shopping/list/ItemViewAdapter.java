package com.shopping.list;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

//Method for handling list view for displaying items
public class ItemViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> data;
    public AddItem addItem;                                     //Link shop interface
    private String searchText;

    // View Type for Separators
    private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    // View Type for Regular rows
    private static final int ITEM_VIEW_TYPE_REGULAR = 1;
    // View Type for Regular rows
    private static final int ITEM_VIEW_TYPE_ADD = 2;
    // Types of Views that need to be handled
    // -- Separators and Regular rows --
    private static final int ITEM_VIEW_TYPE_COUNT = 3;

    public interface AddItem{                                     //Interface for sending the position and id of the shopping list back to the main activity
        void sendItem(int quantity, int id);
    }

    public ItemViewAdapter(Context context, ArrayList<Item> list, String searchText) {
        this.context = context;
        this.data = list;
        this.searchText = this.searchText;
    }

    @Override //Return array list size
    public int getCount() {
        return data.size();
    }

    @Override       //Get item
    public Item getItem(int position) {
        return data.get(position);
    }

    @Override       //Get item id
    public long getItemId(int position) {
        return position;
    }

    //Method for adding an item to the list view
    public void add(Item item){
        data.add(item);
        notifyDataSetChanged();
    }
    //Method for deleting an item in the list view
    public void delete(int position){
        data.remove(position);
        notifyDataSetChanged();
    }

 /*   @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }*/

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isSection = data.get(position).isSeparator();
        boolean isAdd = data.get(position).isAddItem();

        if (isSection) {
            return ITEM_VIEW_TYPE_SEPARATOR;
        }
        else if(isAdd){
            return ITEM_VIEW_TYPE_ADD;
        }
        else {
            return ITEM_VIEW_TYPE_REGULAR;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view;
        final LayoutInflater inflat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;

        final Item item = data.get(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
                // If its a section ?
                view = inflater.inflate(R.layout.seperator_item_list, null);
            }
            else if(itemViewType == ITEM_VIEW_TYPE_ADD){
                view = inflater.inflate(R.layout.add_item_list, null);
            }
            else {
                // Regular row
                view = inflater.inflate(R.layout.shopping_item_list, null);
            }
        }
        else {
            view = convertView;
        }


        if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
            // If separator

            TextView separatorView = (TextView) view.findViewById(R.id.separator);
            separatorView.setText(item.getName());
        }
        else if(itemViewType == ITEM_VIEW_TYPE_ADD){
            TextView addView = (TextView) view.findViewById(R.id.addItem);
            addView.setText(item.getName());
            addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                       //Delete image click listener that displays a dialog to delete an item
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final View myView = inflat.inflate(R.layout.dialog_add_item, null);
                    builder.setView(myView);
                    final TextView itemName = (TextView) myView.findViewById(R.id.itemName);
                    final EditText quantity = (EditText) myView.findViewById(R.id.itemQty);

                    itemName.setText(searchText);

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {           //Yes
                            if(!quantity.getText().toString().isEmpty()){
                                int amount = Integer.parseInt(quantity.getText().toString());
                                DataBase db = new DataBase(context);
                                int itemID = db.saveItem(new Item(searchText));
                                if(itemID > 0){
                                    addItem = (AddItem) parent.getContext();
                                    addItem.sendItem(amount, itemID);
                                    dialog.dismiss();
                                }

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
        }
        else {
            // If regular

            // Set contact name and number
            TextView itemNameView = (TextView) view.findViewById(R.id.nameLabel);
            itemNameView.setText( item.getName() );

            itemNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                       //Delete image click listener that displays a dialog to delete an item
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final View myView = inflat.inflate(R.layout.dialog_add_item, null);
                    builder.setView(myView);
                    final TextView itemName = (TextView) myView.findViewById(R.id.itemName);
                    final EditText quantity = (EditText) myView.findViewById(R.id.itemQty);

                    itemName.setText(item.getName());

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {           //Yes
                            if(!quantity.getText().toString().isEmpty()){
                                int amount = Integer.parseInt(quantity.getText().toString());
                                addItem = (AddItem) parent.getContext();
                                addItem.sendItem(amount, item.getItemID());
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

            ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                       //Delete image click listener that displays a dialog to delete an item
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure you want to delete?");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {           //Yes
                            DataBase dataBase = new DataBase(context);
                            if (dataBase.deleteItem(item)) {
                                if(getItem(position - 1).isSeparator() && getItem(position + 1).isSeparator()){
                                    delete(position - 1);
                                    delete(position - 1);
                                }
                                else {
                                    delete(position);
                                }
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
        }

        return view;
    }
/*    //Declaration and Initialisation
    private ArrayList<Item> data;                       //Array list of items
    private Context context;                            //Context of passed activity
    private static LayoutInflater inflater = null;         //Layout inflater
    //Constructor
    public ItemViewAdapter(Context context, ArrayList<Item> data) {
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
    public void add(Item item){
        data.add(item);
        notifyDataSetChanged();
    }
    //Method for deleting an item in the list view
    public void delete(int position){
        data.remove(position);
        notifyDataSetChanged();
    }
    //Method for handling of displaying the list view and on click listeneres
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // See if the view needs to be inflated
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.shopping_item_list, null);
        }
        final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.nameLabel);
        final ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);
        // Get the data item
        final Item item = data.get(position);
        if(item.isBought()){
            simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_on_holo);
            simpleCheckedTextView.setChecked(true);
        }
        else {
            simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_off_holo);
            simpleCheckedTextView.setChecked(false);
        }
        // Display the data item's properties
        simpleCheckedTextView.setText(item.getName() + " x " + item.getQuantity());
        // perform on Click Event Listener on CheckedTextView
        simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleCheckedTextView.isChecked()) {
        // set cheek mark drawable and set checked property to false
                    DataBase dataBase = new DataBase(context);
                    item.setBought(false);
                    if(dataBase.updateListItem(item)){
                        simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_off_holo);
                        simpleCheckedTextView.setChecked(false);
                        data.get(position).setBought(false);
                    }
                } else {
            // set cheek mark drawable and set checked property to true
                    item.setBought(true);
                    DataBase dataBase = new DataBase(context);
                    if(dataBase.updateListItem(item)){
                        simpleCheckedTextView.setCheckMarkDrawable(R.drawable.btn_check_on_holo);
                        simpleCheckedTextView.setChecked(true);
                        data.get(position).setBought(true);
                    }
                }
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       //Delete image click listener that displays a dialog to delete an item
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete?");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {           //Yes
                        DataBase dataBase = new DataBase(context);
                        if(dataBase.deleteListItem(item)){
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
    }*/
}
