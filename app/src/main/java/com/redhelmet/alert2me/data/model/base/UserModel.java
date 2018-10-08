package com.redhelmet.alert2me.data.model.base;

public class UserModel implements Model {
    private boolean userEdited;

    public boolean isUserEdited() {
        return userEdited;
    }

    public void setUserEdited(boolean userEdited) {
        this.userEdited = userEdited;
    }
}
