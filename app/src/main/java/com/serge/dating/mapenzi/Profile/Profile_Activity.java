package com.serge.dating.mapenzi.Profile;

import android.Manifest;
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
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.serge.dating.mapenzi.Login.RegisterBasicInfo;
import com.serge.dating.mapenzi.Login.RegisterGender;
import com.serge.dating.mapenzi.Login.RegisterHobby;
import com.serge.dating.mapenzi.Main.MainActivity;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.GPS;
import com.serge.dating.mapenzi.Utils.Profile;
import com.serge.dating.mapenzi.Utils.PulsatorLayout;
import com.serge.dating.mapenzi.Utils.TopNavigationViewHelper;
import com.serge.dating.mapenzi.Utils.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {
    private static final String TAG = "Profile_Activity";
    private static final int ACTIVITY_NUM = 0;
    private static final int PICK_IMAGE = 100;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    //firebase
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Context mContext = Profile_Activity.this;
    private CircleImageView profileImage;
    private TextView profileName;
    private int REQUEST_CAMERA = 0, SELECT_FROM_FILE = 1;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    //------Firebase------
    private Uri selectedImageUri;

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: create the page");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("profilePictures");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        PulsatorLayout mPulsator = findViewById(R.id.pulsator);
        mPulsator.start();

        loadProfileDetails();

        setupTopNavigationView();

        profileImage = findViewById(R.id.circle_profile_image);
        profileName = findViewById(R.id.profile_name);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        ImageButton edit_btn = findViewById(R.id.edit_profile);
        edit_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Activity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Activity.this, SettingsActivity.class);
            startActivity(intent);
        });

        profileImage.setOnClickListener(v -> requestMultiplePermissions());
        profileName.setOnClickListener(v -> createProfileNameDialog());
    }

    private void loadProfileDetails() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
               try {
                   if (user.getUsername() == null){
                       GPS gps = new GPS(getApplicationContext());
                       double latitude = gps.getLatitude();
                       double longitude = gps.getLongitude();

                       Profile profile= new Profile();
                       com.serge.dating.mapenzi.Utils.Settings settings = new com.serge.dating.mapenzi.Utils.Settings();

                       Toast.makeText(getApplicationContext(),"Please Update Profile", Toast.LENGTH_LONG).show();
                       Intent intent = new Intent(Profile_Activity.this, RegisterGender.class);
                       User userNew = new User("", "", mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(),0L , "", latitude, longitude, profile,settings,"google");
                       intent.putExtra("classUser", userNew);
                       startActivity(intent);

                   }else {
                       profileName.setText(user.getUsername());
                       if (user.getProfileImageUrl() != null){
                           switch (user.getProfileImageUrl()) {
                               case "defaultFemale":
                                   Glide.with(mContext).load(R.drawable.default_woman).into(profileImage);
                                   break;
                               case "defaultMale":
                                   Glide.with(mContext).load(R.drawable.default_man).into(profileImage);
                                   break;
                               default:
                                   Glide.with(mContext).load(user.getProfileImageUrl()).into(profileImage);
                                   break;
                           }
                       }

                   }
               }catch (NullPointerException ex){

               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createProfileNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
        builder.setTitle("Profile Name");

        //setting input text
        final EditText profileNameInput = new EditText(Profile_Activity.this);
        profileNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(profileNameInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newProfileName = profileNameInput.getText().toString().trim();
            profileName.setText(newProfileName);

            updateProfileName(newProfileName);

        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateProfileName(String newProfileName) {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabaseReference.child(userId).child("username")
                .setValue(newProfileName).addOnSuccessListener(aVoid -> Toast.makeText(Profile_Activity.this, "Username Updated", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile_Activity.this, "Failed to Update Username", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: resume to the page");

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

    private void requestMultiplePermissions() {
        if (ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Profile_Activity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Profile_Activity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Profile_Activity.this, permissionsRequired[2])) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Profile_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(Profile_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            // txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {
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
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FROM_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FROM_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if(data!=null){
            selectedImageUri =data.getData();
            Picasso.get()
                    .load(selectedImageUri)
                    .into(profileImage);
            
            updateProfileImage(selectedImageUri);
        }
    }

    private void updateProfileImage(Uri selectedImageUri) {
        final String userId = mAuth.getCurrentUser().getUid();

        final StorageReference pictureRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
        pictureRef
                .putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //String pictureName = pictureRef.getPath();
                                String imageUrl = uri.toString();
                                mDatabaseReference.child(userId).child("profileImageUrl")
                                        .setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Profile_Activity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Profile_Activity.this, "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });

                    }
                });
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

       try {
           updateProfileImage(data.getData());
       }catch (NullPointerException ex){

       }

        profileImage.setImageBitmap(thumbnail);
    }



}
