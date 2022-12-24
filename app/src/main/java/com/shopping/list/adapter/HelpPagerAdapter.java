package com.shopping.list.adapter;

import android.content.Context;

import android.view.LayoutInflater;

import android.widget.ImageView;

import android.view.View;

import android.widget.LinearLayout;

import android.view.ViewGroup;

import com.shopping.list.R;

import androidx.viewpager.widget.PagerAdapter;

 public class HelpPagerAdapter extends PagerAdapter
 {
    private Context mText;
    private LayoutInflater mLayout;
    private int[] mSources;

    public HelpPagerAdapter(Context Text,int[] sources)
    {
        mText=Text;
        mSources=sources;
        mLayout=(LayoutInflater)mText.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

     @Override
     public Object instantiateItem(ViewGroup contain,int position)
     {
         View ViewItem=mLayout.inflate(R.layout.pager_item,contain,false);
         ImageView imageView=(ImageView)ViewItem.findViewById(R.id.imageView);
         imageView.setImageResource(mSources[position]);
         contain.addView(ViewItem);
         return ViewItem;
     }

     @Override
     public void destroyItem(ViewGroup contain,int position,Object obj)
     {
         contain.removeView((LinearLayout)obj);
     }

     @Override
     public int getCount()
     {
         return mSources.length;
     }

     @Override
     public boolean isViewFromObject(View view,Object obj)
     {
         return view==((LinearLayout)obj);
     }
 }