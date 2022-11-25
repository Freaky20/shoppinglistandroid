package com.shopping.list;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    //This is a helper class for basic creation of the SQL lite database
    //Code is based off this https://camposha.info/android-swipe-tabs-sqlite-fragments-with-listview/

    //SQlLite Create statement
    private Context context;

    // Constructor
    public DataBaseHelper(Context context)
    {
        super(context, "shopping_DB", null, 1);
        this.context = context;
    }

    //CREATE TABLE

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(context.getResources().getString(R.string.CREATE_TABLE_LIST));
            db.execSQL(context.getResources().getString(R.string.CREATE_TABLE_LOCATION));
            db.execSQL(context.getResources().getString(R.string.CREATE_TABLE_ITEM));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //UPGRADE TABLE

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL("DROP TABLE IF EXISTS List");
            db.execSQL(context.getResources().getString(R.string.CREATE_TABLE_LIST));
            db.execSQL("DROP TABLE IF EXISTS Location");
            db.execSQL(context.getResources().getString(R.string.CREATE_TABLE_LOCATION));
            db.execSQL("DROP TABLE IF EXISTS Item");
            db.execSQL(context.getResources().getString(R.string.CREATE_TABLE_ITEM));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
