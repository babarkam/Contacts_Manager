package com.example.contacts_manager.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ContactsModel implements Parcelable,Serializable {

    String contactID, name, picture, gender, pinLocation, pinAddress;
    AddressModel addressModel;
    PhoneModel phoneModel;



    public ContactsModel(String contactID, String name, String picture, String gender, String pinLocation, AddressModel addressModel, PhoneModel phoneModel, String pinAddress) {
        this.contactID = contactID;
        this.name = name;
        this.picture = picture;
        this.gender = gender;
        this.pinLocation = pinLocation;
        this.addressModel = addressModel;
        this.phoneModel = phoneModel;
        this.pinAddress = pinAddress;
    }

    protected ContactsModel(Parcel in) {
        contactID = in.readString();
        name = in.readString();
        picture = in.readString();
        gender = in.readString();
        pinLocation = in.readString();
        pinAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactID);
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(gender);
        dest.writeString(pinLocation);
        dest.writeString(pinAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContactsModel> CREATOR = new Creator<ContactsModel>() {
        @Override
        public ContactsModel createFromParcel(Parcel in) {
            return new ContactsModel(in);
        }

        @Override
        public ContactsModel[] newArray(int size) {
            return new ContactsModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPinLocation() {
        return pinLocation;
    }

    public void setPinLocation(String pinLocation) {
        this.pinLocation = pinLocation;
    }

    public AddressModel getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(AddressModel addressModel) {
        this.addressModel = addressModel;
    }

    public PhoneModel getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(PhoneModel phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getPinAddress() { return pinAddress; }

    public void setPinAddress(String pinAddress) { this.pinAddress = pinAddress; }
}
