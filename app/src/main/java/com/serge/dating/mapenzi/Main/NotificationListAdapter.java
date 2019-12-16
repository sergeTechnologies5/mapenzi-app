package com.serge.dating.mapenzi.Main;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.NotificationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationListAdapter extends ArrayAdapter<NotificationModel> {

    private Context con;
    private ImageButton acceptBtn;
    private ImageButton rejectBtn;


    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    public NotificationListAdapter(@NonNull Context context, List<NotificationModel> list) {
        super(context, R.layout.custom_notication_row, list);
        con = context;


        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = mAuth.getCurrentUser();

        mDatabaseReference.child("notifications").child(mFirebaseUser.getUid());

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_notication_row, parent, false);
        NotificationModel model = getItem(position);
        // get layout
        LinearLayout layout = (LinearLayout) customView.findViewById(R.id.layout_CustomNotificationRow);

        // make components according to model and append to layout

        TextView tv_NotficationMessage = (TextView) customView.findViewById(R.id.tv_NotificationMessage);
        tv_NotficationMessage.setText(model.getNotificationMessage());

        // friend request
        if (model.getNotificationType() == 1) {
            // make button and append
//            acceptBtn = new Button(getContext());
//            rejectBtn = new Button(getContext());

            acceptBtn = new ImageButton(getContext());
            rejectBtn = new ImageButton(getContext());

            acceptBtn.setBackgroundColor(Color.TRANSPARENT);
            rejectBtn.setBackgroundColor(Color.TRANSPARENT);

            acceptBtn.setImageResource(R.drawable.ic_like1);
            rejectBtn.setImageResource(R.drawable.ic_dislike);

            setCustomOnClick(acceptBtn, model.getEmailFrom(), model.getFirstName(), model.getLastName());
            onRejectClick(rejectBtn, position, model.getFirstName() + " " + model.getLastName());
            // set layout params
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER;

            acceptBtn.setLayoutParams(layoutParams);
            rejectBtn.setLayoutParams(layoutParams);
            acceptBtn.setPadding(4, 4, 4, 4);
            rejectBtn.setPadding(4, 4, 4, 4);
            layout.addView(acceptBtn);
            layout.addView(rejectBtn);
        }
        return customView;
    }


    private void setCustomOnClick(final ImageButton btn, final String friendEmail, final String friendFirstName, final String friendLastName) {

        btn.setOnClickListener(
                v -> {

                    // set each other friends

                    Map<String, String> map1 = new HashMap<>();
                    map1.put("Email", friendEmail);
                    map1.put("FirstName", friendFirstName);
                    map1.put("LastName", friendLastName);

                    mDatabaseReference.setValue(map1);

                        Map<String, String> map2 = new HashMap<>();
                        map2.put("Email", mFirebaseUser.getEmail());
                        map2.put("FirstName", mFirebaseUser.getDisplayName());
                        map2.put("LastName", mFirebaseUser.getDisplayName());
//                        fireBase.child(friendEmail).child(user.Email).setValue(map2);
//
//                        frRequ.child(user.Email).child(friendEmail).removeValue();
//                        acceptBtn.setEnabled(false);
//
//                        Toast.makeText(con, "Accepted", Toast.LENGTH_SHORT).show();
//                        rejectBtn.setEnabled(false);
//
//                        Map<String, String> notMap = new HashMap<String, String>();
//                        notMap.put("SenderEmail", user.Email);
//                        notMap.put("FirstName", user.FirstName);
//                        notMap.put("LastName", user.LastName);
//                        notMap.put("Message", "Contact request accepted start chating... ");
//                        // accepted contact reques
//                        notMap.put("NotificationType", "3");
//                        Firebase notRef = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);
//                        notRef.push().setValue(notMap);
                }
        );


    }

    private void onRejectClick(final ImageButton btn, final int modelPosition, final String friedFullName) {
        btn.setOnClickListener(v -> new AlertDialog.Builder(con)
                .setTitle(friedFullName)
                .setMessage("Are you sure to reject this contact request?")
                .setPositiveButton("Reject", (dialog, which) -> {
//                        Firebase fireBase = new Firebase(StaticInfo.FriendRequestsEndPoint + "/" + user.Email + "/" + getItem(modelPosition).FriendRequestFireBaseKey);
//                        fireBase.removeValue();
                    rejectBtn.setEnabled(false);
                    acceptBtn.setEnabled(false);
                    Toast.makeText(con, "Rejected", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .show());

    }
}
