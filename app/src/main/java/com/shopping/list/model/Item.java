package com.shopping.list.model;

 public class Item
 {
     private String name;
     private int itemID;
     private boolean separator;
     private boolean addItem;

     public String getName()
     {
         return name;
     }

     public void setName(String name)
     {
         this.name=name;
     }

     public int getItemID()
     {
         return itemID;
     }

     public void setItemID(int itemID)
     {
         this.itemID=itemID;
     }

     public boolean isSeparator()
     {
         return separator;
     }

     public void setSeparator(boolean separator)
     {
         this.separator=separator;
     }

     public boolean isAddItem()
     {
         return addItem;
     }

     public void setAddItem(boolean addItem)
     {
         this.addItem=addItem;
     }

     public Item()
     {
         this.setItemID(-1);
         this.setAddItem(false);
         this.setSeparator(false);
     }

     public Item(String name)
     {
         this.setName(name);
         this.setItemID(-1);
         this.setAddItem(false);
     }

     public Item(String name,int id,boolean separator)
     {
         this.setName(name);
         this.setItemID(id);
         this.setSeparator(separator);
         this.setAddItem(false);
     }
 }
