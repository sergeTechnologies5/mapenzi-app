package com.serge.dating.mapenzi.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.serge.dating.mapenzi.Main.MainActivity;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.User;


public class RegisterHobby extends AppCompatActivity {
    private static final String TAG = "RegisterHobby";

    //User Info
    User userInfo;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    private Context mContext;
    private Button hobbiesContinueButton;
    private Button sportsSelectionButton;
    private Button travelSelectionButton;
    private Button musicSelectionButton;
    private Button fishingSelectionButton;

    private String append = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hobby);
        mContext = RegisterHobby.this;

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference("users");
        Log.d(TAG, "onCreate: started");

        Intent intent = getIntent();
        userInfo = (User) intent.getSerializableExtra("classUser");
        initWidgets();

        init();
    }

    private void initWidgets() {
        sportsSelectionButton = findViewById(R.id.sportsSelectionButton);
        travelSelectionButton = findViewById(R.id.travelSelectionButton);
        musicSelectionButton = findViewById(R.id.musicSelectionButton);
        fishingSelectionButton = findViewById(R.id.fishingSelectionButton);
        hobbiesContinueButton = findViewById(R.id.hobbiesContinueButton);

        // Initially all the buttons needs to be grayed out so this code is added, on selection we will enable it later
        sportsSelectionButton.setAlpha(.5f);
        sportsSelectionButton.setBackgroundColor(Color.GRAY);

        travelSelectionButton.setAlpha(.5f);
        travelSelectionButton.setBackgroundColor(Color.GRAY);

        musicSelectionButton.setAlpha(.5f);
        musicSelectionButton.setBackgroundColor(Color.GRAY);

        fishingSelectionButton.setAlpha(.5f);
        fishingSelectionButton.setBackgroundColor(Color.GRAY);


        sportsSelectionButton.setOnClickListener(v -> sportsButtonClicked());

        travelSelectionButton.setOnClickListener(v -> travelButtonClicked());

        musicSelectionButton.setOnClickListener(v -> musicButtonClicked());

        fishingSelectionButton.setOnClickListener(v -> fishingButtonClicked());


    }

    public void sportsButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (sportsSelectionButton.getAlpha() == 1.0f) {
            sportsSelectionButton.setAlpha(.5f);
            sportsSelectionButton.setBackgroundColor(Color.GRAY);
            userInfo.getProfile().setSports(false);
        } else {
            sportsSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            sportsSelectionButton.setAlpha(1.0f);
            userInfo.getProfile().setSports(true);
        }
    }

    public void travelButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (travelSelectionButton.getAlpha() == 1.0f) {
            travelSelectionButton.setAlpha(.5f);
            travelSelectionButton.setBackgroundColor(Color.GRAY);
            userInfo.getProfile().setTravel(false);
        } else {
            travelSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            travelSelectionButton.setAlpha(1.0f);
            userInfo.getProfile().setTravel(true);

        }

    }

    public void musicButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (musicSelectionButton.getAlpha() == 1.0f) {
            musicSelectionButton.setAlpha(.5f);
            musicSelectionButton.setBackgroundColor(Color.GRAY);
            userInfo.getProfile().setMusic(false);
        } else {
            musicSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            musicSelectionButton.setAlpha(1.0f);
            userInfo.getProfile().setMusic(true);

        }

    }

    public void fishingButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (fishingSelectionButton.getAlpha() == 1.0f) {
            fishingSelectionButton.setAlpha(.5f);
            fishingSelectionButton.setBackgroundColor(Color.GRAY);
            userInfo.getProfile().setFishing(false);
        } else {
            fishingSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            fishingSelectionButton.setAlpha(1.0f);
            userInfo.getProfile().setFishing(true);

        }

    }

    public void init() {
        hobbiesContinueButton.setOnClickListener(v ->


                createUser(userInfo));
    }

    //----------------------------------------Firebase----------------------------------------

    private void createUser(final User userInfo) {

        if (userInfo.getAccount_type().equals("google")){
            storeUserDetails(userInfo);
        }

        mAuth.createUserWithEmailAndPassword(userInfo.getEmail(),userInfo.getUserPassword())
                .addOnSuccessListener(authResult -> storeUserDetails(userInfo)).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterHobby.this, "Failed to Sign In..... Try Again ater"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void storeUserDetails(User userInfo) {
        String userId = mAuth.getCurrentUser().getUid();
        mReference.child(userId).setValue(userInfo).addOnSuccessListener(aVoid -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }).addOnFailureListener(e -> {
            Toast.makeText(RegisterHobby.this, "Failed to create user", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
