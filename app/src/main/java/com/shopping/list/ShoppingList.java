package com.shopping.list;

import java.util.ArrayList;

public class ShoppingList {

    private String name;
    private ArrayList<Item> items;
    private int shoppingListID;

    public ShoppingList(){

        this.items = new ArrayList<>();
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public ArrayList<Item> getItems() {

        return items;
    }

    public void setItems(ArrayList<Item> items) {

        this.items = items;
    }

    public int getShoppingListID() {

        return shoppingListID;
    }

    public void setShoppingListID(int shoppingListID) {

        this.shoppingListID = shoppingListID;
    }
}
