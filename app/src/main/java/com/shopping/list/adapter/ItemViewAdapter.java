package com.shopping.list.adapter;

import android.content.Context;

import android.content.DialogInterface;

import android.view.View;

import android.view.ViewGroup;

import android.view.LayoutInflater;

import android.widget.TextView;

import android.widget.ImageView;

import android.widget.BaseAdapter;

import android.widget.EditText;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import com.shopping.list.R;

import com.shopping.list.model.Item;

import com.shopping.list.database.DataBase;

 public class ItemViewAdapter extends BaseAdapter
 {
    private Context Text;
    private ArrayList<Item> i;
    public AddItem addItem;
    private String searchText;
    private DataBase DB;

    private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    private static final int ITEM_VIEW_TYPE_REGULAR = 1;
    private static final int ITEM_VIEW_TYPE_ADD = 2;
    private static final int ITEM_VIEW_TYPE_COUNT = 3;

     public void add(Item item)
     {
         i.add(item);
         notifyDataSetChanged();
     }

     public void delete(int p)
     {
         i.remove(p);
         notifyDataSetChanged();
     }

     @Override
     public int getCount()
     {
         return i.size();
     }

     @Override
     public Item getItem(int p)
     {
         return i.get(p);
     }

     @Override
     public long getItemId(int p)
     {
         return p;
     }

     @Override
     public View getView(final int p,View convertView,final ViewGroup parent)
     {
         View view;
         final LayoutInflater inflat=(LayoutInflater) Text.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         final Item item=i.get(p);
         int itemViewType=getItemViewType(p);
         if(convertView==null)
         {
             LayoutInflater flat=(LayoutInflater) Text.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             if(itemViewType==ITEM_VIEW_TYPE_SEPARATOR)
             {
                 view=flat.inflate(R.layout.seperator_item_list, null);
             }
             else if(itemViewType==ITEM_VIEW_TYPE_ADD)
             {
                 view=flat.inflate(R.layout.add_item_list, null);
             }
             else
             {
                 view=flat.inflate(R.layout.shopping_item_list, null);
             }
         }
         else
         {
             view=convertView;
         }

         if(itemViewType==ITEM_VIEW_TYPE_SEPARATOR)
         {
             TextView separatorView = (TextView) view.findViewById(R.id.separator);
             separatorView.setText(item.getName());
         }
         else if(itemViewType==ITEM_VIEW_TYPE_ADD)
         {
             TextView addView=(TextView) view.findViewById(R.id.addItem);
             addView.setText(item.getName());
             addView.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Text);
                                                final View myView = inflat.inflate(R.layout.dialog_add_item, null);
                                                builder.setView(myView);
                                                builder.setCancelable(false);
                                                final TextView itemName = (TextView) myView.findViewById(R.id.itemName);
                                                EditText itemNameEdit = myView.findViewById(R.id.itemNameEdit);
                                                final EditText quantity = (EditText) myView.findViewById(R.id.itemQty);
                                                itemNameEdit.setVisibility(View.GONE);
                                                itemName.setText(searchText);
                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                                        {
                                                            public void onClick(DialogInterface dialog, int id)
                                                            {
                                                                if(!quantity.getText().toString().isEmpty())
                                                                {
                                                                    if(!DB.checkItemExist(formatText(searchText)))
                                                                    {
                                                                        int amount = Integer.parseInt(quantity.getText().toString());
                                                                        int itemID = DB.saveItem(new Item(formatText(searchText)));
                                                                        if (itemID > 0)
                                                                        {
                                                                            addItem.sendItem(amount, itemID, formatText(searchText));
                                                                            dialog.dismiss();
                                                                        }
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
                                        }
             );
         }
         else
         {
             TextView linkedShopList = view.findViewById(R.id.linkedList);
             ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);
             linkedShopList.setText(DB.getLinkedItemListNames(item.getItemID()));
             if(!linkedShopList.getText().toString().isEmpty())
             {
                 linkedShopList.setVisibility(View.VISIBLE);
                 deleteImage.setPaddingRelative(0, 24, 0 , 0);
             }
             TextView itemNameView = (TextView) view.findViewById(R.id.nameLabel);
             itemNameView.setText(item.getName());
             itemNameView.setOnClickListener(new View.OnClickListener()
                                             {
                                                 @Override
                                                 public void onClick(View v)
                                                 {
                                                     AddQty(inflat, item);
                                                 }
                                             }
             );
             view.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View view)
                                         {
                                             AddQty(inflat, item);
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
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                                            {
                                                                public void onClick(DialogInterface dialog, int id)
                                                                {
                                                                    if (DB.deleteItem(item))
                                                                    {
                                                                        DB.deleteLinkedItemToList(item.getItemID());
                                                                        DB.deleteHistoryItemID(item.getItemID());
                                                                        delete(p);
                                                                        addItem.deleteItem();
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
         }
         return view;
     }

     public interface AddItem
    {
        void sendItem(int quantity, int id, String name);
        void deleteItem();
    }

    public ItemViewAdapter(Context text, ArrayList<Item> l, String searchText, AddItem addItem)
    {
        this.Text=text;
        this.i=l;
        this.searchText=searchText;
        this.DB=new DataBase(text);
        this.addItem=addItem;
    }
     private void AddQty(LayoutInflater f,Item i)
     {
         AlertDialog.Builder builder=new AlertDialog.Builder(Text);
         final View myView=f.inflate(R.layout.dialog_add_item, null);
         builder.setView(myView);
         builder.setCancelable(false);
         final TextView itemName=(TextView) myView.findViewById(R.id.itemName);
         EditText itemNameEdit=myView.findViewById(R.id.itemNameEdit);
         final EditText quantity=(EditText) myView.findViewById(R.id.itemQty);
         itemNameEdit.setVisibility(View.GONE);
         itemName.setText(i.getName());
         builder.setPositiveButton("Ok",new DialogInterface.OnClickListener()
                 {
                     public void onClick(DialogInterface dialog,int id)
                     {
                         if(!quantity.getText().toString().isEmpty())
                         {
                             int amount=Integer.parseInt(quantity.getText().toString());
                             addItem.sendItem(amount,i.getItemID(),i.getName());
                             dialog.dismiss();
                         }
                     }
                 }
         );
         builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
                 {
                     public void onClick(DialogInterface dialog,int id)
                     {
                         dialog.dismiss();
                     }
                 }
         );
         AlertDialog dialog=builder.create();
         dialog.show();
     }

     @Override
     public int getViewTypeCount()
      {
         return ITEM_VIEW_TYPE_COUNT;
      }

      @Override
      public int getItemViewType(int p)
      {
        boolean isSection=i.get(p).isSeparator();
        boolean isAdd=i.get(p).isAddItem();
        if (isSection)
        {
            return ITEM_VIEW_TYPE_SEPARATOR;
        }
        else if(isAdd)
        {
            return ITEM_VIEW_TYPE_ADD;
        }
        else
        {
            return ITEM_VIEW_TYPE_REGULAR;
        }
      }

    @Override
    public boolean isEnabled(int p)
    {
        return getItemViewType(p)!=ITEM_VIEW_TYPE_SEPARATOR;
    }

    public String formatText(String name)
    {
        return name.substring(0, 1).toUpperCase()+name.substring(1);
    }
 }
