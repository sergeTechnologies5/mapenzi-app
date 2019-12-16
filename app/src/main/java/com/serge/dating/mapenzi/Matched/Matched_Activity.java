package com.serge.dating.mapenzi.Matched;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.FriendRequests;
import com.serge.dating.mapenzi.Utils.Friends;
import com.serge.dating.mapenzi.Utils.TopNavigationViewHelper;
import com.serge.dating.mapenzi.Utils.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Matched_Activity extends AppCompatActivity {

    private static final String TAG = "Matched_Activity";
    private static final int ACTIVITY_NUM = 2;

    List<User> matchList = new ArrayList<>();
    List<User> copyList = new ArrayList<>();

    private Context mContext = Matched_Activity.this;

    private String userId, userSex, lookforSex;
    private double latitude = 37.349642;
    private double longtitude = -121.938987;
    private EditText search;
    private List<User> usersList = new ArrayList<>();

    private RecyclerView recyclerView, mRecyclerView;
    private ActiveUserAdapter adapter;
    private MatchUserAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mfDatabaseReference;
    private FirebaseAuth mAuth;
    Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched);

        setupTopNavigationView();
        searchFunc();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mfDatabaseReference = FirebaseDatabase.getInstance().getReference("friends");
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.active_recycler_view);
        mRecyclerView = findViewById(R.id.matche_recycler_view);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        prepareActiveData(aDataSnapshot -> {
            usersList.clear();
            for (DataSnapshot matchedDataSnapshot : aDataSnapshot.getChildren()) {

                Friends friends = matchedDataSnapshot.getValue(Friends.class);
                if (friends.onlineStatus ==0L){
                    User activeUsers = new User();
                    activeUsers.setOnlineStatus(friends.onlineStatus);
                    activeUsers.setUsername(friends.username);
                    activeUsers.setUser_id(friends.receiver);
                    usersList.add(activeUsers);
                }


            }

            adapter = new ActiveUserAdapter(usersList, getApplicationContext());
            recyclerView.setAdapter(adapter);
        });

        //mAdapter = new MatchUserAdapter(matchList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager1);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.setAdapter(mAdapter);

        prepareMatchData(mDataSnapshot -> {
            matchList.clear();
            for (DataSnapshot matchedDataSnapshot : mDataSnapshot.getChildren()) {
                Friends friends = matchedDataSnapshot.getValue(Friends.class);
                String uid = matchedDataSnapshot.getKey();

                User matchedUser = new User();
                matchedUser.setOnlineStatus(friends.onlineStatus);
                matchedUser.setUsername(friends.username);
                matchedUser.setUser_id(friends.receiver);

                matchedUser.setProfileImageUrl(friends.profileImageUrl);
                if (!uid.equals(mAuth.getCurrentUser().getUid()) && matchedUser.getUsername() != null) {
                    matchList.add(matchedUser);
                }
            }


            mAdapter = new MatchUserAdapter(matchList, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);
        });


    }

    private void prepareActiveData(final Matched_Activity.OnDataReceiveCallback callback) {

        mfDatabaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void prepareMatchData(final Matched_Activity.OnDataReceiveCallback callback) {

        mfDatabaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private interface OnDataReceiveCallback {
        void onDataReceived(DataSnapshot mDataSnapshot);
    }

    private void searchFunc() {
        search = findViewById(R.id.searchBar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //searchText();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //searchText();
            }
        });
    }

    private void searchText() {
        String text = search.getText().toString().toLowerCase(Locale.getDefault());
        if (text.length() != 0) {
            if (matchList.size() != 0) {
                // matchList.clear();
                for (User user : copyList) {
                    if (user.getUsername().toLowerCase(Locale.getDefault()).contains(text)) {
                        matchList.add(user);
                    }
                }
            }
        } else {
            matchList.clear();
            matchList.addAll(copyList);
        }

        mAdapter.notifyDataSetChanged();
    }

    private boolean checkDup(User user) {
        if (matchList.size() != 0) {
            for (User u : matchList) {
                if (u.getUsername() == user.getUsername()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkClickedItem(int position) {

        User user = matchList.get(position);
        //calculate distance
        Intent intent = new Intent(this, ProfileCheckinMatched.class);
        intent.putExtra("classUser", user);
        startActivity(intent);
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
