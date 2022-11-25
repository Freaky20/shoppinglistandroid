package com.shopping.list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class AddItemActivity extends AppCompatActivity
            implements ItemViewAdapter.AddItem{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        sortView();
    }

    //Method that opens link shop activity from the card adapter
    public void sendItem(String name, int quantity){
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("quantity", quantity);
        setResult(RESULT_OK, intent);
        finish();
    }


    public void sortView(){

        DataBase db = new DataBase(this);

        // Creating our custom adapter
        ItemViewAdapter adapter = new ItemViewAdapter(this, db.retrieveItemsSorted());

        // Create the list view and bind the adapter
        ListView listView = (ListView) findViewById(R.id.itemView);
        listView.setAdapter(adapter);
    }
}
