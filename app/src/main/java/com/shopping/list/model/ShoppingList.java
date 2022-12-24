package com.shopping.list.model;

import android.content.Context;

import com.shopping.list.database.DataBase;

 public class ShoppingList
 {
     private String name;
     private int shoppingListID;
     private int lastLocationID;

     public ShoppingList()
     {
     }

     public String getName()
     {
         return name;
     }

     public void setName(String name)
     {
         this.name=name;
     }

     public int getShoppingListID()
     {
         return shoppingListID;
     }

     public void setShoppingListID(int shoppingListID)
     {
         this.shoppingListID=shoppingListID;
     }

     public int getLastLocationID()
     {
         return lastLocationID;
     }

     public void setLastLocationID(int lastLocationID)
     {
         this.lastLocationID=lastLocationID;
     }

     public boolean checkIfGeofence(Context text)
     {
         return new DataBase(text).checkListIsGeofence(getShoppingListID());
     }

     public ShoppingList(String name)
     {
         this.name=name;
         this.setShoppingListID(-1);
     }
 }
