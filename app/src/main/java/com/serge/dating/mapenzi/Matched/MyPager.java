package com.serge.dating.mapenzi.Matched;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.Images;
import com.serge.dating.mapenzi.Utils.Profile;
import com.serge.dating.mapenzi.Utils.User;

import java.util.ArrayList;
import java.util.List;

public class MyPager extends PagerAdapter {

    private Context context;

    private int c = 0;
    private User user;
    public MyPager(Context context,User user) {

        this.user = user;
        this.context = context;
        try {
            this.c = user.getProfile().getImages().size();
        }catch (NullPointerException ex){
            this.c = 0;
        }

    }

    /*
    This callback is responsible for creating a page. We inflate the layout and set the drawable
    to the ImageView based on the position. In the end we add the inflated layout to the parent
    container .This method returns an object key to identify the page view, but in this example page view
    itself acts as the object key
    */

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.sliding_images_layout, null);
        ImageView imageView = view.findViewById(R.id.image);

        if (this.c == 0){

            switch (user.getProfileImageUrl()) {
                case "defaultFemale":
                    Glide.with(context).load(R.drawable.default_woman).into(imageView);
                    break;
                case "defaultMale":
                    Glide.with(context).load(R.drawable.default_man).into(imageView);
                    break;
                default:
                    Glide.with(context).load(user.getProfileImageUrl()).into(imageView);
                    break;
            }

        }else{
            switch (getUrl(position)) {
                case "defaultFemale":
                    Glide.with(context).load(R.drawable.default_woman).into(imageView);
                    break;
                case "defaultMale":
                    Glide.with(context).load(R.drawable.default_man).into(imageView);
                    break;
                default:
                    Glide.with(context).load(getUrl(position)).into(imageView);
                    break;
            }
        }


        container.addView(view);
        return view;
    }

    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {

        container.removeView((View) view);

    }

    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {
        if (this.c == 0){
            return 1;
        }else {
            return this.c;
        }

    }

    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }


    private String getUrl(int position){

        String imageUrl = user.getProfileImageUrl();
        if(user!=null){

            Profile profile = user.getProfile();
            List<Images> images;
            if (profile != null){
                images = profile.getImages();
                if (images != null){
                    try {
                        imageUrl =images.get(position).getImageUrl();
                    }catch (NullPointerException ex){

                        imageUrl = user.getProfileImageUrl();
                    }
                }

            }
        }
        return imageUrl;

    }


}