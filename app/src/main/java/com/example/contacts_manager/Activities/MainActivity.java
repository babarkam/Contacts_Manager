package com.example.contacts_manager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.contacts_manager.Adapters.ContactsAdapter;
import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.ContactsModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.SessionManager;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.GetLocationDetail;
import com.example.easywaylocation.Listener;
import com.example.easywaylocation.LocationData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Listener, LocationData.AddressCallBack, NavigationView.OnNavigationItemSelectedListener{

    List<ContactsModel> contactsModelList;
    RecyclerView contactsRecycler;
    ContactsAdapter contactsAdapter;
    AddressModel addressModel;
    PhoneModel phoneModel;
    ImageView contactAdd, menu, drawer;
    DatabaseReference databaseReference;
    AlertDialog.Builder builder;
    SessionManager sessionManager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;


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
        sessionManager = new SessionManager(this);
        menu = findViewById(R.id.iv_contactMenu);
        drawer = findViewById(R.id.iv_contactDrawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navView);
        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.close, R.string.open);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( MainActivity.this);


        easyWayLocation = new EasyWayLocation(MainActivity.this, false,false,MainActivity.this);
        easyWayLocation.startLocation();
        getLocationDetail = new GetLocationDetail(this, this);


        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, menu);

                popupMenu.getMenuInflater().inflate(R.menu.option_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.menuItem1:
                                sessionManager.logOut();
                                Intent logOut = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(logOut);
                                finish();
                                break;
                        }

                        return true;
                    }
                });

                popupMenu.show();


            }
        });



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
                finish();
            }
        });

        contactsModelList = new ArrayList<>();
        contactsRecycler = findViewById(R.id.rv_contacts);


    }

    public void getDataFromDB() {
        contactsModelList.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getuserId()).child("ContactDetails");
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
                        addressModel.setHousePin(dataSnapshot.child("Address").child("housePin").getValue(String.class));

                        phoneModel = new PhoneModel();
                        phoneModel.setMobile(dataSnapshot.child("PhoneNumbers").child("mobile").getValue(String.class));
                        phoneModel.setWork(dataSnapshot.child("PhoneNumbers").child("work").getValue(String.class));
                        phoneModel.setExtra(dataSnapshot.child("PhoneNumbers").child("extra").getValue(String.class));
                        phoneModel.setEmergency(dataSnapshot.child("PhoneNumbers").child("emergency").getValue(String.class));

                        contactsModelList.add(new ContactsModel(dataSnapshot.child("ContactID").getValue(String.class), dataSnapshot.child("Name").getValue(String.class),
                                dataSnapshot.child("Picture").getValue(String.class), dataSnapshot.child("Gender").getValue(String.class), dataSnapshot.child("Pin").getValue(String.class),
                                addressModel, phoneModel, dataSnapshot.child("PinAddress").getValue(String.class)));
                    }
                    contactsAdapter = new ContactsAdapter( contactsModelList, MainActivity.this);
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

        builder.setMessage("Do you want to log out").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logOut();
                        Intent goBack = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(goBack);
                        finish();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("Logging Out");
        alert.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case R.id.menu_conversations:
                Intent convIntent = new Intent(MainActivity.this, ConversationsActivity.class);
                startActivity(convIntent);
                finish();
                break;

            case R.id.menu_logout:
                sessionManager.logOut();
                Intent log = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(log);
                finish();
        }


        drawerLayout.closeDrawers();
        return true;
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