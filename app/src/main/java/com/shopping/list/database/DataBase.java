package com.shopping.list.database;

import android.content.ContentValues;

import android.content.Context;

import android.database.Cursor;

import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;

import com.shopping.list.model.Item;

import com.shopping.list.model.ItemList;

import com.shopping.list.model.Location;

import com.shopping.list.model.ShoppingList;

import com.shopping.list.R;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;

import java.util.Locale;

 public class DataBase {
     private Context Text;
     private SQLiteDatabase DB;
     public DataBaseHelper Help;

     public DataBase(Context text)
     {
         this.Text=text;
         this.Help=new DataBaseHelper(text);
     }

     public void setupItem()
     {
         try
         {
             DB = Help.getWritableDatabase();
             if(DB.rawQuery("SELECT * FROM Item",null).getCount() < 1)
             {
                 String[] items = new String[]
                         {
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
                                 "IIla-ma fruit",
                                 "Jack-fruit",
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
                 ContentValues insertValues=new ContentValues();
                 for (String item:items)
                 {
                     insertValues.put("name",item);
                     DB.insert(Text.getString(R.string.ITEM_TABLE),null,insertValues);
                 }
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
     }

     public int saveItem(Item i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues contentValues=new ContentValues();
             contentValues.put("name", i.getName());
             long result=DB.insert(Text.getString(R.string.ITEM_TABLE), "id", contentValues);
             if (result>0)
             {
                 return (int) result;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return 0;
     }

     public int saveShopList(ShoppingList i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues contentValues=new ContentValues();
             contentValues.put("name",i.getName());
             contentValues.put("lastLocation",i.getLastLocationID());
             long result=DB.insert(Text.getString(R.string.SHOP_LIST_TABLE),"id",contentValues);
             if (result>0)
             {
                 return (int) result;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return 0;
     }

     public int saveLocation(Location l)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues values=new ContentValues();
             values.put("name", l.getName());
             values.put("latitude", l.getLatitude());
             values.put("longitude", l.getLongitude());
             values.put("geofence", l.isGeofence());
             values.put("shopID", l.getShoppingListID());
             long result=DB.insert(Text.getString(R.string.LOCATION_TABLE), "id", values);
             if(result>0)
             {
                 return (int) result;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return 0;
     }

     public boolean saveListItem(ItemList i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues contentValues=new ContentValues();
             contentValues.put("itemID",i.getItemID());
             contentValues.put("quantity",i.getQuantity());
             contentValues.put("bought",i.isBought());
             contentValues.put("shopID",i.getShoppingListID());
             long result=DB.insert(Text.getString(R.string.LIST_TABLE), "id",contentValues);
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean saveHistory(int shopID,int locationID)
     {
         try
         {
             ArrayList<ItemList> itemListArrayList=retrieveListItems(1, shopID);
             if(itemListArrayList.size()>0)
             {
                 DB=Help.getWritableDatabase();
                 for(ItemList itemList:itemListArrayList)
                 {
                     ContentValues values=new ContentValues();
                     values.put("itemID",itemList.getItemID());
                     values.put("locationID",locationID);
                     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                     Date date=new Date();
                     values.put("date",dateFormat.format(date));
                     values.put("count",getItemHistoryCount(itemList.getItemID(), locationID) + 1);
                     int update=DB.update(Text.getString(R.string.HISTORY_TABLE),values,"itemID = ? AND locationID = ?",new String[]{String.valueOf(itemList.getItemID()),String.valueOf(locationID)});
                     if(update==0)
                     {
                         DB.insertWithOnConflict(Text.getString(R.string.HISTORY_TABLE),null,values,SQLiteDatabase.CONFLICT_REPLACE);
                     }
                 }
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public String getShopListName(int shopListID)
     {
         String shopName="";
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT name FROM Shopping WHERE id = " + shopListID,null);
             while(cursor.moveToNext())
             {
                 shopName=cursor.getString(0);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return shopName;
     }

     public int getTotalListItems(int shopID)
     {
         int count=0;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT COUNT(id) FROM List WHERE deleted = 0 AND shopid = " + shopID,null);
             while(cursor.moveToNext())
             {
                 count=cursor.getInt(0);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return count;
     }

     public int getBoughtCount(int shopID)
     {
         int count=0;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT bought, COUNT(bought) FROM List where deleted = 0 AND shopid = " + shopID + " GROUP BY bought", null);
             while(cursor.moveToNext())
             {
                 boolean bought=(cursor.getInt(0) != 0);
                 if(bought)
                 {
                     count=cursor.getInt(1);
                 }
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return count;
     }

     public String getLinkedItemListNames(int itemID)
     {
         String listNames="";
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT Shopping.name, List.quantity FROM List join Shopping ON List.shopID = Shopping.id where List.deleted = 0 AND List.itemID = "+itemID,null);
             StringBuilder stringBuilder=new StringBuilder();
             while(cursor.moveToNext())
             {
                 String shopListName=cursor.getString(0);
                 int amount=cursor.getInt(1);
                 stringBuilder.append(shopListName+"("+amount+")"+",");
             }
             if(stringBuilder.length()>0)
             {
                 listNames=stringBuilder.toString().substring(0,stringBuilder.length() - 1);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return listNames;
     }

     public int getItemLocationHistory(int itemID)
     {
         int locationID=-1;
         boolean found=false;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT locationID FROM History where itemID = " + itemID + " ORDER BY count, date DESC",null);
             while(cursor.moveToNext()&&!found)
             {
                 locationID=cursor.getInt(0);
                 found=true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return locationID;
     }

     public int getItemListCount(int shopID)
     {
         int count=0;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT Count(id) FROM List where deleted = 0 AND shopID = "+shopID,null);
             while(cursor.moveToNext())
             {
                 count=cursor.getInt(0);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
             Help.close();
         }
         return count;
     }

     public ShoppingList getShopList(int shopID)
     {
         ShoppingList shoppingList = new ShoppingList();
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT * FROM Shopping WHERE id = " + shopID,null);
             while(cursor.moveToNext())
             {
                 int id=cursor.getInt(0);
                 String name=cursor.getString(1);
                 int lastLocation=cursor.getInt(2);
                 shoppingList.setName(name);
                 shoppingList.setShoppingListID(id);
                 shoppingList.setLastLocationID(lastLocation);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return shoppingList;
     }

     public int getItemHistoryCount(int itemID,int locationID)
     {
         int count=0;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT count FROM History where itemID = "+ itemID+" AND locationID = "+ locationID,null);
             while(cursor.moveToNext())
             {
                 count=cursor.getInt(0);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
             Help.close();
         }
         return count;
     }

     public Location getLocation(int id)
     {
         Location l=new Location();
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT * FROM Location WHERE id = " + id,null);
             while(cursor.moveToNext())
             {
                 int locationID=cursor.getInt(0);
                 String name=cursor.getString(1);
                 double latitude=cursor.getDouble(2);
                 double longitude=cursor.getDouble(3);
                 boolean geofence=(cursor.getInt(4) != 0);
                 int shopListID=cursor.getInt(5);
                 l.setName(name);
                 l.setLatitude(latitude);
                 l.setLongitude(longitude);
                 l.setGeofence(geofence);
                 l.setLocationID(locationID);
                 l.setShoppingListID(shopListID);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return l;
     }

     public boolean updateListItem(ItemList i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues values=new ContentValues();
             values.put("quantity",i.getQuantity());
             values.put("bought",i.isBought());
             int result=DB.update(Text.getString(R.string.LIST_TABLE), values, "id = ?", new String[]{String.valueOf(i.getItemListID())});
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean updateShopList(ShoppingList i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues values=new ContentValues();
             values.put("name",i.getName());
             values.put("lastLocation",i.getLastLocationID());
             int result=DB.update(Text.getString(R.string.SHOP_LIST_TABLE), values, "id = ?", new String[]{String.valueOf(i.getShoppingListID())});
             if (result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean updateLocation(Location l)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues values=new ContentValues();
             values.put("name",l.getName());
             values.put("latitude",l.getLatitude());
             values.put("longitude",l.getLongitude());
             values.put("geofence",l.isGeofence());
             values.put("shopID",l.getShoppingListID());
             int result=DB.update(Text.getString(R.string.LOCATION_TABLE),values,"id = ?",new String[]
                     {
                             String.valueOf(l.getLocationID())
                     }
                     );
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public int[] checkItemListExist(int itemID, int shopID)
     {
         int[] values=new int[2];
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT id, quantity FROM List Where deleted = 0 AND itemID = " + itemID + " AND shopID = " + shopID, null);
             if(cursor.getCount()>0)
             {
                 while(cursor.moveToNext())
                 {
                     values[0]=cursor.getInt(0);
                     values[1]=cursor.getInt(1);
                 }
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return values;
     }

     public int checkForlinkShoppingList(int shopID)
     {
         int locationID=0;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT id FROM Location where geofenced = 1 AND shopid = " + shopID, null);
             while(cursor.moveToNext())
             {
                 locationID=(cursor.getInt(0));
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return locationID;
     }

     public boolean checkListIsGeofence(int shopID)
     {
         boolean geofence=false;
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT geofence FROM Location where geofence = 1 AND shopID = " + shopID,null);
             if(cursor.getCount() > 0)
             {
                 geofence=true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return geofence;
     }

     public boolean checkLocationExist(Location l)
     {
         try
         {
             DB=Help.getWritableDatabase();
             String[] columns={"id", "name"};
             String[] selectionArgs={l.getName() + "%"};
             if(DB.query(Text.getString(R.string.LOCATION_TABLE), columns, "name LIKE ?",selectionArgs,null,null,null).getCount()>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean checkItemExist(String name)
     {
         try
         {
             DB=Help.getWritableDatabase();
             String[] columns={"id", "name"};
             String[] selectionArgs={name + "%"};
             if(DB.query(Text.getString(R.string.ITEM_TABLE), columns, "name LIKE ?",selectionArgs,null,null,null).getCount() > 0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean checkShoppingListExist(String name)
     {
         try
         {
             DB=Help.getWritableDatabase();
             String[] columns={"id", "name"};
             String[] selectionArgs={name + "%"};
             if(DB.query(Text.getString(R.string.SHOP_LIST_TABLE),columns,"name LIKE ?",selectionArgs,null,null,null).getCount() > 0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public ArrayList<Item> retrieveItemsSorted()
     {
         ArrayList<Item> items=new ArrayList<>();
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT * FROM Item Order by name ASC",null);
             int p=0;
             boolean isSeparator;
             while (cursor.moveToNext())
             {
                 isSeparator=false;
                 String name=cursor.getString(1);
                 int id=cursor.getInt(0);
                 char[] nameArray;
                 if (p==0)
                 {
                     isSeparator=true;
                     nameArray=name.toCharArray();
                 }
                 else
                 {
                     cursor.moveToPrevious();
                     String previousName=cursor.getString(1);
                     char[] previousNameArray=previousName.toCharArray();
                     nameArray=name.toCharArray();
                     if (nameArray[0]!=previousNameArray[0])
                     {
                         isSeparator=true;
                     }
                     cursor.moveToNext();
                 }
                 if(isSeparator)
                 {
                     Item item=new Item(String.valueOf(nameArray[0]), id, true);
                     items.add(item);
                 }
                 Item item=new Item(name, id, false);
                 items.add(item);
                 p++;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return items;
     }

     public ArrayList<ShoppingList> retrieveShopList()
     {
         ArrayList<ShoppingList> arrayList=new ArrayList<>();
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT * FROM Shopping",null);
             ShoppingList item;
             arrayList.clear();
             while(cursor.moveToNext())
             {
                 int id=cursor.getInt(0);
                 String name=cursor.getString(1);
                 int lastLocation=cursor.getInt(2);
                 item=new ShoppingList();
                 item.setName(name);
                 item.setShoppingListID(id);
                 item.setLastLocationID(lastLocation);
                 arrayList.add(item);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return arrayList;
     }

     public ArrayList<Item> retrieveItems()
     {
         ArrayList<Item> arrayList=new ArrayList<>();
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT * FROM Item",null);
             Item item;
             arrayList.clear();
             while (cursor.moveToNext())
             {
                 int id=cursor.getInt(0);
                 String name=cursor.getString(1);
                 item=new Item();
                 item.setName(name);
                 item.setItemID(id);
                 arrayList.add(item);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return arrayList;
     }

     public ArrayList<ItemList> retrieveListItems(int sort, int shopID)
     {
         ArrayList<ItemList> arrayList = new ArrayList<>();
         try
         {
             DB=Help.getWritableDatabase();
             String extra="";
             switch (sort)
             {
                 case 1:
                     extra=" ORDER by Item.name ASC";
                     break;

                 case 2:
                     extra=" ORDER by Item.name DESC";
                     break;

             }
             Cursor cursor = DB.rawQuery("SELECT List.id, List.itemID, List.quantity, List.bought, List.shopID, Item.name FROM List JOIN Item on List.itemID = Item.id GROUP by LIst.id HAVING List.deleted = 0 AND List.shopid = " + shopID + extra, null);
             ItemList item;
             arrayList.clear();
             while(cursor.moveToNext())
             {
                 int id=cursor.getInt(0);
                 int itemID=cursor.getInt(1);
                 int quantity=cursor.getInt(2);
                 boolean bought=(cursor.getInt(3) != 0);
                 shopID=cursor.getInt(4);
                 String name=cursor.getString(5);
                 item=new ItemList();
                 item.setItemID(itemID);
                 item.setQuantity(quantity);
                 item.setBought(bought);
                 item.setItemListID(id);
                 item.setShoppingListID(shopID);
                 item.setName(name);
                 arrayList.add(item);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return arrayList;
     }

     public ArrayList<Location> retrieveLocations()
     {
         ArrayList<Location> arrayList=new ArrayList<>();
         try
         {
             DB=Help.getWritableDatabase();
             Cursor cursor=DB.rawQuery("SELECT * FROM Location",null);
             Location l;
             arrayList.clear();
             while(cursor.moveToNext())
             {
                 int id=cursor.getInt(0);
                 String name=cursor.getString(1);
                 double latitude=cursor.getDouble(2);
                 double longitude=cursor.getDouble(3);
                 boolean geofence=(cursor.getInt(4) != 0);
                 int shopListID=cursor.getInt(5);
                 l=new Location();
                 l.setName(name);
                 l.setLatitude(latitude);
                 l.setLongitude(longitude);
                 l.setGeofence(geofence);
                 l.setLocationID(id);
                 l.setShoppingListID(shopListID);
                 arrayList.add(l);
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return arrayList;
     }

     public boolean deleteItem(Item i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.ITEM_TABLE), "id = ?", new String[]{String.valueOf(i.getItemID())});
             if (result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteListItem(ItemList i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             ContentValues values=new ContentValues();
             values.put("deleted",1);
             int result=DB.update(Text.getString(R.string.LIST_TABLE), values, "id = ?", new String[]{String.valueOf(i.getItemListID())});
             if (result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteShopList(ShoppingList i)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.SHOP_LIST_TABLE), "id = ?", new String[]{String.valueOf(i.getShoppingListID())});
             if (result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteLocation(Location l)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.LOCATION_TABLE),"id = ?",new String[]
                     {
                             String.valueOf(l.getLocationID())
                     }
             );
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteLinkedItemToList(int itemID)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.LIST_TABLE), "itemID = ?", new String[]{String.valueOf(itemID)});
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteLinkedShopToList(int shopID)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.LIST_TABLE), "shopID = ?", new String[]{String.valueOf(shopID)});
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteHistoryItemID(int itemID)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.HISTORY_TABLE),"itemID = ?",new String[]
                     {
                             String.valueOf(itemID)
                     }
             );
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }

     public boolean deleteHistoryLocationID(int locationID)
     {
         try
         {
             DB=Help.getWritableDatabase();
             int result=DB.delete(Text.getString(R.string.HISTORY_TABLE),"locationID = ?",new String[]{String.valueOf(locationID)});
             if(result>0)
             {
                 return true;
             }
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
         finally
         {
             Help.close();
         }
         return false;
     }
 }
