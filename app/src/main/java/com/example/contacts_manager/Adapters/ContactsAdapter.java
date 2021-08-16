package com.example.contacts_manager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contacts_manager.Activities.AddContact;
import com.example.contacts_manager.Activities.ChatActivity;
import com.example.contacts_manager.Activities.ViewContact;
import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.ContactsModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    List<ContactsModel> contactsModelList;
    Context context;
    DatabaseReference databaseReference;
    DatabaseReference idGetter;
    String userId = "";
    String name = "";

    public ContactsAdapter(List<ContactsModel>contactsModelList, Context context) {
        this.context = context;
        this.contactsModelList = contactsModelList;

    }

    @NonNull
    @Override
    public ContactsAdapter.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contacts_list_card,parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ContactsViewHolder holder, int position) {
        holder.name.setText(contactsModelList.get(position).getName());
        holder.phone.setText(contactsModelList.get(position).getPhoneModel().getMobile());

        if (!contactsModelList.get(position).getPicture().equals("")) {
            Glide.with(context).load(contactsModelList.get(position).getPicture()).into(holder.picture);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ContactsModel>  contact = new ArrayList<>();
                List<AddressModel>  address = new ArrayList<>();
                List<PhoneModel>  phone = new ArrayList<>();
                Intent intent = new Intent(context, ViewContact.class);
                contact.add(contactsModelList.get(position));
                address.add(contactsModelList.get(position).getAddressModel());
                phone.add(contactsModelList.get(position).getPhoneModel());
                intent.putExtra("contact", (Serializable) contact);
                intent.putExtra("address", (Serializable) address);
                intent.putExtra("phone", (Serializable) phone);

                context.startActivity(intent);
            }
        });
        holder.pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "http://maps.google.com/maps?saddr=" + contactsModelList.get(position).getPinLocation() + "&daddr=" + contactsModelList.get(position).getAddressModel().getHousePin();
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent2);

            }
        });


        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, v);
                if (!contactsModelList.get(position).getPhoneModel().getMobile().isEmpty()) {
                popupMenu.getMenu().add(contactsModelList.get(position).getPhoneModel().getMobile());}
                if (!contactsModelList.get(position).getPhoneModel().getWork().isEmpty()) {
                popupMenu.getMenu().add(contactsModelList.get(position).getPhoneModel().getWork());}
                if (!contactsModelList.get(position).getPhoneModel().getExtra().isEmpty()) {
                popupMenu.getMenu().add(contactsModelList.get(position).getPhoneModel().getExtra());}
                if (!contactsModelList.get(position).getPhoneModel().getEmergency().isEmpty()) {
                popupMenu.getMenu().add(contactsModelList.get(position).getPhoneModel().getEmergency());}
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + item.getTitle().toString()));
                        context.startActivity(callIntent);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idGetter = FirebaseDatabase.getInstance().getReference("Users");
                idGetter.orderByChild("Phone").equalTo(contactsModelList.get(position).getPhoneModel().getMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                 userId = dataSnapshot.child("UserID").getValue(String.class);
                                 name = dataSnapshot.child("Name").getValue(String.class);


                                Intent chatIntent = new Intent(context, ChatActivity.class);
                                chatIntent.putExtra("userId", userId);
                                chatIntent.putExtra("name", name);
                                //chatIntent.putExtra("name", contactsModelList.get(position).getName());
                                context.startActivity(chatIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("Phone").equalTo(contactsModelList.get(position).getPhoneModel().getMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    holder.message.setColorFilter(R.color.quantum_grey600);
                    //Toast.makeText(context, contactsModelList.get(position).getName() + " has not installed Contacts Manager on their phone", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsModelList.size();
    }



    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        ImageView picture, pin, call, message;
        LinearLayout edit;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tv_contactName);
            this.phone = itemView.findViewById(R.id.tv_contactPhone);
            this.picture = itemView.findViewById(R.id.iv_contactImage);
            this.pin = itemView.findViewById(R.id.iv_contactPin);
            this.edit = itemView.findViewById(R.id.linear_card);
            this.call = itemView.findViewById(R.id.iv_contactCall);
            this.message = itemView.findViewById(R.id.iv_contactMessage);

        }
    }
}
