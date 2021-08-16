package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.ContactsModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditContact extends AppCompatActivity {
    ImageView back, confirm;
    TextInputEditText name, mobile, work, extra, emergency, houseNumber, street, city, country, zipCode;
    RelativeLayout rel_work, rel_extra, rel_emergency;
    List<ContactsModel> contactsModel;
    List<AddressModel> addressModel;
    List<PhoneModel> phoneModel;
    ImageView addWork, addExtra, addEmergency;
    Boolean bool_work = false;
    Boolean bool_extra = false;
    Boolean bool_emergency = false;
    DatabaseReference databaseReference;
    AddressModel address;
    PhoneModel phone;
    SessionManager sessionManager;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        back = findViewById(R.id.iv_editBack);
        name = findViewById(R.id.et_editName);
        mobile = findViewById(R.id.et_editMobile);
        work = findViewById(R.id.et_editWork);
        extra = findViewById(R.id.et_editExtra);
        emergency = findViewById(R.id.et_editEmergency);
        houseNumber = findViewById(R.id.et_editHouseNo);
        street = findViewById(R.id.et_editStreet);
        city = findViewById(R.id.et_editCity);
        country = findViewById(R.id.et_editCountry);
        zipCode = findViewById(R.id.et_editZip);
        rel_work = findViewById(R.id.rel_addWork);
        rel_extra = findViewById(R.id.rel_addExtra);
        rel_emergency = findViewById(R.id.rel_addEmergency);
        addWork = findViewById(R.id.iv_editAddWork);
        addExtra = findViewById(R.id.iv_editAddExtra);
        addEmergency = findViewById(R.id.iv_editAddEmergency);
        confirm = findViewById(R.id.iv_editConfirm);
        sessionManager = new SessionManager(this);



        contactsModel = new ArrayList<>();
        addressModel = new ArrayList<>();
        phoneModel = new ArrayList<>();

        contactsModel = (List<ContactsModel>) getIntent().getSerializableExtra("edit_contact");
        addressModel = (List<AddressModel>) getIntent().getSerializableExtra("edit_address");
        phoneModel = (List<PhoneModel>) getIntent().getSerializableExtra("edit_phone");


        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bool_work) {
                    rel_work.setVisibility(View.VISIBLE);
                    addWork.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
                    bool_work = true;
                } else if (bool_work) {
                    rel_work.setVisibility(View.GONE);
                    rel_extra.setVisibility(View.GONE);
                    rel_emergency.setVisibility(View.GONE);
                    addWork.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    bool_work = false;
                    bool_extra = false;
                    bool_emergency = false;
                }
            }
        });

        addExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bool_extra) {
                    rel_extra.setVisibility(View.VISIBLE);
                    addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
                    bool_extra = true;
                } else if (bool_extra) {
                    rel_extra.setVisibility(View.GONE);
                    rel_emergency.setVisibility(View.GONE);
                    addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    bool_extra = false;
                    bool_emergency = false;
                }
            }
        });

        addEmergency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bool_emergency) {
                    rel_emergency.setVisibility(View.VISIBLE);
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
                    bool_emergency = true;
                } else if (bool_emergency) {
                    rel_emergency.setVisibility(View.GONE);
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    bool_emergency = false;

                }
            }
        });




        if (!phoneModel.get(0).getWork().isEmpty()) {
            rel_work.setVisibility(View.VISIBLE);
            bool_work = true;
            addWork.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
        }
        if (!phoneModel.get(0).getExtra().isEmpty()) {
            rel_extra.setVisibility(View.VISIBLE);
            bool_extra = true;
            addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
        }
        if (!phoneModel.get(0).getEmergency().isEmpty()) {
            rel_emergency.setVisibility(View.VISIBLE);
            bool_emergency = true;
            addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
        }

        name.setText(contactsModel.get(0).getName());
        mobile.setText(phoneModel.get(0).getMobile());
        work.setText(phoneModel.get(0).getWork());
        extra.setText(phoneModel.get(0).getExtra());
        emergency.setText(phoneModel.get(0).getEmergency());
        houseNumber.setText(addressModel.get(0).getHouseNumber());
        street.setText(addressModel.get(0).getStreet());
        city.setText(addressModel.get(0).getCity());
        country.setText(addressModel.get(0).getCountry());
        zipCode.setText(addressModel.get(0).getZipCode());





        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataDB();
                Toast.makeText(EditContact.this, "Contact updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditContact.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    public void updateDataDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getuserId()).child("ContactDetails").child(contactsModel.get(0).getContactID());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Map<String, Object> edit = new HashMap<>();
                address = new AddressModel(houseNumber.getText().toString(), street.getText().toString(), city.getText().toString(), country.getText().toString(), zipCode.getText().toString(), "");
                phone = new PhoneModel(mobile.getText().toString(), work.getText().toString(), extra.getText().toString(), emergency.getText().toString());
                edit.put("ContactID", contactsModel.get(0).getContactID());
                edit.put("Name", name.getText().toString());
                edit.put("Picture", contactsModel.get(0).getPicture());
                edit.put("Gender", contactsModel.get(0).getGender());
                edit.put("Pin", contactsModel.get(0).getPinLocation());
                edit.put("Address", address);
                edit.put("PhoneNumbers", phone);
                edit.put("PinAddress", contactsModel.get(0).getPinAddress());
                databaseReference.setValue(edit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}