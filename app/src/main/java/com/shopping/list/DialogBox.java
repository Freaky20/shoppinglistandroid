package com.shopping.list;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.shopping.list.ui.home.HomeViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class DialogBox extends DialogFragment {

    //Declaration and Initialisation
    private String[] quantity = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private HomeViewModel model;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {                   //a procedure for handling the dialogue box's creation
        AlertDialog.Builder builder;                                            //Build-a-dialog-alert object
        builder = buildStandardDialog();
        model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        return builder.create();
    }

    public AlertDialog.Builder buildStandardDialog(){   //Method for creating a two-button alert dialogue box
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_item, null);
/*        final Spinner spinner = (Spinner) view.findViewById(R.id.itemQty);                 //Getting quantity from edit text

        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, quantity));
        builder.setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {          //Yes button
                        EditText textBox = (EditText) view.findViewById(R.id.itemName);                        //Getting name from edit text
                        Item tempItem = new Item(textBox.getText().toString(), Integer.parseInt(spinner.getSelectedItem().toString())); //Creating new text box object
                        model.addItem(tempItem);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {           //No button
                        dialog.dismiss();
                    }
                });*/
        return builder;
    }
}
