package com.shopping.list;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.shopping.list.ui.home.HomeViewModel;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class DialogBox extends DialogFragment {

    //Declaration and Initialisation
    private String[] quantity = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private HomeViewModel model;
    //private Fragment fragment;
    //private FragmentDataPassListener mCallback;

/*    @Override public void onAttach(Context context) {
        super.onAttach(context); try {
            mCallback = (FragmentDataPassListener) context;
        }
        catch (ClassCastException e)
        { throw new ClassCastException(context.toString()+ " must implement FragmentDataPassListener in GPSmapFragment");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {                   //a procedure for handling the dialogue box's creation
        //Declaration and Initialisation
        //       Bundle arguments = getArguments();                                      //The passed bundle that contains the message title and message. Not sure if this is the best method of doing this.
        // String title = arguments.getString("title");                            //Message title
        //super.onCreate(savedInstanceState);
        AlertDialog.Builder builder;                                            //Build-a-dialog-alert object
        builder = buildStandardDialog();
        model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        return builder.create();
    }

    public AlertDialog.Builder buildStandardDialog(){   //Method for creating a two-button alert dialogue box
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_item, null);
        final Spinner spinner = (Spinner) view.findViewById(R.id.itemQty);                 //Getting quantity from edit text

        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, quantity));
        builder.setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {          //Yes button
                        EditText textBox = (EditText) view.findViewById(R.id.itemName);                        //Getting name from edit text
/*                        Bundle bundle = new Bundle();
                        bundle.putString("Name", textBox.getText().toString());
                        bundle.putInt("Quantity", Integer.parseInt(spinner.getSelectedItem().toString()));
                        Intent intent = new Intent().putExtras(bundle);
                        fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);*/
/*                        dialogResultItem  = (DialogResultItem) getActivity().getSupportFragmentManager().getFragments().get(0);                          //Getting activity
                        Item tempItem = new Item(textBox.getText().toString(), Integer.parseInt(spinner.getSelectedItem().toString())); //Creating new text box object
                        dialogResultItem.sendDialogResultItem(tempItem);*/
                        Item tempItem = new Item(textBox.getText().toString(), Integer.parseInt(spinner.getSelectedItem().toString())); //Creating new text box object
                        model.addItem(tempItem);
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
