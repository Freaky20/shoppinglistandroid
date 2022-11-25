package com.shopping.list;

public class Item {
    private String name;
    private int quantity;
    private boolean bought;
    private int itemID;
    private boolean seperator;

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

    public Item(String name){
        this.setName(name);
        this.setQuantity(0);
        this.setBought(false);
        this.setItemID(-1);
    }

    public Item(String name, int id, boolean seperator){
        this.setName(name);
        this.setQuantity(0);
        this.setBought(false);
        this.setItemID(id);
        this.setSeperator(seperator);
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

    public boolean isSeperator() {

        return seperator;
    }

    public void setSeperator(boolean seperator) {

        this.seperator = seperator;
    }
}
