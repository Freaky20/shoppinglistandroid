package com.shopping.list.ui.home;

import com.shopping.list.model.Item;

import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;

 public class HomeViewModel extends ViewModel
 {
    private MutableLiveData<String> mText;
    private MutableLiveData<Item> item;

    public HomeViewModel()
    {
        mText = new MutableLiveData<>();
        item = new MutableLiveData<Item>();
        mText.setValue("This is home fragment");
    }

    public void addItem(Item newItem)
    {
        item.setValue(newItem);
    }

    public LiveData<Item> getSelected()
    {
        return item;
    }

    public LiveData<String> getText()
    {
        return mText;
    }
 }