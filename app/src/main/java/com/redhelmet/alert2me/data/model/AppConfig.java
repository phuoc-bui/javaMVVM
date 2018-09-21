package com.redhelmet.alert2me.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by inbox on 2/1/18.
 */

public class AppConfig implements Model {
    @SerializedName("tertiaryColor")
    private String navColor;
    private String textColor;
    private String statusBarColor;
    private String secondaryColor;
    private String primaryColor;
    private String termsAndConditionUrl;
    private String helpSupportUrl;
    private String helpSupportEmail;
    private String baseWms;
    private boolean showMobileWatchZone;
    @SerializedName("hintScreen")
    private List<Hint> hintsScreen;

    public void setNavColor(String navColor) {
        this.navColor = navColor;
    }

    public String getNavColor() {
        return this.navColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextColor() {
        return this.textColor;
    }

    public void setStatusBarColor(String statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public String getStatusBarColor() {
        return this.statusBarColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getSecondaryColor() {
        return this.secondaryColor;
    }

    public List<Hint> getHintsScreen() {
        return hintsScreen;
    }

    public void setHintsScreen(List<Hint> hintsScreen) {
        this.hintsScreen = hintsScreen;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getPrimaryColor() {
        return this.primaryColor;
    }

    public void setTermsAndConditionUrl(String termsAndConditionUrl) {
        this.termsAndConditionUrl = termsAndConditionUrl;
    }

    public String getTermsAndConditionUrl() {
        return this.termsAndConditionUrl;
    }

    public String getHelpSupportUrl() {
        return helpSupportUrl;
    }

    public void setHelpSupportUrl(String helpSupportUrl) {
        this.helpSupportUrl = helpSupportUrl;
    }

    public String getHelpSupportEmail() {
        return helpSupportEmail;
    }

    public void setHelpSupportEmail(String helpSupportEmail) {
        this.helpSupportEmail = helpSupportEmail;
    }

    public String getBaseWms() {
        return baseWms;
    }

    public void setBaseWms(String baseWms) {
        this.baseWms = baseWms;
    }

    public boolean isShowMobileWatchZone() {
        return showMobileWatchZone;
    }

    public void setShowMobileWatchZone(boolean showMobileWatchZone) {
        this.showMobileWatchZone = showMobileWatchZone;
    }
}
