package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts_manager.Adapters.ChatsAdapter;
import com.example.contacts_manager.Models.ChatsModel;
import com.example.contacts_manager.Models.MessageNotificationModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.CallbackMethod;
import com.example.contacts_manager.Utils.MyHelperClass;
import com.example.contacts_manager.Utils.MyService;
import com.example.contacts_manager.Utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements CallbackMethod {

    ImageView back, picture, call, btn_send;
    EditText message;
    SessionManager sessionManager;
    MyHelperClass myHelperClass;
    String senderId = "";
    String receiverId = "";
    TextView tv_chatHeading;
    DatabaseReference databaseReference;
    List<ChatsModel> chatsModelList;
    ChatsAdapter chatsAdapter;
    RecyclerView chatsRecycler;
    SwipeRefreshLayout swipeRefreshLayout;
    String received = "";
    DatabaseReference chatReferenceForSender;
    DatabaseReference chatReferenceForReceiver;
    DatabaseReference calling;
    DatabaseReference notification;
    String toCall = "";
    String fcm;
    Boolean active = false;
    String text = "";


    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        active = true;
        new MyService(ChatActivity.this);
        tv_chatHeading = findViewById(R.id.tv_chatHeading);
        back = findViewById(R.id.iv_chatBack);
        picture = findViewById(R.id.iv_chatImage);
        call = findViewById(R.id.iv_chatCall);
        btn_send = findViewById(R.id.iv_chatSend);
        message = findViewById(R.id.et_chatText);
        sessionManager = new SessionManager(this);
        myHelperClass = new MyHelperClass();
        chatsRecycler = findViewById(R.id.rv_chats);
        swipeRefreshLayout = findViewById(R.id.chatsSwipeRefresh);


        senderId = sessionManager.getuserId();
        receiverId = getIntent().getStringExtra("userId");
        received = getIntent().getStringExtra("receiverId");
        tv_chatHeading.setText(getIntent().getStringExtra("name"));
        //time = myHelperClass.getCurrentDateHHMMSS_12();


/*        new CountDownTimer(1000, 1000)
        {
            public void onTick(long l) {}
            public void onFinish()
            {
                start();
                if (receiverId != null && !receiverId.isEmpty()) {
                readMessage(receiverId, senderId); }
                else {
                    readMessage(received, senderId);
                }
            }
        }.start();*/





        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (receiverId != null) {
                readMessage(receiverId, senderId); }
                else {
                    readMessage(received, senderId);
                }
                //fetchTimelineAsync(0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, v);

                if (receiverId != null && !receiverId.isEmpty()) {
                    calling = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverId);
                    calling.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                toCall = snapshot.child("Phone").getValue(String.class);
                                popupMenu.getMenu().add(toCall);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" + item.getTitle().toString()));
                                        startActivity(callIntent);
                                        return true;
                                    }
                                });
                                popupMenu.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    calling = FirebaseDatabase.getInstance().getReference().child("Users").child(received);
                    calling.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                toCall = snapshot.child("Phone").getValue(String.class);
                                popupMenu.getMenu().add(toCall);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" + item.getTitle().toString()));
                                        startActivity(callIntent);
                                        return true;
                                    }
                                });
                                popupMenu.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(ChatActivity.this, ConversationsActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().isEmpty()) {
                    text = message.getText().toString();
                    insertDataToFirebase();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
/*                    if (receiverId != null && !receiverId.isEmpty()) {

                        notification = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
                        notification.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    *//*sendNotification(snapshot.child("Name").getValue(String.class), message.getText().toString(),
                                            myHelperClass.getCurrentDateHHMMSS_24(), senderId, snapshot.child("FCMtoken").getValue(String.class));*//*
                                    readMessage(receiverId, senderId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull  DatabaseError error) {

                            }
                        });
                    }
                    else {

                        notification = FirebaseDatabase.getInstance().getReference("Users").child(received);
                        notification.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    *//*sendNotification(sessionManager.getName(), message.getText().toString(),
                                            myHelperClass.getCurrentDateHHMMSS_24(), senderId, snapshot.child("FCMtoken").getValue(String.class));*//*
                                    readMessage(received, senderId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull  DatabaseError error) {

                            }
                        });
                    }*/

                }
            }
                /*if (message.getText().toString().isEmpty()) {
                    return;
                }
                databaseReference = FirebaseDatabase.getInstance().getReference("Conversations");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if ( (senderId.equals(dataSnapshot.child("senderID").getValue(String.class)) || receiverId.equals(dataSnapshot.child("receiverID").getValue(String.class))) &&
                                        (receiverId.equals(dataSnapshot.child("senderID").getValue(String.class)) || senderId.equals(dataSnapshot.child("receiverID").getValue(String.class))) ) {

                                    databaseReference.child(dataSnapshot.getKey()).child("senderID").setValue(senderId);
                                    databaseReference.child(dataSnapshot.getKey()).child("receiverID").setValue(receiverId);
                                    databaseReference.child(dataSnapshot.getKey()).child("message").setValue(message.getText().toString());
                                    databaseReference.child(dataSnapshot.getKey()).child("time").setValue("time");

                                }

                            }
                        }
                        else {
                            Map<String, Object> conversation = new HashMap<>();
                            String key = databaseReference.push().getKey();
                            conversation.put("senderID", senderId);
                            conversation.put("receiverID", receiverId);
                            conversation.put("message", message.getText().toString());
                            conversation.put("time", myHelperClass.getCurrentDateHHMMSS_12());

                            databaseReference.child(key).setValue(conversation);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });*/


        });

        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btn_send.performClick();
                }
                return false;
            }
        });

    }

    private void insertDataToFirebase() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = new HashMap<>();
                String key = reference.push().getKey();
                reference.child(key).setValue(map);
                map.put("senderID", senderId);
                if (receiverId != null) {
                    map.put("receiverID", receiverId);
                } else {
                    map.put("receiverID", received);
                }
                map.put("message", message.getText().toString());
                map.put("time", myHelperClass.getCurrentDateHHMMSS_24());

                reference.child(key).setValue(map);

