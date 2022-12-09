package com.shopping.list.ui;

import com.shopping.list.Item;

public class ItemList extends Item {

    private int quantity;
    private boolean bought;
    private int itemListID;
    private boolean clearBought;

    public ItemList(){
        this.setSeparator(false);
        this.setAddItem(false);
        this.setClearBought(false);
    }

    public ItemList(boolean separator){
        this.setBought(false);
        this.setQuantity(0);
        this.setItemListID(-1);
        this.setItemID(-1);
        this.setSeparator(separator);
        this.setName("Crossed Off");
        this.setAddItem(false);
        this.setClearBought(false);
    }

    public ItemList(int quantity, int id){
        this.setSeparator(false);
        this.setBought(false);
        this.setQuantity(quantity);
        this.setItemID(id);
        this.setItemListID(-1);
        this.setClearBought(false);
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(int quantity) {

        this.quantity = quantity;
    }

    public boolean isBought() {

        return bought;
    }

    public void setBought(boolean bought) {

        this.bought = bought;
    }

    public int getItemListID() {

        return itemListID;
    }

    public void setItemListID(int itemListID) {

        this.itemListID = itemListID;
    }

    public boolean isClearBought() {

        return clearBought;
    }

    public void setClearBought(boolean clearBought) {

        this.clearBought = clearBought;
    }
}
