package com.example.contacts_manager.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PhoneModel implements Parcelable, Serializable {
    String mobile, work, extra, emergency;

    public PhoneModel(String mobile, String work, String extra, String emergency) {
        this.mobile = mobile;
        this.work = work;
        this.extra = extra;
        this.emergency = emergency;
    }

    public PhoneModel() {
    }

    protected PhoneModel(Parcel in) {
        mobile = in.readString();
        work = in.readString();
        extra = in.readString();
        emergency = in.readString();
    }

    public static final Creator<PhoneModel> CREATOR = new Creator<PhoneModel>() {
        @Override
        public PhoneModel createFromParcel(Parcel in) {
            return new PhoneModel(in);
        }

        @Override
        public PhoneModel[] newArray(int size) {
            return new PhoneModel[size];
        }
    };

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobile);
        dest.writeString(work);
        dest.writeString(extra);
        dest.writeString(emergency);
    }
}
