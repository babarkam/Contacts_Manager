package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contacts_manager.Adapters.ContactsAdapter;
import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.ContactsModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.GetLocationDetail;
import com.example.easywaylocation.Listener;
import com.example.easywaylocation.LocationData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Listener, LocationData.AddressCallBack{

    List<ContactsModel> contactsModelList;
    RecyclerView contactsRecycler;
    ContactsAdapter contactsAdapter;
    AddressModel addressModel;
    PhoneModel phoneModel;
    ImageView contactAdd;
    DatabaseReference databaseReference;
    AlertDialog.Builder builder;


    SwipeRefreshLayout swipeRefresh;
    String latLong = "";
    GetLocationDetail getLocationDetail;

    EasyWayLocation easyWayLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactAdd = findViewById(R.id.iv_contactAdd);
        builder = new AlertDialog.Builder(this);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        easyWayLocation = new EasyWayLocation(MainActivity.this, false,false,MainActivity.this);
        easyWayLocation.startLocation();
        getLocationDetail = new GetLocationDetail(this, this);



        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromDB();
                //fetchTimelineAsync(0);
                swipeRefresh.setRefreshing(false);
            }
        });

        contactAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddContact.class);
                startActivity(intent);
            }
        });

        contactsModelList = new ArrayList<>();
        contactsRecycler = findViewById(R.id.rv_contacts);


    }

    public void getDataFromDB() {
        contactsModelList.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ContactDetails");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        addressModel=new AddressModel();
                        addressModel.setHouseNumber(dataSnapshot.child("Address").child("houseNumber").getValue(String.class));
                        addressModel.setStreet(dataSnapshot.child("Address").child("street").getValue(String.class));
                        addressModel.setCity(dataSnapshot.child("Address").child("city").getValue(String.class));
                        addressModel.setCountry(dataSnapshot.child("Address").child("country").getValue(String.class));
                        addressModel.setZipCode(dataSnapshot.child("Address").child("zipCode").getValue(String.class));

                        phoneModel = new PhoneModel();
                        phoneModel.setMobile(dataSnapshot.child("PhoneNumbers").child("mobile").getValue(String.class));
                        phoneModel.setWork(dataSnapshot.child("PhoneNumbers").child("work").getValue(String.class));
                        phoneModel.setExtra(dataSnapshot.child("PhoneNumbers").child("extra").getValue(String.class));
                        phoneModel.setEmergency(dataSnapshot.child("PhoneNumbers").child("emergency").getValue(String.class));

                        contactsModelList.add(new ContactsModel(dataSnapshot.child("ContactID").getValue(String.class), dataSnapshot.child("Name").getValue(String.class),
                                dataSnapshot.child("Picture").getValue(String.class), dataSnapshot.child("Gender").getValue(String.class), dataSnapshot.child("Pin").getValue(String.class),
                                addressModel, phoneModel, dataSnapshot.child("PinAddress").getValue(String.class)));
                    }
                    contactsAdapter = new ContactsAdapter( contactsModelList, MainActivity.this, latLong);
                    contactsRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    contactsRecycler.setAdapter(contactsAdapter);
                }
                else {
                    Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }

    @Override
    public void locationOn() {

    }

    @Override
    public void currentLocation(Location location) {
        latLong = location.getLatitude() + "," + location.getLongitude();
        getLocationDetail.getAddress(location.getLatitude(), location.getLongitude(), "xyz");
    }

    @Override
    public void locationCancelled() {

    }

    @Override
    public void locationData(LocationData locationData) {

    }

    @Override
    public void onBackPressed() {

        builder.setMessage("Do you want to close this application?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("Closing App");
        alert.show();
    }

/*    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // ...the data has come back, add new items to your adapter...
                adapter.addAll(...);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }*/
}