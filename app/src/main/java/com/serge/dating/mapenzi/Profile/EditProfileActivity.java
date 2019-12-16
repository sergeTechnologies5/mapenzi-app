package com.serge.dating.mapenzi.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.Images;
import com.serge.dating.mapenzi.Utils.Profile;
import com.serge.dating.mapenzi.Utils.User;

import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    private List<Images> imagesList = new ArrayList<>();

    private static final int REQUEST_PERMISSION_SETTING = 101;

    ImageButton back;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView;


    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    EditText aboutMe, jobTitle, currentCompany, currentSchool;
    private SwitchCompat genderMale, genderFemale,sportsCheckBox,
            travelCheckBox, musicCheckBox, fishingCheckBox, showAge, showDistance;
    Boolean genderMan = false;

    private Profile mProfile = new Profile();
    private Profile profile;


    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private List<ImageView> imageViews = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("profilePictures");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");


        imageView1 = findViewById(R.id.image_view_1);
        imageView2 = findViewById(R.id.image_view_2);
        imageView3 = findViewById(R.id.image_view_3);
        imageView4 = findViewById(R.id.image_view_4);
        imageView5 = findViewById(R.id.image_view_5);
        imageView6 = findViewById(R.id.image_view_6);

        genderMale = findViewById(R.id.man_button);
        genderFemale = findViewById(R.id.woman_button);

        aboutMe = findViewById(R.id.et_add_jabout_you);
        jobTitle = findViewById(R.id.et_add_job_title);
        currentCompany= findViewById(R.id.et_add_company);
        currentSchool= findViewById(R.id.et_add_school);


        fishingCheckBox= findViewById(R.id.hobbies_fishing_button);
        sportsCheckBox= findViewById(R.id.hobbie_sport__button);
        musicCheckBox= findViewById(R.id.hobbies_music__button);
        travelCheckBox= findViewById(R.id.hobbie_travelling_button);
        showAge = findViewById(R.id.switch_dont_show_age);
        showDistance = findViewById(R.id.switch_dont_show_dist);


        back = findViewById(R.id.back);


        loadUserProfile();
        back.setOnClickListener(v -> onBackPressed());

        genderFemale.setOnCheckedChangeListener((buttonView, isChecked) ->{


            if (isChecked) {
                genderFemale.setChecked(true);
                genderMale.setChecked(false);
                genderMan = false;
            }
        } );


        genderMale.setOnCheckedChangeListener((buttonView, isChecked) ->{


            if (isChecked) {
                genderFemale.setChecked(false);
                genderMale.setChecked(true);
                genderMan = true;
            }
        } );

        imageViews.add(imageView1);imageViews.add(imageView2);imageViews.add(imageView3);
        imageViews.add(imageView4);imageViews.add(imageView5);imageViews.add(imageView6);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = imageView1;

                proceedAfterPermission();

            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = imageView2;
                proceedAfterPermission();

            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = imageView3;
                proceedAfterPermission();

            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = imageView4;

                proceedAfterPermission();

            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = imageView5;

                proceedAfterPermission();

            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = imageView6;

                proceedAfterPermission();

            }
        });



    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                profile = user.getProfile();

                mProfile = profile;
                imagesList = mProfile.getImages();


                try {
                    for (int i=0;i<imageViews.size();i++){

                        try {
                            Picasso.get()
                                    .load(imagesList.get(i).getImageUrl())
                                    .into(imageViews.get(i));
                        }catch (ArrayIndexOutOfBoundsException ex){

                        }
                    }
                }catch (Exception ex){

                }

                setProfileDisplay(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setProfileDisplay(Profile profile) {

        aboutMe.setText(profile.getAboutMe());
        jobTitle.setText(profile.getJobTitle());
        currentCompany.setText(profile.getCompanyName());
        currentSchool.setText(profile.getSchoolName());

        if(profile.getSex().equals("male")){
            genderMale.setChecked(true);
            genderFemale.setChecked(false);
            genderMan = true;
        }else{
            genderFemale.setChecked(true);
            genderMale.setChecked(false);
        }


        if(profile.isSports()) sportsCheckBox.setChecked(true);
        if(profile.isTravel()) travelCheckBox.setChecked(true);
        if(profile.isMusic()) musicCheckBox.setChecked(true);
        if(profile.isFishing()) fishingCheckBox.setChecked(true);
        if(profile.isShowAge()) showAge.setChecked(true);
        if(profile.isShowDistance()) showDistance.setChecked(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        updateProfile();
    }

    private void updateProfile() {
        String aboutYou = aboutMe.getText().toString().trim();
        String jobTittle = jobTitle.getText().toString().trim();
        String company = currentCompany.getText().toString().trim();
        String school = currentSchool.getText().toString().trim();

        String gender = genderMan ? "male" : "female";
        Boolean sports = sportsCheckBox.isChecked();
        Boolean travelling = travelCheckBox.isChecked();
        Boolean music = musicCheckBox.isChecked();
        Boolean fishing = fishingCheckBox.isChecked();

        Boolean displayAge = showAge.isChecked();
        Boolean displayDistance = showDistance.isChecked();


        /*Profile profile = new Profile(
                aboutYou,jobTittle,company,school,gender,
                displayAge,displayDistance,sports,travelling,music,fishing,
                aboutYou,mProfile.getPreferSex(),mProfile.getDateOfBirth()
        );*/

         profile = new Profile(aboutYou,jobTittle,company,school,gender,displayAge,displayDistance,sports,
                travelling,music,fishing,aboutYou,gender,mProfile.getPreferSex(),mProfile.getDateOfBirth()
        );

         profile.setImages(imagesList);
        String userId = mAuth.getCurrentUser().getUid();
        mDatabaseReference.child(userId).
                child("profile").setValue(profile);


    }



    private void proceedAfterPermission() {

        //requestMultiplePermissions();
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    cameraIntent();

                } else if (options[item].equals("Choose from Gallery"))

                {

                    galleryIntent();


                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = data.getData();
       try {
           addProfileImages(uri);
       }catch (NullPointerException ex){

       }
        imageView.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = data.getData();
            addProfileImages(uri);
        }

        imageView.setImageBitmap(bm);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


    private void addProfileImages(Uri selectedImageUri) {
        final String userId = mAuth.getCurrentUser().getUid();


        final StorageReference pictureRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
        pictureRef
                .putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //String pictureName = pictureRef.getPath();
                        String imageUrl = uri.toString();
                        if (imagesList == null){
                            imagesList = new ArrayList<>();
                        }
                        imagesList.add(new Images(imageUrl));
                        profile.setImages(imagesList);
                        mDatabaseReference.child(userId).child("profile")
                                .setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfileActivity.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }));
    }
}
