package com.redhelmet.alert2me.data.remote.response;

public class RegisterResponse {
    public boolean success;
    public String errorMessage;
    public Device device;

    public static class Device {
        public String id;
        public String apiToken;
    }
}