//                CreateChat();
                if (receiverId != null) {
                    chatReferenceForSender = FirebaseDatabase.getInstance().getReference("Conversations")
                            .child(senderId)
                            .child(receiverId);
                } else {
                    chatReferenceForSender = FirebaseDatabase.getInstance().getReference("Conversations")
                            .child(senderId)
                            .child(received);
                }

                chatReferenceForSender.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("ID", receiverId);
                            map.put("lastMessage", message.getText().toString());

                            map.put("time", myHelperClass.getCurrentDateHHMMSS_24());

                            chatReferenceForSender.setValue(map);
                            message.setText("");
                            getFCM();

                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("lastMessage", message.getText().toString());

                            map.put("time", myHelperClass.getCurrentDateHHMMSS_24());

                            chatReferenceForSender.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    message.setText("");
                                    getFCM();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if (receiverId != null) {
                    chatReferenceForReceiver = FirebaseDatabase.getInstance().getReference("Conversations")
                            .child(receiverId)
                            .child(senderId);
                } else {
                    chatReferenceForReceiver = FirebaseDatabase.getInstance().getReference("Conversations")
                            .child(received)
                            .child(senderId);
                }

                chatReferenceForReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("ID", senderId);
                            map.put("lastMessage", message.getText().toString());

                            map.put("time", myHelperClass.getCurrentDateHHMMSS_24());
                            chatReferenceForReceiver.setValue(map);
                            message.setText("");
                            getFCM();


                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("lastMessage", message.getText().toString());

                            map.put("time", myHelperClass.getCurrentDateHHMMSS_24());
                            chatReferenceForReceiver.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    Log.d("database", "" + databaseError);
                                    message.setText("");
                                    getFCM();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (receiverId != null && !receiverId.isEmpty()) {

            notification = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
            notification.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                                    /*sendNotification(snapshot.child("Name").getValue(String.class), message.getText().toString(),
                                            myHelperClass.getCurrentDateHHMMSS_24(), senderId, snapshot.child("FCMtoken").getValue(String.class));*/
                        readMessage(receiverId, senderId);
                    }
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });
        }
        else {

            notification = FirebaseDatabase.getInstance().getReference("Users").child(received);
            notification.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                                    /*sendNotification(sessionManager.getName(), message.getText().toString(),
                                            myHelperClass.getCurrentDateHHMMSS_24(), senderId, snapshot.child("FCMtoken").getValue(String.class));*/
                        readMessage(received, senderId);
                    }
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });
        }
    }

    public void getFCM() {
        DatabaseReference fcmReference = FirebaseDatabase.getInstance().getReference("Users");
        fcmReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (receiverId != null && !receiverId.isEmpty()) {
                            if (snapshot.child(senderId).exists() && snapshot.child(receiverId).exists()) {
                                if (data.child("UserID").getValue(String.class).equals(receiverId)) {
                                    fcm = data.child("FCMtoken").getValue(String.class);
                                    sendNotification(sessionManager.getName(), text, myHelperClass.getCurrentDateHHMMSS_24(), senderId, fcm);
                                }
                            }
                        }
                        else {
                            if (snapshot.child(senderId).exists() && snapshot.child(received).exists()) {
                                if (data.child("UserID").getValue(String.class).equals(received)) {
                                    fcm = data.child("FCMtoken").getValue(String.class);
                                    sendNotification(sessionManager.getName(), text, myHelperClass.getCurrentDateHHMMSS_24(), senderId, fcm);
                                }
                            }
                        }
                    }

                }
                else {
                    Toast.makeText(ChatActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void readMessage(String receiverId, String senderId) {
        chatsModelList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatsModel chatsModel = new ChatsModel(dataSnapshot.child("message").getValue(String.class), dataSnapshot.child("time").getValue(String.class), dataSnapshot.child("senderID").getValue(String.class),dataSnapshot.child("receiverID").getValue(String.class));


                    if( (chatsModel.getReceiverId().equals(receiverId) && chatsModel.getSenderId().equals(senderId)) ||
                            (chatsModel.getReceiverId().equals(senderId) && chatsModel.getSenderId().equals(receiverId)) ) {
                        chatsModelList.add(chatsModel);
                    }

/*                    else if( (chatsModel.getReceiverId().equals(received) && chatsModel.getSenderId().equals(senderId)) ||
                            (chatsModel.getReceiverId().equals(senderId) && chatsModel.getSenderId().equals(received)) ) {
                        chatsModelList.add(chatsModel);
                    }*/
                }

                chatsAdapter = new ChatsAdapter(chatsModelList, ChatActivity.this);
                chatsRecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                chatsRecycler.setAdapter(chatsAdapter);
                chatsAdapter.notifyDataSetChanged();
                chatsRecycler.scrollToPosition(chatsRecycler.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendNotification(String name, String body, String time, String senderId, String fcm) {

        Gson gson = new Gson();
        MessageNotificationModel notificationModel = new MessageNotificationModel();
        notificationModel.notification.name = name;
        notificationModel.notification.body = body;
        notificationModel.notification.time = time;
        notificationModel.notification.senderId = senderId;

        notificationModel.data.name = name;
        notificationModel.data.body = body;
        notificationModel.data.time = time;
        notificationModel.data.senderId = senderId;

        notificationModel.to = fcm;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),
                gson.toJson(notificationModel));
        final Request request = new Request.Builder().header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAA3ooFMrQ:APA91bHPOWcv8EDC95A_trU1KA0cliJ2_4hzguHUFodeQJdZLfFCht9SqUG-vnVYfESUWRb3UXPWfkHQO8aMUVj_Qv5Txgjs32zPTu5WPxxuZ0nxw6rGVO_YQLoMS_rxjCq_ybj0sIYq").url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody).build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp fail", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("okhttp response", response.toString());
                /*message.setText("");*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        if (received != null) {
            readMessage(received, senderId);
        }
        else {
            readMessage(receiverId, senderId);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void messageReceived() {
     if (active) {
         if (received != null) {
             readMessage(received, senderId);
         }
         else {
             readMessage(receiverId, senderId);
         }
     }
    }
}