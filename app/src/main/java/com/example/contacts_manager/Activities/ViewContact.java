package com.example.contacts_manager.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.ContactsModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;
import com.example.contacts_manager.Utils.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewContact extends AppCompatActivity implements OnMapReadyCallback {

    ImageView back, edit, image;
    TextView mainName, mainCity, heading;
    TextInputEditText name, mobile, work, extra, emergency, gender, houseNumber, street, city, country, zipCode;
    TextInputLayout lWork, lExtra, lEmergency;

    List<ContactsModel> contactsModel;
    List<AddressModel> addressModel;
    List<PhoneModel> phoneModel;

    FirebaseStorage storage;
    StorageReference storageReference;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Uri profilePic;
    String imageUrl;
    DatabaseReference databaseReference;
    SessionManager sessionManager;


    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        heading = findViewById(R.id.tv_viewHeading);
        back = findViewById(R.id.iv_viewBack);
        edit = findViewById(R.id.iv_viewEdit);
        image = findViewById(R.id.iv_viewImage);
        mainName = findViewById(R.id.tv_viewMainName);
        mainCity = findViewById(R.id.tv_viewMainCity);
        name = findViewById(R.id.et_viewName);
        mobile = findViewById(R.id.et_viewPhoneMobile);
        work = findViewById(R.id.et_viewPhoneWork);
        extra = findViewById(R.id.et_viewPhoneExtra);
        emergency = findViewById(R.id.et_viewPhoneEmergency);
        gender = findViewById(R.id.et_viewGender);
        houseNumber = findViewById(R.id.et_viewHouseNumber);
        street = findViewById(R.id.et_viewStreet);
        city = findViewById(R.id.et_viewCity);
        country = findViewById(R.id.et_viewCountry);
        zipCode = findViewById(R.id.et_viewZip);
        lWork = findViewById(R.id.textFieldWork);
        lExtra = findViewById(R.id.textFieldExtra);
        lEmergency = findViewById(R.id.textFieldEmergency);
        sessionManager = new SessionManager(this);

        contactsModel = new ArrayList<>();
        contactsModel = (List<ContactsModel>) getIntent().getSerializableExtra("contact");
        addressModel = new ArrayList<>();
        addressModel = (List<AddressModel>) getIntent().getSerializableExtra("address");
        phoneModel = new ArrayList<>();
        phoneModel = (List<PhoneModel>) getIntent().getSerializableExtra("phone");





        heading.setText(contactsModel.get(0).getName() + "'s " + "contact");

        name.setEnabled(false);
        mobile.setEnabled(false);
        work.setEnabled(false);
        extra.setEnabled(false);
        emergency.setEnabled(false);
        gender.setEnabled(false);
        houseNumber.setEnabled(false);
        street.setEnabled(false);
        city.setEnabled(false);
        country.setEnabled(false);
        zipCode.setEnabled(false);

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            profilePic = data.getData();
                            uploadImage(profilePic);

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePic);
                                image.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ContactsModel>  contact = new ArrayList<>();
                List<AddressModel>  address = new ArrayList<>();
                List<PhoneModel>  phone = new ArrayList<>();
                Intent intent2 = new Intent(ViewContact.this, EditContact.class);
                contact.add(contactsModel.get(0));
                address.add(addressModel.get(0));
                phone.add(phoneModel.get(0));
                intent2.putExtra("edit_contact", (Serializable) contact);
                intent2.putExtra("edit_address", (Serializable) address);
                intent2.putExtra("edit_phone", (Serializable) phone);
                startActivity(intent2);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewContact.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (phoneModel.get(0).getWork().isEmpty()) {
            lWork.setVisibility(View.GONE);
        }
        if (phoneModel.get(0).getExtra().isEmpty()) {
            lExtra.setVisibility(View.GONE);
        }
        if (phoneModel.get(0).getEmergency().isEmpty()) {
            lEmergency.setVisibility(View.GONE);
        }

        Glide.with(this).load(contactsModel.get(0).getPicture()).into(image);
        mainName.setText(contactsModel.get(0).getName());
        mainCity.setText(addressModel.get(0).getCity() + ", " + addressModel.get(0).getCountry());
        name.setText(contactsModel.get(0).getName());
        mobile.setText(phoneModel.get(0).getMobile());
        work.setText(phoneModel.get(0).getWork());
        extra.setText(phoneModel.get(0).getExtra());
        emergency.setText(phoneModel.get(0).getEmergency());
        gender.setText(contactsModel.get(0).getGender());
        houseNumber.setText(addressModel.get(0).getHouseNumber());
        street.setText(addressModel.get(0).getStreet());
        city.setText(addressModel.get(0).getCity());
        country.setText(addressModel.get(0).getCountry());
        zipCode.setText(addressModel.get(0).getZipCode());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mv_viewMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (!contactsModel.get(0).getPinLocation().isEmpty()) {
            String[] latLong = contactsModel.get(0).getPinLocation().split(",");
            double lat = Double.parseDouble(latLong[0]);
            double lon = Double.parseDouble(latLong[1]);
            LatLng sydney = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15.0f));
        }
    }

    public void getPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ViewContact.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(gallery);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ViewContact.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }




        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    private void uploadImage(Uri filePath)
    {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ViewContact.this,
                                                    "Image Uploaded",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUrl = uri.toString();
                                            updatePic();

                                        }
                                    });
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ViewContact.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

    public void updatePic() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getuserId()).child("ContactDetails").child(contactsModel.get(0).getContactID()).child("Picture");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}