package com.shopping.list.adapter;

import android.content.Context;

import android.content.DialogInterface;

import android.graphics.Color;

import android.view.LayoutInflater;

import android.view.MenuItem;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.widget.PopupMenu;

import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.RecyclerView;

import com.shopping.list.R;

import com.shopping.list.model.Location;

import com.shopping.list.model.ShoppingList;

import com.shopping.list.database.DataBase;

 public class ShoppingListViewAdapter extends RecyclerView.Adapter<ShoppingListViewAdapter.DataObjectHolder>
 {
    private ArrayList<ShoppingList> mDataset;
    private static MyClickListener myClickListener;
    public LinkShops linkShops;
    public HandleGeofence handleGeofence;
    private Context Text;
    private DataBase DB;
    private ViewGroup parent;
    private View view;

     public ShoppingListViewAdapter(Context text,ArrayList<ShoppingList> myDataset)
     {
         this.mDataset=myDataset;
         this.Text=text;
         this.DB =new DataBase(text);
     }

     @Override
     public int getItemCount()
     {
         return mDataset.size();
     }

     @Override
     public void onBindViewHolder(final DataObjectHolder holder, int p)
     {
         holder.label.setText(mDataset.get(p).getName());
         holder.bought.setText(DB.getBoughtCount(mDataset.get(p).getShoppingListID()) + "");
         holder.total.setText( "/" + DB.getTotalListItems(mDataset.get(p).getShoppingListID()));                                //Setting the item total
         holder.deleteImage.setTag(mDataset.get(p).getShoppingListID());
         holder.linkShops.setTag(p);
         holder.label.setTag(mDataset.get(p).getName());
         if(mDataset.get(p).checkIfGeofence(Text))
         {
             holder.label.setTextColor(Color.parseColor("#FFFFFF"));
             holder.bought.setTextColor(Color.parseColor("#FFFFFF"));
             holder.total.setTextColor(Color.parseColor("#FFFFFF"));
             holder.deleteImage.setImageResource(R.drawable.list);
             holder.cardView.setCardBackgroundColor(Color.parseColor("#40B5E7"));
         }
     }

     public void addItem(ShoppingList dataObj, int index)
     {
         mDataset.add(index, dataObj);
         notifyItemInserted(index);
     }

     public void deleteItem(int i, int shopID)
     {
         if(i < mDataset.size())
         {
             if(DB.deleteShopList(mDataset.get(i)))
             {
                 DB.deleteLinkedShopToList(shopID);
                 int locationID = DB.checkForlinkShoppingList(shopID);
                 if(locationID != 0)
                 {
                     Location location = DB.getLocation(locationID);
                     location.setGeofence(false);
                     DB.updateLocation(location);
                     handleGeofence  = (HandleGeofence) Text;
                     handleGeofence.removeGeofenceData(locationID);
                 }
                 mDataset.remove(i);
                 notifyItemRemoved(i);
                 notifyItemRangeChanged(i, mDataset.size());
             }
         }
     }

     @Override
     public DataObjectHolder onCreateViewHolder(final ViewGroup parent,int viewType)
     {
         view=LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_shopping_list, parent, false);
         this.parent=parent;
         DataObjectHolder dataObjectHolder=new DataObjectHolder(view);
         ImageView deleteImage=(ImageView) view.findViewById(R.id.delete_button);
         ImageView linkImage=(ImageView) view.findViewById(R.id.link_button);
         TextView label=(TextView) view.findViewById(R.id.name);
         linkImage.setOnClickListener(new View.OnClickListener()
                                      {
                                          @Override
                                          public void onClick(View v)
                                          {
                                              showPickMenu(v,deleteImage,linkImage,label);
                                          }
                                      }
         );
         return dataObjectHolder;
     }

     public interface LinkShops
     {
        void sendLinkShops(String name, int id);
     }

     public interface HandleGeofence
     {
         void removeGeofenceData(int id);
     }

     public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener
     {
        TextView label;
        TextView total;
        TextView bought;
        ImageView deleteImage;
        ImageView linkShops;
        CardView cardView;

        public DataObjectHolder(View itemView)
        {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.name);
            total = (TextView) itemView.findViewById(R.id.total);
            bought = (TextView) itemView.findViewById(R.id.bought);
            deleteImage = (ImageView) itemView.findViewById(R.id.delete_button);
            linkShops = (ImageView) itemView.findViewById(R.id.link_button);
            cardView = itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
     }

     private void extracted(ViewGroup parent,ImageView deleteImage,ImageView linkImage)
     {
        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        builder.setTitle("Delete this shopping list?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                int position = (int)linkImage.getTag();
                int shopID = (int)deleteImage.getTag();
                deleteItem(position, shopID);
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
        AlertDialog dialog=builder.create();
        dialog.show();
     }

     private void showPickMenu(View anchor,ImageView deleteImage,ImageView linkImage,TextView label)
     {
        PopupMenu popupMenu=new PopupMenu(Text, anchor);
        popupMenu.inflate(R.menu.more);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.link:

                        linkShops=(LinkShops)parent.getContext();
                        int id=(int)deleteImage.getTag();
                        String name=(String)label.getTag();
                        linkShops.sendLinkShops(name,id);
                        break;

                    case R.id.delete:

                        extracted(parent,deleteImage,linkImage);
                        break;

                }
                return false;
            }
        }
        );
        popupMenu.show();
     }

     public void setOnItemClickListener(MyClickListener myClickListener)
     {
         this.myClickListener = myClickListener;
     }

     public interface MyClickListener
     {
         void onItemClick(int p, View v);
     }
 }
