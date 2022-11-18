package com.shopping.list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DataBase {

    private Context context;
    private SQLiteDatabase DB;
    private DataBaseHelper Help;

    // INITIALIZE DB HELPER AND PASS IT A CONTEXT
    public DataBase(Context context) {
        this.context = context;
        this.Help = new DataBaseHelper(context);
    }

    //UPDATE Location
    public boolean updateLocation(Location location){
        try{
            DB = Help.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", location.getName());
            values.put("latitude", location.getLatitude());
            values.put("longitude", location.getLongitude());
            values.put("geofence", location.isGeofence());

            int result = DB.update("Location", values, "id = ?", new String[] { String.valueOf(location.getLocationID()) });

            if (result > 0) {
                return true;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }
        return false;
    }

    //DELETE FROM DATABASE
    public boolean deleteLocation(Location location){
        try{
            DB = Help.getWritableDatabase();
            int result = DB.delete("Location", "id = ?", new String[] { String.valueOf(location.getLocationID()) });

            if (result > 0) {
                return true;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }
        return false;
    }

    //SAVE Location TO DB
    public boolean saveLocation(Location location) {
        try {
            DB = Help.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", location.getName());
            values.put("latitude", location.getLatitude());
            values.put("longitude", location.getLongitude());
            values.put("geofence", location.isGeofence());

            long result = DB.insert("Location", "id", values);
            if (result > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return false;
    }

    public boolean checkLocationExist(Location location){
        try {
            DB = Help.getWritableDatabase();

            //Cursor cursor = db.rawQuery("SELECT * FROM Location Where name like '" + location.getName() + "'",null);
            if(DB.query("Location", new String[] {"id","name"},"name LIKE '?'", new String[]{location.getName()+"%"}, null, null, null).getCount() > 0){
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return false;
    }

    public int retrieveLocationID(Location location){
        int id = 0;
        try {
            DB = Help.getWritableDatabase();
            String []columns = {"id", "name"};
            String []selectionArgs = {location.getName() + "%"};
            Cursor cursor = DB.query("Location", columns,"name LIKE ?",selectionArgs,null,null,null);
            //Cursor cursor = db.query("Location", new String[] {"id","name"},"name LIKE '?'", new String[]{location.getName()+"%"}, null, null, null);
            while (cursor.moveToNext())
            {
                id = cursor.getInt(0);

            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Help.close();
        }

        return id;
    }

    //Relieving locations from the SQLite Database
    public ArrayList<Location> retrieveLocations() {
        ArrayList<Location> arrayList = new ArrayList<>();

        try {
            DB = Help.getWritableDatabase();

            Cursor cursor = DB.rawQuery("SELECT * FROM Location",null);

            Location location;
            arrayList.clear();

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double latitude = cursor.getInt(2);
                double longitude = cursor.getInt(3);
                boolean geofence = (cursor.getInt(4) != 0);

                location = new Location();
                location.setName(name);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setGeofence(geofence);
                location.setLocationID(id);

                arrayList.add(location);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return arrayList;
    }

    //UPDATE Item
    public boolean updateItem(Item item){
        try{
            DB = Help.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", item.getName());
            values.put("quantity", item.getQuantity());
            values.put("bought", item.isBought());

            int result = DB.update("List", values, "id = ?", new String[] {
                    String.valueOf(item.getItemID())
            });

            if (result > 0) {
                return true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Help.close();
        }
        return false;
    }

    //DELETE FROM DATABASE
    public boolean deleteItem(Item item){
        try{
            DB = Help.getWritableDatabase();
            int result = DB.delete("List", "id = ?", new String[] { String.valueOf(item.getItemID()) });

            if (result > 0) {
                return true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Help.close();
        }
        return false;
    }

    //SAVE DATA TO DB
    public boolean saveItem(Item item) {
        try {
            DB = Help.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", item.getName());
            contentValues.put("quantity", item.getQuantity());
            contentValues.put("bought", item.isBought());

            long result = DB.insert("List", "id", contentValues);
            if (result > 0) {
                return true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Help.close();
        }

        return false;
    }

    //Relieving items from the SQL lite Database
    public ArrayList<Item> retrieveItems() {
        ArrayList<Item> arrayList = new ArrayList<>();

        try {
            DB = Help.getWritableDatabase();

            Cursor cursor = DB.rawQuery("SELECT * FROM List",null);

            Item item;
            arrayList.clear();

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int quantity = cursor.getInt(2);
                boolean bought = (cursor.getInt(3) != 0);

                item = new Item();
                item.setName(name);
                item.setQuantity(quantity);
                item.setBought(bought);
                item.setItemID(id);

                arrayList.add(item);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Help.close();
        }

        return arrayList;
    }

}
