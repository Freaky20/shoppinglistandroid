package com.shopping.list.ui.recipe;

import android.app.ProgressDialog;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import com.shopping.list.MainActivity;

import com.shopping.list.R;

import com.shopping.list.adapter.RecipeAdapterView;

import com.shopping.list.model.RecipeItem;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


 public class RecipeListFragment extends Fragment
 {
    private View root;
    private FirebaseFirestore db;
    private RecyclerView recipe_recycler_view;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter recipeAdapter;
    private ArrayList<RecipeItem> recipeItems;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        root=inflater.inflate(R.layout.fragment_recipe_list, container, false);
        recipe_recycler_view=root.findViewById(R.id.recipe_recycler_view);
        recipe_recycler_view.setHasFixedSize(true);
        recipe_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        loadRecipe();
        return root;
    }

    private void loadRecipe(){
        progressDialog.show();
        recipeItems=new ArrayList<>();
        db.collection("recipe_list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
//                                Log.d("TAG", document.getId() + " => " + document.getData());
                                recipeItems.add(new RecipeItem(document.getId(),document.getString("title"),document.getString("description")));
                            }
                            recipeAdapter = new RecipeAdapterView(requireContext(),recipeItems);
                            recipe_recycler_view.setAdapter(recipeAdapter);
                            ((RecipeAdapterView) recipeAdapter).setOnItemClickListener((position, v) ->
                            {
                                ((MainActivity) getActivity()).openRecipeDetailsFragment(root, recipeItems.get(position).getId(),recipeItems.get(position).getTitle());
                            }
                            );
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