package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.contacts_manager.Adapters.ConversationsAdapter;
import com.example.contacts_manager.Models.ConversationsModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConversationsActivity extends AppCompatActivity {

    List<ConversationsModel> conversationsModelList;
    RecyclerView conversationsRecycler;
    ConversationsAdapter conversationsAdapter;
    ImageView back;
    SwipeRefreshLayout swipeRefresh;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        back = findViewById(R.id.iv_conversationBack);
        conversationsModelList = new ArrayList<>();
        conversationsRecycler = findViewById(R.id.rv_conversations);
        swipeRefresh = findViewById(R.id.conversationSwipeRefresh);



        getConversations();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ConversationsActivity.this, MainActivity.class);
                startActivity(back);
                finish();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConversations();
                //fetchTimelineAsync(0);
                swipeRefresh.setRefreshing(false);
            }
        });

        getConversations();

    }
    public void getConversations() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Conversations").child(new SessionManager(ConversationsActivity.this).getuserId());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationsModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    conversationsModelList.add(new ConversationsModel(new SessionManager(ConversationsActivity.this).getuserId(), dataSnapshot.child("ID").getValue(String.class),
                            dataSnapshot.child("lastMessage").getValue(String.class), dataSnapshot.child("time").getValue(String.class)));
                }

                Collections.sort(conversationsModelList, new Comparator<ConversationsModel>() {
                    @Override
                    public int compare(ConversationsModel o1, ConversationsModel o2) {
                        return o2.getTime().compareTo(o1.getTime());
                    }
                });


                //Collections.reverse(conversationsModelList);
                conversationsAdapter = new ConversationsAdapter(conversationsModelList,ConversationsActivity.this);

                conversationsRecycler.setLayoutManager(new LinearLayoutManager(ConversationsActivity.this));
                conversationsRecycler.setAdapter(conversationsAdapter);
                conversationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}