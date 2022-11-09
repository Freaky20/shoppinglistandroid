package com.shopping.list;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogBox extends DialogFragment {

    //Declaration and Initialisation
    public DialogResultItem dialogResultItem;             //mainly based on the dialogue result code URL: http://www.coderzheaven.com/2013/07/01/return-dialogfragment-dialogfragments-android/


    public interface DialogResultItem{             //interface that is built closely on to deliver the dialogue result back to activity code URL: http://www.coderzheaven.com/2013/07/01/return-dialogfragment-dialogfragments-android/
        void sendDialogResultItem(Item tempItem);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {                   //a procedure for handling the dialogue box's creation
        //Declaration and Initialisation
        Bundle arguments = getArguments();                                      //the passed bundle containing both the message and message title. I'm not certain if this is the most effective approach to take.
        String title = arguments.getString("title");                            //Message title
        AlertDialog.Builder builder;                                            //Build-a-dialog-alert object

        builder = buildStandardDialog();
        return builder.create();
    }

    public AlertDialog.Builder buildStandardDialog(){   //Method for creating a two-button alert dialogue box
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_item, null);
        builder.setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {          //Yes button
                        EditText textBox1;
                        dialogResultItem  = (DialogResultItem) getActivity();                          //obtaining activity
                        textBox1 = (EditText) view.findViewById(R.id.itemName);                        //obtaining the name from edited text
                        EditText textBox2 = (EditText) view.findViewById(R.id.itemQty);                 //obtaining the quantity from edited text
                        Item tempItem = new Item(textBox1.getText().toString(), Integer.parseInt(textBox2.getText().toString())); //Adding a fresh text box object
                        dialogResultItem.sendDialogResultItem(tempItem);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {           //No button
                        dialog.dismiss();
                    }
                });
        return builder;
    }
}
