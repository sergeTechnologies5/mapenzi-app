package com.serge.dating.mapenzi.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serge.dating.mapenzi.Matched.MyPager;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.User;

import me.relex.circleindicator.CircleIndicator;


public class ProfileCheckinMain extends AppCompatActivity {

    private Context mContext;
    String profileImageUrl;

    //

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_checkin_main);

        mContext = ProfileCheckinMain.this;

        ImageButton downArrow = findViewById(R.id.downArrow);


        downArrow.setOnClickListener(v -> onBackPressed());

        TextView profileName = findViewById(R.id.name_main);
        ImageView profileImage = findViewById(R.id.profileImage);
        TextView profileBio = findViewById(R.id.bio_beforematch);
        TextView profileInterest = findViewById(R.id.interests_beforematch);
        TextView profileDistance = findViewById(R.id.distance_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String bio = intent.getStringExtra("bio");
        String interest = intent.getStringExtra("interest");
        int distance = intent.getIntExtra("distance", 1);
        String append = (distance == 1) ? "mile away" : "miles away";

        profileDistance.setText(distance + " " + append);
        profileName.setText(name);
        profileBio.setText(bio);
        profileInterest.setText(interest);

        profileImageUrl = intent.getStringExtra("photo");
        switch (profileImageUrl) {
            case "defaultFemale":
                Glide.with(mContext).load(R.drawable.default_woman).into(profileImage);
                break;
            case "defaultMale":
                Glide.with(mContext).load(R.drawable.default_man).into(profileImage);
                break;
            default:
                Glide.with(mContext).load(profileImageUrl).into(profileImage);
                break;
        }

        User user  = (User) intent.getSerializableExtra("classUser");

        myPager = new MyPager(this,user);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(myPager);
        circleIndicator = findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);
    }


    public void DislikeBtn(View v) {
            Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
            btnClick.putExtra("url", profileImageUrl);
            startActivity(btnClick);

    }

    public void LikeBtn(View v) {
            Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
            btnClick.putExtra("url", profileImageUrl);
            startActivity(btnClick);

    }

    public void MessageBtn(View v) {

    }

}
