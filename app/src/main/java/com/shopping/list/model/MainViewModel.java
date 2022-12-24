package com.shopping.list.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

 public class MainViewModel extends ViewModel
 {
    private MutableLiveData<String> mText;

    public MainViewModel()
    {
        mText = new MutableLiveData<>();
    }

    public void addTitle(String title)
    {
        mText.postValue(title);
    }

    public LiveData<String> getText()
    {
        return mText;
    }
 }