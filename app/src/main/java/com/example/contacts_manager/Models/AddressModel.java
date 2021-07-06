package com.example.contacts_manager.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AddressModel implements Parcelable, Serializable {
    String houseNumber, street, city, country, zipCode;

    public AddressModel(String houseNumber, String street, String city, String country, String zipCode) {
        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    public AddressModel() {
    }

    protected AddressModel(Parcel in) {
        houseNumber = in.readString();
        street = in.readString();
        city = in.readString();
        country = in.readString();
        zipCode = in.readString();
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(houseNumber);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(zipCode);
    }
}
