package com.example.contacts_manager.Models;

import android.app.Notification;

public class MessageNotificationModel {
    public String to;

    public Notification notification = new Notification();
    public Data data = new Data();


    public static class Notification {
        public String name;
        public String body;
        public String time;
        public String senderId;
    }

    public static class Data {
        public String name;
        public String body;
        public String time;
        public String senderId;
    }
}
