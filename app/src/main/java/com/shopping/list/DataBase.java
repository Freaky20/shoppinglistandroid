package com.shopping.list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class DataBase {

    Context context;
    SQLiteDatabase db;
    DataBaseHelper helper;


    // INITIALIZE DB HELPER AND PASS IT A CONTEXT
    public DataBase(Context context) {
        this.context = context;
        helper = new DataBaseHelper(context);
    }

    //SAVE DATA TO DB
    public boolean saveItem(Item item) {
        try {
            db = helper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", item.getName());
            contentValues.put("quantity", item.getQuantity());
            contentValues.put("bought", item.isBought());

            long result = db.insert("List", "id", contentValues);
            if (result > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }

        return false;
    }

    //Relieving items from the SQLlite Database
    public ArrayList<Item> retrieveItems() {
        ArrayList<Item> arrayList = new ArrayList<>();

        try {
            db = helper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM List",null);

            Item item;
            arrayList.clear();

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int quantity = cursor.getInt(2);

                item = new Item();
                item.setName(name);
                item.setQuantity(quantity);
                item.setItemID(id);

                arrayList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }

        return arrayList;
    }

}
