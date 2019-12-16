package com.serge.dating.mapenzi.Login;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.GPS;
import com.serge.dating.mapenzi.Utils.Profile;
import com.serge.dating.mapenzi.Utils.Settings;
import com.serge.dating.mapenzi.Utils.User;


public class RegisterBasicInfo extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    GPS gps;
    private Context mContext;
    private String email, username, password;
    private TextInputEditText mEmail, mPassword, mUsername;

    private AppCompatButton btnRegister;
    private String append = "";

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerbasic_info);
        mContext = RegisterBasicInfo.this;
        Log.d(TAG, "onCreate: started");

        gps = new GPS(getApplicationContext());

        initWidgets();
        init();
    }

    private void init() {
        btnRegister.setOnClickListener(v -> {

            email = mEmail.getText().toString();
            username = mUsername.getText().toString();
            password = mPassword.getText().toString();

            if (checkInputs(email, username, password)) {
                //find geo location
                //find geo location
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Profile profile= new Profile();
                Settings settings = new Settings();

                Intent intent = new Intent(RegisterBasicInfo.this, RegisterGender.class);
                User user = new User("", "", email, username, password,0L , "", latitude, longitude, profile,settings,"manual");
                intent.putExtra("classUser", user);
                startActivity(intent);
            }
        });
    }

    private boolean checkInputs(String email, String username, String password) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (email.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(mContext, "All fields must be filed out.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Below code checks if the email id is valid or not.
        if (!email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Invalid email address, enter valid email id and click on Continue", Toast.LENGTH_SHORT).show();
            return false;

        }


        return true;
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets");
        mEmail = findViewById(R.id.input_email);
        mUsername = findViewById(R.id.input_username);
        btnRegister = findViewById(R.id.btn_register);
        mPassword = findViewById(R.id.input_password_signUp);
        mContext = RegisterBasicInfo.this;

    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));

    }
}
