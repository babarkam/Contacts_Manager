package com.example.contacts_manager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contacts_manager.Activities.ChatActivity;
import com.example.contacts_manager.Models.ConversationsModel;
import com.example.contacts_manager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationsViewHolder> {
    List<ConversationsModel> conversationsModelList;
    Context context;
    DatabaseReference databaseReference;
    DatabaseReference lastMessage;
    DatabaseReference chat;
    String receiverId = "";
    public ConversationsAdapter(List<ConversationsModel> conversationsModelList, Context context) {
        this.conversationsModelList = conversationsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationsAdapter.ConversationsViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.conversations_list_card, parent, false);
        return new ConversationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ConversationsAdapter.ConversationsViewHolder holder, int position) {

        holder.message.setText(conversationsModelList.get(position).getLastMessage());
        holder.time.setText((conversationsModelList.get(position).getTime()));
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(conversationsModelList.get(position).getReceiverId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = "";
                    name = snapshot.child("Name").getValue(String.class);
                    receiverId = snapshot.child("UserID").getValue(String.class);
                    holder.name.setText(name);
                    holder.icon.setText(name.toUpperCase().charAt(0) + "");
               //     holder.icon.setText(snapshot.child("Name").getValue(String.class).toUpperCase().charAt(0));
                    //holder.rel.setBackgroundTintList(context.getResources().getColorStateList(R.color.orange));
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.card_conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat = FirebaseDatabase.getInstance().getReference("Users").child(conversationsModelList.get(position).getReceiverId());
                chat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            receiverId = snapshot.child("UserID").getValue(String.class);
                            Intent intent=new Intent(context, ChatActivity.class);
                            intent.putExtra("name",holder.name.getText().toString());
                            intent.putExtra("receiverId", receiverId);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return conversationsModelList.size();
    }

    public class ConversationsViewHolder extends RecyclerView.ViewHolder {
        //ImageView picture;
        RelativeLayout rel;
        TextView name, message, time, icon;
        CardView card_conversation;

        public ConversationsViewHolder(@NonNull View itemView) {

            super(itemView);
            //this.picture = itemView.findViewById(R.id.iv_conversationImage);
            this.card_conversation = itemView.findViewById(R.id.card_conversation);
            this.rel = itemView.findViewById(R.id.rel_icon);
            this.name = itemView.findViewById(R.id.tv_conversationName);
            this.message = itemView.findViewById(R.id.tv_conversationLastMsg);
            this.time = itemView.findViewById(R.id.tv_conversationTime);
            this.icon = itemView.findViewById(R.id.tv_conversationCircle);
        }
    }
}
