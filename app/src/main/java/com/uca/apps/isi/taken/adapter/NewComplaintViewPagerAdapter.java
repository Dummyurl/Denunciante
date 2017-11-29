package com.uca.apps.isi.taken.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.RelativeLayout;

import com.uca.apps.isi.taken.R;

import java.util.ArrayList;



public class NewComplaintViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    private Context context;
    private ArrayList<Uri> imageComplaintUri;
    private boolean isEmpty;

    public NewComplaintViewPagerAdapter(Context context, ArrayList<Uri> imageComplaintUri) {
        this.context = context;

        this.imageComplaintUri = imageComplaintUri;
    }

    @Override
    public int getCount() {
        if(imageComplaintUri==null || imageComplaintUri.isEmpty())
        {
            isEmpty = true ;
            return 1;

        }
        else
        {
            isEmpty = false ;
            return imageComplaintUri.size();
        }

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==  object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables

        ImageView imageComplaint;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_view_pager, container,
                false);

        // Locate the TextViews in viewpager_item.xml


        // Capture position and set to the TextViews

        // Locate the ImageView in viewpager_item.xml
        imageComplaint = itemView.findViewById(R.id.actualImageComplaint);
        // Capture position and set to the ImageView

        if(isEmpty)
        {
            imageComplaint.setImageResource(R.drawable.ic_default_image);
        }
        else
        {
            imageComplaint.setImageURI(imageComplaintUri.get(position));
        }




        // Add viewpager_item.xml to ViewPager
        (container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        (container).removeView((RelativeLayout) object);

    }
}
