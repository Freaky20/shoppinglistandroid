package com.shopping.list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.shopping.list.ui.ItemList;

import java.util.ArrayList;

public class DataBase {

    private Context context;
    private SQLiteDatabase DB;
    public DataBaseHelper Help;

    // INITIALIZE DB HELPER AND PASS IT A CONTEXT
    public DataBase(Context context) {
        this.context = context;
        this.Help = new DataBaseHelper(context);
    }

    public void setupItem(){
        try {
            DB = Help.getWritableDatabase();
            if (DB.rawQuery("SELECT * FROM Item", null).getCount() < 1) {

                String[] items = new String[]{
                        "Apple",
                        "Aubergine",
                        "Beetroot",
                        "Avocado",
                        "Apricot",
                        "Asparagus",
                        "Broccoli",
                        "Cherry",
                        "Dried fruit",
                        "Garlic",
                        "Grade",
                        "Guava",
                        "Honeydew melon",
                        "Iceberg lettuce",
                        "IIlama fruit",
                        "Jackfruit",
                        "Kale",
                        "Lemon",
                        "Leek",
                        "Melon",
                        "Mango",
                        "Mushroom",
                        "Nut",
                        "Nectarine",
                        "Banana",
                        "Beans",
                        "Milk",
                        "Cereal",
                        "Olive",
                        "Peanut",
                        "Pineapple",
                        "Quince",
                        "Radish",
                        "Strawberry",
                        "Sweet potato",
                        "Cheese",
                        "Carrot",
                        "Rice",
                        "Fish",
                        "Pasta",
                        "Chicken",
                        "Nut Butter",
                        "Spinach",
                        "Tomato",
                        "Vine leaf",
                        "Watermelon",
                        "Yuzu",
                        "Zucchini",
                        "Bread",
                        "Flour",
                        "Eggs"

                };

                ContentValues insertValues = new ContentValues();

                for (String item : items) {

                    insertValues.put("name", item);
                    DB.insert(context.getString(R.string.ITEM_TABLE), null, insertValues);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        } finally{
            Help.close();
        }
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
    public int saveLocation(Location location) {
        try {
            DB = Help.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", location.getName());
            values.put("latitude", location.getLatitude());
            values.put("longitude", location.getLongitude());
            values.put("geofence", location.isGeofence());

            long result = DB.insert("Location", "id", values);
            if (result > 0) {
                return (int)result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return 0;
    }

    public boolean checkLocationExist(Location location){
        try {
            DB = Help.getWritableDatabase();
            String []columns = {"id", "name"};
            String []selectionArgs = {location.getName() + "%"};
            //DB.query("Location", columns,"name LIKE ?",selectionArgs,null,null,null);
            //Cursor cursor = DB.rawQuery("SELECT * FROM Location Where name like '" + location.getName() + "'",null);
            //if(DB.query("Location", new String[] {"id","name"},"name LIKE '?'", new String[]{location.getName()+"%"}, null, null, null).getCount() > 0){
            if(DB.query("Location", columns,"name LIKE ?",selectionArgs,null,null,null).getCount() > 0){
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

            int result = DB.update(context.getString(R.string.ITEM_TABLE), values, "id = ?", new String[] { String.valueOf(item.getItemID()) });

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
            int result = DB.delete(context.getString(R.string.ITEM_TABLE), "id = ?", new String[] { String.valueOf(item.getItemID()) });

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

    //SAVE DATA TO DB
    public int saveItem(Item item) {
        try {
            DB = Help.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", item.getName());

            long result = DB.insert(context.getString(R.string.ITEM_TABLE), "id", contentValues);
            if (result > 0) {
                return (int)result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return 0;
    }

    //Relieving items from the SQL lite Database
    public ArrayList<Item> retrieveItemsSorted() {

        ArrayList<Item> items = new ArrayList<>();

        try {
            DB = Help.getWritableDatabase();

            Cursor cursor = DB.rawQuery("SELECT * FROM Item Order by name ASC",null);

            int position = 0;
            boolean isSeparator = false;

            while(cursor.moveToNext()) {
                isSeparator = false;

                String name = cursor.getString(1);
                int id = cursor.getInt(0);

                char[] nameArray;

                // If it is the first item then need a separator
                if (position == 0) {
                    isSeparator = true;
                    nameArray = name.toCharArray();
                }
                else {
                    // Move to previous
                    cursor.moveToPrevious();

                    // Get the previous contact's name
                    String previousName = cursor.getString(1);

                    // Convert the previous and current contact names
                    // into char arrays
                    char[] previousNameArray = previousName.toCharArray();
                    nameArray = name.toCharArray();

                    // Compare the first character of previous and current contact names
                    if (nameArray[0] != previousNameArray[0]) {
                        isSeparator = true;
                    }

                    // Don't forget to move to next
                    // which is basically the current item
                    cursor.moveToNext();
                }

                // Need a separator? Then create a Contact
                // object and save it's name as the section
                // header while pass null as the phone number
                if (isSeparator) {
                    Item item = new Item(String.valueOf(nameArray[0]), id, true);
                    items.add( item );
                }

                // Create a Contact object to store the name/number details
                Item item = new Item(name, id, false);
                items.add( item );

                position++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Help.close();
        }

        return items;
    }

    //Relieving items from the SQL lite Database
    public ArrayList<Item> retrieveItems() {
        ArrayList<Item> arrayList = new ArrayList<>();

        try {
            DB = Help.getWritableDatabase();

            Cursor cursor = DB.rawQuery("SELECT * FROM Item",null);

            Item item;
            arrayList.clear();

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                item = new Item();
                item.setName(name);
                item.setItemID(id);

                arrayList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return arrayList;
    }

    public int[] checkItemExist(int id){
        int[] values = new int[2];
        try {
            DB = Help.getWritableDatabase();
            Cursor cursor = DB.rawQuery("SELECT id, quantity FROM List Where itemID = " + id,null);

            if(cursor.getCount() > 0){
                while (cursor.moveToNext()) {
                    values[0] = cursor.getInt(0);
                    values[1] = cursor.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Help.close();
        }

        return values;
    }

    //UPDATE Item
    public boolean updateListItem(ItemList item){
        try{
            DB = Help.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("quantity", item.getQuantity());
            values.put("bought", item.isBought());

            int result = DB.update("List", values, "id = ?", new String[] { String.valueOf(item.getItemListID()) });

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
    public boolean deleteListItem(ItemList item){
        try{
            DB = Help.getWritableDatabase();
            int result = DB.delete("List", "id = ?", new String[] { String.valueOf(item.getItemListID()) });

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

    //SAVE DATA TO DB
    public boolean saveListItem(ItemList item) {
        try {
            DB = Help.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("itemID", item.getItemID());
            contentValues.put("quantity", item.getQuantity());
            contentValues.put("bought", item.isBought());

            long result = DB.insert("List", "id", contentValues);
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

    //Relieving items from the SQL lite Database
    public ArrayList<ItemList> retrieveListItems(int sort) {
        ArrayList<ItemList> arrayList = new ArrayList<>();

        try {
            DB = Help.getWritableDatabase();

            String extra = "";
            switch (sort){
                case 1:
                    extra = " ORDER by Item.name ASC";
                    break;
                case 2:
                    extra = " ORDER by Item.name DESC";
                    break;
            }
            Cursor cursor = DB.rawQuery("SELECT List.id, List.itemID, List.quantity, List.bought, Item.name FROM List JOIN Item on List.itemID = Item.id" + extra,null);

            ItemList item;
            arrayList.clear();

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                int itemID = cursor.getInt(1);
                int quantity = cursor.getInt(2);
                boolean bought = (cursor.getInt(3) != 0);
                String name = cursor.getString(4);

                item = new ItemList();
                item.setItemID(itemID);
                item.setQuantity(quantity);
                item.setBought(bought);
                item.setItemListID(id);
                item.setName(name);

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