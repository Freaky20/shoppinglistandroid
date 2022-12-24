package com.shopping.list.ui.recipe;

import android.app.ProgressDialog;

import android.os.Build;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.text.Html;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.TextView;

import com.shopping.list.model.MainViewModel;

import com.shopping.list.R;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

public class RecipeDetailsFragment extends Fragment
 {
    private FirebaseFirestore db;
    private MainViewModel mainViewModel;
    private String Id;
    private String name;
    private View root;
    private TextView description;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Loading Recipes List, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
        if (getArguments()!=null)
        {
            Id=getArguments().getString("Id");
            name=getArguments().getString("name");
        }
        mainViewModel=ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.addTitle(name);
        getDetails(Id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        root=inflater.inflate(R.layout.fragment_recipe_details, container, false);
        description=root.findViewById(R.id.description);
        return root;
    }

    private void getDetails(String id)
    {
        progressDialog.show();
        DocumentReference docRef=db.collection("recipe_list").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists())
                    {
                       // description.setText(document.getString("description"));
                        //"<span style=\"font-size: 18\">\(product.getDescription)</span>"
                        String descriptionText = "<span style=\"font-size: 18\">"+document.getString("description")+"</span>";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        {
                            description.setText(Html.fromHtml(descriptionText, Html.FROM_HTML_MODE_LEGACY));
                        }
                        else
                        {
                            description.setText(Html.fromHtml(descriptionText));
                        }
                    }
                    progressDialog.hide();
                }
                else
                {
                    progressDialog.hide();
                }
            }
        }
        );
    }
 }
