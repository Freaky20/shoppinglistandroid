package com.shopping.list.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import android.view.WindowManager;

import androidx.viewpager.widget.ViewPager;

import com.shopping.list.adapter.HelpPagerAdapter;

import com.shopping.list.R;

  public class HelpActivity extends AppCompatActivity
  {
     ViewPager mViewPager;

     @Override
     protected void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
         setContentView(R.layout.activity_help);
         mViewPager = (ViewPager) findViewById(R.id.pager);
         int[] mResources =
                {
                R.drawable.welcome_page
                };
         HelpPagerAdapter mCustomPagerAdapter = new HelpPagerAdapter(this, mResources);
         mViewPager.setAdapter(mCustomPagerAdapter);
    }

    public void close(View v)
    {
        finish();
    }
  }
