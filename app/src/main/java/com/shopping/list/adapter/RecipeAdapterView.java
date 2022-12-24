package com.shopping.list.adapter;

import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.shopping.list.R;

import com.shopping.list.model.RecipeItem;

import java.util.ArrayList;

 public class RecipeAdapterView extends RecyclerView.Adapter<RecipeAdapterView.DataObjectHolder>
 {
    private Context text;
    private ArrayList<RecipeItem> recipeItems;
    private View view;
    private static ShoppingListViewAdapter.MyClickListener myClickListener;
    public RecipeAdapterView(Context text, ArrayList<RecipeItem> recipeItems)
    {
        this.text = text;
        this.recipeItems = recipeItems;
    }

    @NonNull
    @Override
    public RecipeAdapterView.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item_list,parent,false);
        DataObjectHolder dataObjectHolder=new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapterView.DataObjectHolder holder,int position)
    {
        RecipeItem item = recipeItems.get(position);
        holder.nameLabel.setText(item.getTitle());
    }

    @Override
    public int getItemCount()
    {
        return recipeItems.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView nameLabel;
        public DataObjectHolder(@NonNull View itemView)
        {
            super(itemView);
            nameLabel=itemView.findViewById(R.id.nameLabel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ShoppingListViewAdapter.MyClickListener myClickListener)
    {
        this.myClickListener = myClickListener;
    }
 }
