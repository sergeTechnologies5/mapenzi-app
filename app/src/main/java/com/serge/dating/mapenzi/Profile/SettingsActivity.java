package com.serge.dating.mapenzi.Profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ImageViewCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.Settings;
import com.serge.dating.mapenzi.Utils.User;
import com.serge.dating.mapenzi.introduction.IntroductionMain;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;


public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    SeekBar distanceSeek;
    SwitchCompat displayMen, displayWomen;

    RangeSeekBar rangeSeekBar;
    TextView distance_text, age_rnge;

    private TextView tvLogout;

    int maximumDist;
    int minAgeLimit;
    int maxAgeLimit;

    Boolean interestInMen = true;

    SwitchCompat notificationMactches;
    SwitchCompat notificationMessages;
    SwitchCompat notificationMessageLikes;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    Settings mSettings;

    AppCompatImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        Toolbar toolbar = findViewById(R.id.toolBar_settings);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                updateSettings();
                return true;
            }
        });
        back = findViewById(R.id.img_settings_back);

        distanceSeek = findViewById(R.id.distance);

        displayMen = findViewById(R.id.switch_man);
        displayWomen = findViewById(R.id.switch_woman);


        distance_text = findViewById(R.id.distance_text);
        age_rnge = findViewById(R.id.age_range);
        rangeSeekBar = findViewById(R.id.rangeSeekbar);

        fetchUserSettings();
        tvLogout = findViewById(R.id.tv_settings_log_out);

        notificationMactches = findViewById(R.id.notification_new_matches);
        notificationMessages = findViewById(R.id.notification_new_messages);
        notificationMessageLikes = findViewById(R.id.notification_new_message_like);

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser();
            }
        });


        distanceSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance_text.setText(progress + " Km");
                maximumDist = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        displayMen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                displayMen.setChecked(true);
                displayWomen.setChecked(false);
            }
        });
        displayWomen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    displayWomen.setChecked(true);
                    displayMen.setChecked(false);
                }
            }
        });
        rangeSeekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {

            int min = (int) minValue;

            int x =(min<18)?18: (int)minValue;
            age_rnge.setText(x + "-" + maxValue);
            minAgeLimit = Integer.valueOf(x);
            maxAgeLimit =Integer.valueOf(maxValue.toString());

        });
        back.setOnClickListener(v -> onBackPressed());


    }

    private void fetchUserSettings() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Settings settings = user.getSettings();

                mSettings = settings;

               try {
                   setSettingsDisplay(settings);
               }catch (NullPointerException ex){

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setSettingsDisplay(Settings settings) {

        boolean showMen = settings.isShowMen();
        boolean showWomen = settings.isShowWoMen();

        long minAge = settings.getMinAge();
        long maxAge = settings.getMaxAge();
        
        maximumDist = Integer.valueOf(settings.getDistance());

        displayMen.setChecked(showMen);
        displayWomen.setChecked(showWomen);


        boolean showMatches = settings.isShowNotifications();
        boolean showMessage = settings.isShowMessages();
        boolean showMassageLike = settings.isShowLikedMessages();

        distance_text.setText(maximumDist + "Km");
        distanceSeek.setProgress(maximumDist);

        age_rnge.setText(String.format("%d-%d", minAge, maxAge));
        rangeSeekBar.setSelectedMinValue(minAge);
        rangeSeekBar.setSelectedMaxValue(maxAge);


        notificationMactches.setChecked(showMatches);
        notificationMessages.setChecked(showMessage);
        notificationMessageLikes.setChecked(showMassageLike);



    }

    private void logOutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SettingsActivity.this, IntroductionMain.class));
        finish();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateSettings();
    }

    private void updateSettings() {
        String userId = mAuth.getCurrentUser().getUid();


        String maxDistance = String.valueOf(maximumDist);

        long minAge = minAgeLimit;
        long maxAge = maxAgeLimit;


        boolean showMen = displayMen.isChecked();
        boolean showWomen = displayWomen.isChecked();


        boolean showMatches = notificationMactches.isChecked();
        boolean showMessage = notificationMessages.isChecked();
        boolean showMassageLike = notificationMessageLikes.isChecked();

        Settings settings =  new Settings(
                showMen,showWomen,maxDistance,minAge, maxAge,showMatches,showMessage,showMassageLike)
                ;

        mDatabaseReference.child(userId).child("settings").setValue(settings).addOnSuccessListener(aVoid -> Toast.makeText(SettingsActivity.this, "Sucess", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
