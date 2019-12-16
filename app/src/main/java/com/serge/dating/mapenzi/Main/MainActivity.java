package com.serge.dating.mapenzi.Main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.serge.dating.mapenzi.Matched.Matched_Activity;
import com.serge.dating.mapenzi.Matched.ProfileCheckinMatched;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.FriendRequests;
import com.serge.dating.mapenzi.Utils.Friends;
import com.serge.dating.mapenzi.Utils.GPS;
import com.serge.dating.mapenzi.Utils.PulsatorLayout;
import com.serge.dating.mapenzi.Utils.TopNavigationViewHelper;
import com.serge.dating.mapenzi.Utils.User;
import com.serge.dating.mapenzi.introduction.IntroductionMain;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 1;
    final private int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    List<User> rowItems = new ArrayList<>();
    List<String> userIds = new ArrayList<>();
    FrameLayout cardFrame, moreFrame;
    private Context mContext = MainActivity.this;
    private NotificationHelper mNotificationHelper;
    private PhotoAdapter arrayAdapter;
    private DatabaseReference mDatabaseReference;
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mFirebaseUser;

    private FirebaseAuth mAuth;

    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = mAuth.getCurrentUser();

        cardFrame = findViewById(R.id.card_frame);
        moreFrame = findViewById(R.id.more_frame);



        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, IntroductionMain.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        // start pulsator
        PulsatorLayout mPulsator = findViewById(R.id.pulsator);
        mPulsator.start();

        profileImage = findViewById(R.id.post);

        mNotificationHelper = new NotificationHelper(this);


        setupTopNavigationView();

        //load friend and friendrequests for current user
        rowItems.clear();
        prepareMatchData(mDataSnapshot -> {
            rowItems.clear();

            for (DataSnapshot matchedDataSnapshot : mDataSnapshot.getChildren()) {
                User matchedUser = matchedDataSnapshot.getValue(User.class);
                String uid = matchedDataSnapshot.getKey();
                String currentUserId = mAuth.getCurrentUser().getUid();


                if (!uid.equals(currentUserId) && matchedUser.getEmail() != null) {

                    matchedUser.setUser_id(uid);
                    rowItems.add(matchedUser);

                    DatabaseReference friendrequests = mDatabaseReference
                            .child("friendrequests")
                            .child(currentUserId)
                            .child(matchedUser.getUser_id());
                    friendrequests.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                FriendRequests friendRequests = d.getValue(FriendRequests.class);
                                if (friendRequests.status.equals("like") || friendRequests.status.equals("dislike") && !friendRequests.receiver.equals(mFirebaseUser.getEmail())) {
                                    rowItems.remove(matchedUser);
                                }
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    if (matchedUser.getProfileImageUrl() != null || rowItems.size() == 0) {

                        switch (matchedUser.getProfileImageUrl()) {
                            case "defaultFemale":
                                Glide.with(mContext).load(R.drawable.default_woman).into(profileImage);
                                break;
                            case "defaultMale":
                                Glide.with(mContext).load(R.drawable.default_man).into(profileImage);
                                break;
                            default:
                                Glide.with(mContext).load(matchedUser.getProfileImageUrl()).into(profileImage);
                                break;
                        }
                        Picasso.get()
                                .load(matchedUser.getProfileImageUrl())
                                .into(profileImage);
                    }

                }
            }

            arrayAdapter = new PhotoAdapter(MainActivity.this, R.layout.item, rowItems);
            updateSwipeCard(arrayAdapter);
            checkRowItem();
            arrayAdapter.notifyDataSetChanged();

        });

    }


    private void prepareMatchData(final MainActivity.OnDataReceiveCallback callback) {

        mDatabaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkRowItem() {
        if (rowItems.isEmpty()) {
            moreFrame.setVisibility(View.VISIBLE);
            cardFrame.setVisibility(View.GONE);
        }
    }

    private void updateLocation() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        updateLocation();
                    } else {
                        Toast.makeText(MainActivity.this, "Location Permission Denied. You have to give permission inorder to know the user range ", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateSwipeCard(final PhotoAdapter arrayAdapter) {
        final SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                User user = (User) dataObject;
                checkRowItem();
                String currentUserId = mFirebaseUser.getUid();

                DatabaseReference friends = mDatabaseReference
                        .child("friendrequests")
                        .child(currentUserId)
                        .push();

                Map<String, String> map = new HashMap<>();
                map.put("receiver", user.getEmail());
                map.put("status", "dislike");
                map.put("key", friends.getKey());
                friends.push().setValue(map);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                User user = (User) dataObject;

                checkRowItem();
                String currentUserId = mFirebaseUser.getUid();
                Query query = mDatabaseReference
                        .child("friendrequests")
                        .child(user.getUser_id())
                        .child(currentUserId);

                DatabaseReference friends = mDatabaseReference
                        .child("friends")
                        .child(currentUserId).push();

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot matchedDataSnapshot : dataSnapshot.getChildren()) {
                            FriendRequests friendRequests = matchedDataSnapshot.getValue(FriendRequests.class);
                            try {
                                if (friendRequests.status.equals("like")) {

                                    Friends map = new Friends(currentUserId, user.getUsername(), user.getOnlineStatus(), user.getProfileImageUrl(), user.getUser_id());
                                    friends.setValue(map);
                                    break;
                                }
                            } catch (NullPointerException ex) {

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DatabaseReference friendrequests = mDatabaseReference
                        .child("friendrequests")
                        .child(currentUserId)
                        .child(user.getUser_id())
                        .push();
                Map<String, String> friendRequests = new HashMap<>();
                friendRequests.put("receiver", user.getUser_id());
                friendRequests.put("sender", currentUserId);
                friendRequests.put("status", "like");
                friendRequests.put("key", friendrequests.getKey());
                friendrequests.setValue(friendRequests);
                //check matches
                checkRowItem();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        /* Optionally add an OnItemClickListener */
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {

            GPS gps = new GPS(getApplicationContext());
            User user = (User) dataObject;
            Intent intent = new Intent(mContext, ProfileCheckinMatched.class);

            intent.putExtra("classUser", user);
            intent.putExtra("distance", gps.calculateDistance(user.getLatitude(), user.getLongitude(), gps.getLatitude(), gps.getLongitude()));
            startActivity(intent);
        });
    }

    public void sendNotification() {
        NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(mContext.getString(R.string.app_name), mContext.getString(R.string.match_notification));
        mNotificationHelper.getManager().notify(1, nb.build());
    }

    public void DislikeBtn(View v) {
        if (rowItems.size() != 0) {
            User card_item = rowItems.get(0);
            rowItems.remove(0);
            checkRowItem();
            arrayAdapter.notifyDataSetChanged();

            //check dislike status
            String currentUserId = mFirebaseUser.getUid();

            DatabaseReference friends = mDatabaseReference
                    .child("friendrequests")
                    .child(currentUserId)
                    .push();

            Map<String, String> map = new HashMap<>();
            map.put("receiver", card_item.getEmail());
            map.put("status", "dislike");
            map.put("key", friends.getKey());
            friends.push().setValue(map);
            Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileImageUrl());
            btnClick.putExtra("classUser", card_item);
            startActivity(btnClick);
        }
    }

    public void checkInfo(View view) {
        Intent intent3 = new Intent(getApplicationContext(), Matched_Activity.class);
        view.getContext().startActivity(intent3);

    }

    public void LikeBtn(View v) {
        if (rowItems.size() != 0) {

            //check matches
            User user = rowItems.get(0);

            //rowItems.remove(0);
            checkRowItem();
            arrayAdapter.notifyDataSetChanged();

            //check like status
            String currentUserId = mFirebaseUser.getUid();

            Query query = mDatabaseReference
                    .child("friendrequests")
                    .child(user.getUser_id())
                    .child(currentUserId);

            DatabaseReference friends = mDatabaseReference
                    .child("friends")
                    .child(currentUserId).push();


            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot matchedDataSnapshot : dataSnapshot.getChildren()) {
                        FriendRequests friendRequests = matchedDataSnapshot.getValue(FriendRequests.class);
                        if (friendRequests.status.equals("like")) {
                            Friends map = new Friends(currentUserId, user.getUsername(), user.getOnlineStatus(), user.getProfileImageUrl(), user.getUser_id());
                            friends.setValue(map);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DatabaseReference friendrequests = mDatabaseReference
                    .child("friendrequests")
                    .child(currentUserId)
                    .child(user.getUser_id())
                    .push();
            Map<String, String> friendRequests = new HashMap<>();
            friendRequests.put("receiver", user.getUser_id());
            friendRequests.put("sender", currentUserId);
            friendRequests.put("status", "like");
            friendRequests.put("key", friendrequests.getKey());
            friendrequests.setValue(friendRequests);
            checkRowItem();
            arrayAdapter.notifyDataSetChanged();

            Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
            btnClick.putExtra("classUser", user);
            btnClick.putExtra("url", user.getProfileImageUrl());
            startActivity(btnClick);
        }
    }

    /**
     * setup top tool bar
     */
    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationViewEx tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            String currentUserId = mAuth.getCurrentUser().getUid();

            if (!currentUserId.isEmpty()) {
                mDatabaseReference.child("users")
                        .child(currentUserId)
                        .child("onlineStatus")
                        .setValue(0L);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuth.getCurrentUser() != null) {
            String currentUserId = mAuth.getCurrentUser().getUid();
            if (!currentUserId.isEmpty()) {
                mDatabaseReference.child("users")
                        .child(currentUserId)
                        .child("onlineStatus")
                        .setValue(ServerValue.TIMESTAMP);
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private interface OnDataReceiveCallback {
        void onDataReceived(DataSnapshot mDataSnapshot);

    }
}

