package com.serge.dating.mapenzi.Matched;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.CalculateAge;
import com.serge.dating.mapenzi.Utils.User;

import me.relex.circleindicator.CircleIndicator;


public class ProfileCheckinMatched extends AppCompatActivity {
    private static final String TAG = "ProfileCheckinMatched";

    private User user;
    private Context mContext = ProfileCheckinMatched.this;
    private Button sendSMSButton, sendEmailButton;
    private int distance;


    //

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_checkin_matched);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        distance = intent.getIntExtra("distance", 1);

        Log.d(TAG, "onCreate: user name is" + user.getUsername());

        TextView toolbar = findViewById(R.id.toolbartag);
        toolbar.setText("Mapenzi");

        sendSMSButton = findViewById(R.id.send_sms);
        sendEmailButton = findViewById(R.id.send_email);

        TextView profile_name = findViewById(R.id.profile_name);
        TextView profile_distance = findViewById(R.id.profile_distance);

        TextView profile_email = findViewById(R.id.profile_email);
        ImageView imageView = findViewById(R.id.image_matched);
        TextView profile_bio = findViewById(R.id.bio_match);
        TextView profile_interest = findViewById(R.id.interests_match);

        CalculateAge cal = new CalculateAge(user.getProfile().getDateOfBirth());
        int age = cal.getAge();

        profile_name.setText(user.getUsername() + ", " + age);
        profile_email.setText(user.getEmail());

        String append = (distance == 1) ? "mile away" : "miles away";
        profile_distance.setText(distance + " " + append);


        try {
            if (user.getProfile().getDescription().length() != 0) {
                profile_bio.setText(user.getProfile().getDescription());
            }


            //append interests
            StringBuilder interest = new StringBuilder();
            if (user.getProfile().isSports()) {
                interest.append("Sports   ");
            }
            if (user.getProfile().isFishing()) {
                interest.append("Fishing   ");
            }
            if (user.getProfile().isMusic()) {
                interest.append("Music   ");
            }
            if (user.getProfile().isTravel()) {
                interest.append("Travel   ");
            }

            profile_interest.setText(interest.toString());

            String profileImageUrl = user.getProfileImageUrl();
            switch (profileImageUrl) {
                case "defaultFemale":
                    Glide.with(mContext).load(R.drawable.default_woman).into(imageView);
                    break;
                case "defaultMale":
                    Glide.with(mContext).load(R.drawable.default_man).into(imageView);
                    break;
                default:
                    Glide.with(mContext).load(profileImageUrl).into(imageView);
                    break;
            }

        }catch (NullPointerException ex){

        }

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
        sendSMSButton.setOnClickListener(v -> sendSMS(user));

        sendEmailButton.setEnabled(false);
        sendEmailButton.setOnClickListener(v -> sendEmail(user.getEmail(), user.getUsername()));


        myPager = new MyPager(this,user);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(myPager);
        circleIndicator = findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);

    }


    // This method will be called when send sms button in matched profile will be clicked. This open the default sms app.
    private void sendSMS(User user) {
        String userName = user.getUsername();
        String userImage = user.getProfileImageUrl();
        String userId = user.getUser_id();
        String onlineStatus = user.getOnlineStatus().toString();
        Intent startMessaging = new Intent(this, MessagingActivity.class);
        startMessaging.putExtra("userName", userName);
        startMessaging.putExtra("userImage", userImage);
        startMessaging.putExtra("classUser", user);
        startMessaging.putExtra("userId", userId);
        startMessaging.putExtra("onlineStatus", onlineStatus);
        this.startActivity(startMessaging);
    }

    // This method will be called when send email button in matched profile will be clicked. This open the default email app.
    private void sendEmail(String email, String userName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding our Pink Moon Match!!!");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi " + userName + ", \n" + "Love to have a coffee with you!!!!");
        startActivity(Intent.createChooser(intent, ""));
    }

}
