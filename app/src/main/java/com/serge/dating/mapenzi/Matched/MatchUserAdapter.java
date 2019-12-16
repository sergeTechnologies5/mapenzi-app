package com.serge.dating.mapenzi.Matched;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.serge.dating.mapenzi.Main.NotificationHelper;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.Message;
import com.serge.dating.mapenzi.Utils.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class MatchUserAdapter extends RecyclerView.Adapter<MatchUserAdapter.MyViewHolder> {
    List<User> usersList;
    Context context;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private NotificationHelper mNotificationHelper;


    public MatchUserAdapter(List<User> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
        mNotificationHelper = new NotificationHelper(context);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference  = FirebaseDatabase.getInstance().getReference();
    }
    @NonNull
    @Override
    public MatchUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.matched_user_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchUserAdapter.MyViewHolder holder, int position) {
        User users = usersList.get(position);
        holder.name.setText(users.getUsername());
        //holder.profession.setText(users.getPr().toString());
        if (users.getProfileImageUrl() != null) {
            Picasso.get().load(users.getProfileImageUrl()).into(holder.imageView);

            holder.user = users;
            holder.userName= users.getUsername();
            holder.userImage = users.getProfileImageUrl();
            holder.userId = users.getUser_id();
            holder.onlineStatus = users.getOnlineStatus().toString();
            loadMessages(users.getUser_id(), holder);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView imageView;
        TextView name, profession;

        TextView status;

        String userName = "";
        String userImage = "";
        String userId = "";
        String onlineStatus = "";

        User user ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.mui_image);
            name = itemView.findViewById(R.id.mui_name);
            profession = itemView.findViewById(R.id.mui_profession);
            status = itemView.findViewById(R.id.mui_status);
        }

        @Override
        public void onClick(View v) {
            /*Toast.makeText(context, name.getText().toString(), Toast.LENGTH_SHORT).show();*/
            Intent startMessaging = new Intent(v.getContext(), MessagingActivity.class);
            startMessaging.putExtra("userName", userName);
            startMessaging.putExtra("userImage", userImage);
            startMessaging.putExtra("classUser", user);
            startMessaging.putExtra("userId", userId);
            startMessaging.putExtra("onlineStatus", onlineStatus);
            v.getContext().startActivity(startMessaging);
        }
    }

    private void loadMessages(final String receiverUid,@NonNull MatchUserAdapter.MyViewHolder holder) {
        String currentUid = mAuth.getCurrentUser().getUid();

        Query query = mDatabaseReference
                .child("messages")
                .child(currentUid)
                .child(receiverUid).limitToLast(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot matchedDataSnapshot : dataSnapshot.getChildren()) {
                    Message message = matchedDataSnapshot.getValue(Message.class);

                    if (message.getType().equals("text")){
                        holder.profession.setBackgroundResource(R.drawable.circle_background);
                        holder.profession.setText(message.getMessage());
                    }else{
                        holder.profession.setText("");
                        holder.profession.setBackgroundResource(R.drawable.ic_photo_camera);
                    }

                    if (message.getIsSeen().equals("true")){

                            holder.status.setBackgroundResource(R.drawable.circle_background);
                        }else {
                            holder.status.setBackgroundResource(R.drawable.circle_background_red);
                           // sendNotification(context);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendNotification(Context mContext) {
        NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(mContext.getString(R.string.app_name), mContext.getString(R.string.match_notification));
        mNotificationHelper.getManager().notify(1, nb.build());
    }
}
