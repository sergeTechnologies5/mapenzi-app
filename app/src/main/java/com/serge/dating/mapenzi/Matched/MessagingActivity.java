package com.serge.dating.mapenzi.Matched;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.serge.dating.mapenzi.R;
import com.serge.dating.mapenzi.Utils.Message;
import com.serge.dating.mapenzi.Utils.TimeAgo;
import com.serge.dating.mapenzi.Utils.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_GALLERY = 100;
    private ImageView imageBack ;
    private CircleImageView profileUserImage ;
    private AppCompatTextView profileUserName ;
    private AppCompatTextView profileOnlineStatus;
    private AppCompatImageView sendImage;

    private TextInputEditText etMessageInput;
    private FloatingActionButton fabSendMessage;
    private RecyclerView rvMessages;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;


    private String receiverUid;
    private String currentUserId;

    private final int MESSAGE_TYPE_SENT = 0;
    private final int MESSAGE_TYPE_RECEIVED = 1;

    private final int MESSAGE_TYPE_RECEIVED_IMAGE= 10;
    private final int MESSAGE_TYPE_SENT_IMAGE = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        imageBack = findViewById(R.id.action_messages_back);
        profileUserImage = findViewById(R.id.img_profile_pic_messages);
        profileUserName = findViewById(R.id.tv_profile_name_messages);
        profileOnlineStatus = findViewById(R.id.tv_online_messages);

        sendImage = findViewById(R.id.btn_image);
        etMessageInput= findViewById(R.id.et_message_input);
        fabSendMessage= findViewById(R.id.fab_send_message);
        rvMessages = findViewById(R.id.rv_messages);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference  = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();


        rvMessages.setLayoutManager(new LinearLayoutManager(MessagingActivity.this));
        rvMessages.setHasFixedSize(false);

        currentUserId =mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        setUserProfileToolbar(intent);


        loadMessages(receiverUid);

        imageBack.setOnClickListener(v -> {
            startActivity(new Intent(MessagingActivity.this, Matched_Activity.class));
            finish();
        });

        sendImage.setOnClickListener(v -> galleryIntent());

        fabSendMessage.setOnClickListener(v -> sendMessage(receiverUid));

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), SELECT_IMAGE_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_GALLERY && resultCode==RESULT_OK){
            Uri imageUri = data.getData();

            sendImageMessage(imageUri);
        }
    }

    private void sendImageMessage(Uri imageUri) {
        final String currentUserReference = "messages/" + currentUserId + "/" + receiverUid;
        final String receiverReference = "messages/" + receiverUid + "/" + currentUserId;


        final DatabaseReference messagePush = mDatabaseReference
                .child("messages")
                .child(currentUserId)
                .child(receiverUid)
                .push();

        DatabaseReference nDatabaseReference = mDatabaseReference
                .child("notifications")
                .child(receiverUid);

        Map<String, String> map = new HashMap<>();
        map.put("Message", "Image");
        map.put("SenderEmail",mFirebaseUser.getEmail() );
        map.put("FirstName", mFirebaseUser.getDisplayName());
        map.put("LastName", mFirebaseUser.getDisplayName());
        map.put("seen", "false");
        map.put("FriendRequestFireBaseKey", nDatabaseReference.getKey());
        map.put("NotificationType", String.valueOf(1));
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        String sentDate = dateFormat.format(date);
        map.put("SentDate", sentDate);
        nDatabaseReference.setValue(map);
        //end of notifications

        final String pushKey = messagePush.getKey();

        final StorageReference picturePath = mStorageReference
                .child("messageImages").child(pushKey + ".jpg");



        picturePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> picturePath.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            String currentUserID = mAuth.getCurrentUser().getUid();
            Map messageMap = new HashMap();
            messageMap.put("message",downloadUrl);
            messageMap.put("isSeen","false");
            messageMap.put("key",pushKey);
            messageMap.put("type","image");
            messageMap.put("from", currentUserID);
            messageMap.put("timeStamp", ServerValue.TIMESTAMP);

            Map messageUsersMap = new HashMap();
            messageUsersMap.put(currentUserReference + "/" + pushKey, messageMap);
            messageUsersMap.put(receiverReference  +"/" + pushKey,messageMap);

            mDatabaseReference.updateChildren(messageUsersMap, (databaseError, databaseReference) -> {
                if(databaseError !=null){
                    Log.d("@@Message Send Err@@@", databaseError.getMessage() );
                }
            });
        }).addOnFailureListener(e -> Toast.makeText(MessagingActivity.this, "Failure", Toast.LENGTH_SHORT).show()));


    }

    private void setUserProfileToolbar(Intent intent) {

        String userName  = intent.getStringExtra("userName");
        String userImage = intent.getStringExtra("userImage");
        String onlineStatus = intent.getStringExtra("onlineStatus");

        long lastSeenTime = Long.parseLong(onlineStatus);

        receiverUid = intent.getStringExtra("userId");


        final User user =  (User) intent.getSerializableExtra("classUser");
        profileUserName.setText(userName);
        profileOnlineStatus.setText(onlineStatus);
        Picasso.get()
                .load(userImage)
                .into(profileUserImage);


        profileUserImage.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ProfileCheckinMatched.class);

            intent1.putExtra("classUser",user);
        });
        if(lastSeenTime == 0L){
            profileOnlineStatus.setText("online");
        }else{

            String lastSeen = TimeAgo.getTimeAgo(lastSeenTime,getApplicationContext());
            profileOnlineStatus.setText(lastSeen);
        }
    }

    private void sendMessage(String receiverUid) {

        String message = etMessageInput.getText().toString();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(MessagingActivity.this, "Cant Send Empty Message",Toast.LENGTH_SHORT).show();
            return;
        }

        etMessageInput.getText().clear();
        currentUserId = mAuth.getCurrentUser().getUid();

        String currentUserReference = "messages/" + currentUserId + "/" + receiverUid;
        String receiverReference = "messages/" + receiverUid + "/" + currentUserId;


        DatabaseReference messagePush = mDatabaseReference
                .child("messages")
                .child(currentUserId)
                .child(receiverUid)
                .push();

        String messagePushKey = messagePush.getKey();

        DatabaseReference nDatabaseReference = mDatabaseReference
                .child("notifications")
                .child(receiverUid);

        Map<String, String> map = new HashMap<>();
        map.put("Message", message);
        map.put("SenderEmail",mFirebaseUser.getEmail() );
        map.put("FirstName", mFirebaseUser.getDisplayName());
        map.put("LastName", mFirebaseUser.getDisplayName());
        map.put("seen", "false");
        map.put("FriendRequestFireBaseKey", nDatabaseReference.getKey());
        map.put("NotificationType", String.valueOf(1));

        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        String sentDate = dateFormat.format(date);
        map.put("SentDate", sentDate);
        nDatabaseReference.setValue(map);
        //end of notifications

        Map messageMap = new HashMap();
        messageMap.put("message",message);
        messageMap.put("isSeen","false");
        messageMap.put("key",messagePushKey);
        messageMap.put("type","text");
        messageMap.put("from", currentUserId);
        messageMap.put("timeStamp", ServerValue.TIMESTAMP);

        Map messageUsersMap = new HashMap();
        messageUsersMap.put(currentUserReference + "/" + messagePushKey, messageMap);
        messageUsersMap.put(receiverReference  +"/" + messagePushKey,messageMap);

        mDatabaseReference.updateChildren(messageUsersMap, (databaseError, databaseReference) -> {
            if(databaseError !=null){
                Log.d("@@Message Send Err@@@", databaseError.getMessage() );
            }
        });

    }

    private void loadMessages(final String receiverUid) {
        String currentUid = mAuth.getCurrentUser().getUid();


        Query query = mDatabaseReference
                .child("messages")
                .child(currentUid)
                .child(receiverUid);


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();


        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                if (viewType == MESSAGE_TYPE_RECEIVED) {
                    return new MessageViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_message_received,
                                    parent, false
                            )
                    );
                } else if(viewType == MESSAGE_TYPE_SENT){
                    return new MessageViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_message_sent,
                                    parent, false
                            )
                    );
                }else if(viewType == MESSAGE_TYPE_RECEIVED_IMAGE) {
                    return new MessageViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_message_received_image,
                                    parent, false
                            )
                    );
                }else{
                    return new MessageViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_message_sent_image,
                                    parent, false
                            )
                    );
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                Long timeStampLong = message.getTimeStamp();

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.UK);

                String dateString = sdf.format(timeStampLong);

                if(message.getType().equals("text")){
                    messageViewHolder.messageReceivedSent.setText(message.getMessage());
                    messageViewHolder.messageSentTime.setText(dateString);
                }else{
                    Picasso.get()
                            .load(message.getMessage())
                            .into(messageViewHolder.mesageImage);

                    messageViewHolder.messageSentTime.setText(dateString);
                }


                message.setIsSeen("true");
                query.getRef().child(message.getKey()).setValue(message);
            }

            @Override
            public int getItemViewType(int position) {


                Message message = getItem(position);
                if (message.getFrom().equals(receiverUid)) {
                    if(message.getType().equals("text")){
                        return MESSAGE_TYPE_RECEIVED;
                    }else{
                        return MESSAGE_TYPE_RECEIVED_IMAGE;
                    }

                } else {
                    if(message.getType().equals("text")){
                        return MESSAGE_TYPE_SENT;
                    }else{
                        return MESSAGE_TYPE_SENT_IMAGE;
                    }
                }

            }


        };

        rvMessages.setAdapter(adapter);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView messageReceivedSent;
        private AppCompatTextView messageSentTime;
        private AppCompatImageView mesageImage;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageReceivedSent =itemView.findViewById(R.id.tv_message);
            messageSentTime = itemView.findViewById(R.id.sentTime);
            mesageImage = itemView.findViewById(R.id.img_image_message);


        }

    }



}
