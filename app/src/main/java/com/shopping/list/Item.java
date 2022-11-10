package com.shopping.list;

public class Item {
    private String name;
    private int quantity;
    private boolean bought;
    private int itemID;

    public Item(){
        this.setBought(false);
        this.setItemID(-1);
    }

    public Item(String name, int quantity){
        this.setName(name);
        this.setQuantity(quantity);
        this.setBought(false);
        this.setItemID(-1);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
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

    public int getItemID() {

        return itemID;
    }

    public void setItemID(int itemID) {

        this.itemID = itemID;
    }
}
