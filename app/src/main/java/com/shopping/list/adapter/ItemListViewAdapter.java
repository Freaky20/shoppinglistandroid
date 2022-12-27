package com.shopping.list.adapter;

import android.content.Context;

import android.content.DialogInterface;

import android.view.ViewGroup;

import android.view.View;

import android.graphics.Paint;

import android.view.LayoutInflater;

import android.widget.EditText;

import android.widget.ImageView;

import android.widget.TextView;

import android.widget.Toast;

import android.widget.BaseAdapter;

import android.widget.CheckedTextView;

import java.util.Collections;

import java.util.Comparator;

import java.util.ArrayList;

import java.util.Iterator;

import androidx.appcompat.app.AlertDialog;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import com.chauthai.swipereveallayout.ViewBinderHelper;

import com.shopping.list.R;

import com.shopping.list.model.ItemList;

import com.shopping.list.database.DataBase;

 public class ItemListViewAdapter extends BaseAdapter
 {

    private ArrayList<ItemList> IL;
    private Context Text;
    private static LayoutInflater inflater = null;
    private DataBase DB;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public ItemListViewAdapter(Context text, ArrayList<ItemList> IL)
    {
        this.Text=text;
        this.IL=IL;
        this.DB=new DataBase(text);
        sortCheck();
        inflater=(LayoutInflater)text.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
     @Override
     public int getCount()
     {
         return IL.size();
     }

     @Override
     public Object getItem(int p)
     {
         return IL.get(p);
     }

     @Override
     public long getItemId(int p)
     {
         return p;
     }

     public void add(ItemList i)
     {
         IL.add(i);
         notifyDataSetChanged();
     }

     public void delete(int p)
     {
         IL.remove(p);
         notifyDataSetChanged();
     }

     @Override
     public boolean isEnabled(int p)
     {
         return !IL.get(p).isSeparator();
     }

     private void removeBought()
     {
         Iterator<ItemList> i = IL.iterator();
         while (i.hasNext())
         {
             ItemList itemList = i.next();
             if (itemList.isBought())
             {
                 i.remove();
                 IL.remove(itemList);
                 DB.deleteListItem(itemList);
             }
         }
     }

     private void DeleteClear()
     {
         boolean f = false;
         int i = 0;
         while (!f && i < IL.size())
         {
             if (IL.get(i).isClearBought())
             {
                 IL.remove(i);
                 f = true;
             }
             i++;
         }
     }

     private void DeleteSep()
     {
         boolean f = false;
         int i = 0;
         while (!f && i < IL.size())
         {
             if (IL.get(i).isSeparator())
             {
                 IL.remove(i);
                 f = true;
             }
             i++;
         }
     }

    private void sortCheck()
    {
        Collections.sort(IL, new Comparator<ItemList>()
        {
            @Override
            public int compare(ItemList i1, ItemList i2)
            {
                return Boolean.compare(i1.isBought(), i2.isBought());
            }
        }
        );
        AddSep();
    }

    private void AddSep()
    {
        boolean f = false;
        int i = 0;
        while (!f && i < IL.size())
        {
            if (IL.get(i).isBought())
            {
                ItemList i2 = new ItemList(true);
                i2.setSeparator(false);
                i2.setClearBought(true);
                IL.add(i, i2);
                ItemList i1 = new ItemList(true);
                IL.add(i, i1);
                f = true;
            }
            i++;
        }
    }

    public void editItem(final int p)
     {
         AlertDialog.Builder builder = new AlertDialog.Builder(Text);
         LayoutInflater inflat = (LayoutInflater) Text.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View myView = inflat.inflate(R.layout.dialog_add_item, null);
         builder.setView(myView);
         TextView itemName = (TextView) myView.findViewById(R.id.itemName);
         TextView heading = myView.findViewById(R.id.heading);
         EditText itemNameEdit = myView.findViewById(R.id.itemNameEdit);
         final EditText quantity = (EditText) myView.findViewById(R.id.itemQty);
         heading.setText("Edit product");
         itemNameEdit.setVisibility(View.GONE);
         itemName.setText(IL.get(p).getName());
         builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                 {
                     public void onClick(DialogInterface dialog, int id)
                     {
                         if (!quantity.getText().toString().isEmpty())
                         {
                             int amount = Integer.parseInt(quantity.getText().toString());
                             IL.get(p).setQuantity(amount);
                             if (DB.updateListItem(IL.get(p)))
                             {
                                 resetView();
                                 Toast.makeText(Text, "Done!", Toast.LENGTH_SHORT).show();
                                 dialog.dismiss();
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

     private void resetView()
     {
         DeleteSep();
         DeleteClear();
         sortCheck();
         notifyDataSetChanged();
     }

    @Override
    public View getView(final int p,View convertView,ViewGroup parent)
    {
        View view = null;
        if (IL.get(p).isSeparator())
        {
            view = inflater.inflate(R.layout.seperator_item_list, null);
            TextView separatorView = (TextView) view.findViewById(R.id.separator);
            separatorView.setText("Purchased Items");
        }
        else if (IL.get(p).isClearBought())
        {
            view = inflater.inflate(R.layout.clear_item_list, null);
            TextView separatorView = (TextView) view.findViewById(R.id.clear);
            separatorView.setText("Remove tick items");
            separatorView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Text);
                    builder.setTitle("you want to delete ?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            removeBought();
                            resetView();
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
            view = inflater.inflate(R.layout.shopping_item_list, null);
            viewBinderHelper.bind((SwipeRevealLayout)view.findViewById(R.id.swipe_layout),IL.get(p).getName());
            final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.nameLabel);
            final ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);
            ImageView editImage = view.findViewById(R.id.edit_button);
            editImage.setVisibility(View.VISIBLE);
            editImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editItem(p);
                }
            }
            );

            final ItemList item = IL.get(p);
            if (item.isBought())
            {
                //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_on);
                simpleCheckedTextView.setPaintFlags(simpleCheckedTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                simpleCheckedTextView.setChecked(true);
            }
            else
            {
                //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_button);
                simpleCheckedTextView.setPaintFlags(simpleCheckedTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                simpleCheckedTextView.setTextColor(Text.getColor(android.R.color.black));
                simpleCheckedTextView.setChecked(false);
            }
            simpleCheckedTextView.setText(item.getName() + " x " + item.getQuantity());
            simpleCheckedTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (simpleCheckedTextView.isChecked())
                    {
                        item.setBought(false);
                        if (DB.updateListItem(item))
                        {
                            IL.get(p).setBought(false);
                            resetView();
                        }
                    }
                    else
                    {
                        item.setBought(true);
                        if (DB.updateListItem(item))
                        {
                            IL.get(p).setBought(true);
                            resetView();

                        }
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
                    builder.setTitle("Are you sure you want to delete?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            if (DB.deleteListItem(item))
                            {
                                IL.remove(p);
                                resetView();
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
 }
