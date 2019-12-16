package com.serge.dating.mapenzi.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.TopNavigationViewHelper;
import com.serge.dating.mapenzi.Utils.User;

import java.util.HashMap;
import java.util.Map;


public class BtnDislikeActivity extends AppCompatActivity {
    private static final String TAG = "BtnDislikeActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = BtnDislikeActivity.this;
    private ImageView dislike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_dislike);

        setupTopNavigationView();
        dislike = findViewById(R.id.dislike);

        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra("url");

        switch (profileUrl) {
            case "defaultFemale":
                Glide.with(mContext).load(R.drawable.default_woman).into(dislike);
                break;
            case "defaultMale":
                Glide.with(mContext).load(R.drawable.default_man).into(dislike);
                break;
            default:
                Glide.with(mContext).load(profileUrl).into(dislike);
                break;
        }

        new Thread(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            Intent mainIntent = new Intent(BtnDislikeActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }).start();
    }

    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationViewEx tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
