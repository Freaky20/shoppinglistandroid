package com.shopping.list;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;

public class DataBaseHelper extends SQLiteOpenHelper {
    //This is a helper class for basic creation of the SQL lite database
    //Code is based off this https://camposha.info/android-swipe-tabs-sqlite-fragments-with-listview/

    //SQlLite Create statement
    private static final String CREATE_TABLE = "CREATE TABLE List(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity INTEGER NOT NULL, bought INTEGER DEFAULT 0);";

    // Constructor
    public DataBaseHelper(Context context) {

        super(context, "shopping_DB", null, 1);
    }

    //CREATE TABLE

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(CREATE_TABLE);
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //UPGRADE TABLE

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL("DROP TABLE IF EXISTS List");
            db.execSQL(CREATE_TABLE);

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
