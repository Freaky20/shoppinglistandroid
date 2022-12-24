package com.shopping.list.database;

import android.content.Context;

import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import com.shopping.list.R;

 public class DataBaseHelper extends SQLiteOpenHelper
 {
     private Context Text;

     public DataBaseHelper(Context context)
     {
         super(context, "shopping_DB", null, 7);
         this.Text = context;
     }

     @Override
     public void onCreate(SQLiteDatabase db)
     {
         try
         {
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_SHOPPING_LIST));
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_LIST));
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_ITEM));
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_LOCATION));
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_HISTORY));
         }
         catch (SQLException e)
         {
             e.printStackTrace();
         }
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int i, int i1)
     {
         try
         {
             db.execSQL("DROP TABLE IF EXISTS Shopping");
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_SHOPPING_LIST));
             db.execSQL("DROP TABLE IF EXISTS List");
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_LIST));
             db.execSQL("DROP TABLE IF EXISTS Item");
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_ITEM));
             db.execSQL("DROP TABLE IF EXISTS Location");
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_LOCATION));
             db.execSQL("DROP TABLE IF EXISTS History");
             db.execSQL(Text.getResources().getString(R.string.CREATE_TABLE_HISTORY));
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
     }
 }

