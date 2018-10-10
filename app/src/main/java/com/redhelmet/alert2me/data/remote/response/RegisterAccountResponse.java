package com.redhelmet.alert2me.data.remote.response;

public class RegisterAccountResponse extends Response {
    public String message;
    public Account account;

    public static class Account {
        public String token;
    }
}
