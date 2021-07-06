package com.example.contacts_manager.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.contacts_manager.Models.AddressModel;
import com.example.contacts_manager.Models.PhoneModel;
import com.example.contacts_manager.R;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.GetLocationDetail;
import com.example.easywaylocation.Listener;
import com.example.easywaylocation.LocationData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddContact extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Listener, LocationData.AddressCallBack  {


    String[] gender = {"Select gender", "Male", "Female", "Other"};
    Spinner genderSpinner;
    Button save;
    Boolean genderSelected = true;
    EditText name,house, street, city, country, zip;
    ImageView back;
    TextInputEditText mobilePhone,  workPhone, extraPhone, emergencyPhone, contactProfile;
    DatabaseReference databaseReference;
    AddressModel addressModel;
    PhoneModel phoneModel;
    ImageView addWork, addExtra, addEmergency;
    RelativeLayout rel_work, rel_extra, rel_emergency;
    Boolean work = false;
    Boolean extra = false;
    Boolean emergency = false;
    ImageView imageChooser;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Uri profileImage;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageUrl = "";
    String pinAddress = "";
    String latLong = "";
    GetLocationDetail getLocationDetail;

    EasyWayLocation easyWayLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        save = findViewById(R.id.btn_contactSave);
        name = findViewById(R.id.et_contactName);
        mobilePhone = findViewById(R.id.et_contactPhoneMobile);
        house = findViewById(R.id.et_contactHouseNo);
        street = findViewById(R.id.et_contactStreet);
        city = findViewById(R.id.et_contactCity);
        country = findViewById(R.id.et_contactCountry);
        zip = findViewById(R.id.et_contactZip);
        genderSpinner = findViewById(R.id.sp_contactGender);
        genderSpinner.setOnItemSelectedListener(this);
        back = findViewById(R.id.iv_contactBack);
        addWork = findViewById(R.id.iv_contactAddWorkPhone);
        addExtra = findViewById(R.id.iv_contactAddExtraPhone);
        addEmergency = findViewById(R.id.iv_contactAddEmergencyPhone);
        rel_work = findViewById(R.id.rel_workPhone);
        rel_extra = findViewById(R.id.rel_extraPhone);
        rel_emergency = findViewById(R.id.rel_emergencyPhone);
        workPhone = findViewById(R.id.et_contactPhoneWork);
        extraPhone = findViewById(R.id.et_contactPhoneExtra);
        emergencyPhone = findViewById(R.id.et_contactPhoneEmergency);
        imageChooser = findViewById(R.id.iv_contactProfileChooser);
        contactProfile = findViewById(R.id.et_contactProfile);

        contactProfile.setEnabled(false);

        easyWayLocation = new EasyWayLocation(AddContact.this, false,false,AddContact.this);
        easyWayLocation.startLocation();
        getLocationDetail = new GetLocationDetail(this, this);

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            profileImage = data.getData();
                            uploadImage(profileImage);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImage);
                                imageChooser.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        imageChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
            }
        });



        addWork.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (!work) {
                    rel_work.setVisibility(View.VISIBLE);
                    addWork.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
                    work = true;
                } else if (work) {
                    rel_work.setVisibility(View.GONE);
                    rel_extra.setVisibility(View.GONE);
                    rel_emergency.setVisibility(View.GONE);
                    addWork.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    work = false;

                }
            }
        });

        addExtra.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!extra) {
                    rel_extra.setVisibility(View.VISIBLE);
                    addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
                    extra = true;
                } else if (extra) {
                    rel_extra.setVisibility(View.GONE);
                    rel_emergency.setVisibility(View.GONE);
                    addExtra.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    extra = false;

                }
            }
        });

        addEmergency.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!emergency) {
                    rel_emergency.setVisibility(View.VISIBLE);
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24));
                    emergency = true;
                } else if (emergency) {
                    rel_emergency.setVisibility(View.GONE);
                    addEmergency.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
                    emergency = false;

                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddContact.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    Toast.makeText(AddContact.this, "Contact created", Toast.LENGTH_SHORT).show();
                    writeDataToDB();
                    Intent intent2 = new Intent(AddContact.this, MainActivity.class);
                    startActivity(intent2);
                    finish();

                }
            }
        });

        ArrayAdapter gg = new ArrayAdapter(this, android.R.layout.simple_spinner_item,gender);
        gg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(gg);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            genderSelected = false;
        }
        else {
            genderSelected = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public boolean validation() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Missing");
            return false;
        }
        if (mobilePhone.getText().toString().isEmpty()) {
            mobilePhone.setError("Missing");
            return false;
        }
        if (!genderSelected) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (house.getText().toString().isEmpty()) {
            house.setError("Missing");
            return false;
        }
        if (street.getText().toString().isEmpty()) {
            street.setError("Missing");
            return false;
        }
        if (city.getText().toString().isEmpty()) {
            city.setError("Missing");
            return false;
        }
        if (country.getText().toString().isEmpty()) {
            country.setError("Missing");
            return false;
        }
        if (zip.getText().toString().isEmpty()) {
            zip.setError("Missing");
            return false;
        }

        return true;
    }

    public void writeDataToDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ContactDetails");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Map<String, Object> contacts = new HashMap<>();
                    String key = databaseReference.push().getKey();
                    databaseReference.child(key).setValue(contacts);
                    addressModel = new AddressModel(house.getText().toString(), street.getText().toString(), city.getText().toString(),
                            country.getText().toString(), zip.getText().toString());
                    phoneModel = new PhoneModel(mobilePhone.getText().toString(), workPhone.getText().toString(), extraPhone.getText().toString(), emergencyPhone.getText().toString());
                    //databaseReference.child(snapshot.getKey()).setValue(contacts);
                    contacts.put("ContactID", key);
                    contacts.put("Name", name.getText().toString());
                    contacts.put("Picture", imageUrl);
                    contacts.put("Gender", genderSpinner.getSelectedItem().toString());
                    contacts.put("Pin", latLong);
                    contacts.put("Address", addressModel);
                    contacts.put("PhoneNumbers", phoneModel);
                    contacts.put("PinAddress", pinAddress);
                  //  databaseReference.push().setValue(contacts);
                    databaseReference.child(key).setValue(contacts);

                    /*contacts.put(snapshot.getKey(), new ContactsModel(name.getText().toString(), phone.getText().toString(),
                            "hehehe", genderSpinner.getSelectedItem().toString(), "hahaha", addressModel));
                    databaseReference.child(snapshot.getKey()).setValue(contacts);*/

                }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    public void getPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(AddContact.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(gallery);


            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddContact.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }




        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET)
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
                                            .makeText(AddContact.this,
                                                    "Image Uploaded",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUrl = uri.toString();

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
                                    .makeText(AddContact.this,
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
        pinAddress = locationData.getFull_address();
    }

}