package com.example.contacts_manager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contacts_manager.Activities.AddContact;
import com.example.contacts_manager.Activities.ViewContact;
import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.ContactsModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    List<ContactsModel> contactsModelList;
    Context context;
    String location;

    public ContactsAdapter(List<ContactsModel>contactsModelList, Context context, String location) {
        this.context = context;
        this.contactsModelList = contactsModelList;
        this.location = location;

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
                String uri = "http://maps.google.com/maps?saddr=" + location + "&daddr=" + contactsModelList.get(position).getPinLocation();
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent2);

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsModelList.size();
    }



    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        ImageView picture, pin;
        LinearLayout edit;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tv_contactName);
            this.phone = itemView.findViewById(R.id.tv_contactPhone);
            this.picture = itemView.findViewById(R.id.iv_contactImage);
            this.pin = itemView.findViewById(R.id.iv_contactPin);
            this.edit = itemView.findViewById(R.id.linear_card);

        }
    }
}
