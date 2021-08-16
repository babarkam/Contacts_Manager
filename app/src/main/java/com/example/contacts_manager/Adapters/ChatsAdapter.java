package com.example.contacts_manager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts_manager.Models.ChatsModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.SessionManager;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter <ChatsAdapter.ChatsViewHolder> {

    List<ChatsModel> chatsModelList;
    Context context;
    DatabaseReference databaseReference;
    SessionManager sessionManager;
    private static final int MSG_TYPE_SENT = 1;
    private static final int MSG_TYPE_RECEIVED = 0;

    public ChatsAdapter(List<ChatsModel> chatsModelList, Context context) {
        this.chatsModelList = chatsModelList;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatsAdapter.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat, parent, false);
            return new ChatsViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat, parent, false);
            return new ChatsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.ChatsViewHolder holder, int position) {



        if (getItemViewType(position) == MSG_TYPE_SENT) {
            holder.sender.setText(chatsModelList.get(position).getMessage());
            holder.senderTime.setText(chatsModelList.get(position).getTime());
        }
        else {
            holder.receiver.setText(chatsModelList.get(position).getMessage());
            holder.receiverTime.setText(chatsModelList.get(position).getTime());
        }

    }

    @Override
    public int getItemCount() {
        return chatsModelList.size();
    }


    public class ChatsViewHolder extends RecyclerView.ViewHolder {

        TextView sender, receiver, senderTime, receiverTime;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sender = itemView.findViewById(R.id.tv_senderChat);
            this.receiver = itemView.findViewById(R.id.tv_receiverChat);
            this.senderTime = itemView.findViewById(R.id.tv_senderTime);
            this.receiverTime = itemView.findViewById(R.id.tv_receiverTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        sessionManager = new SessionManager(context);
        if (chatsModelList.get(position).getSenderId().equals(sessionManager.getuserId())) {
            return MSG_TYPE_SENT;
        }
        else {
            return MSG_TYPE_RECEIVED;
        }
    }
}
