package com.redhelmet.alert2me.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by inbox on 2/1/18.
 */

public class Config {

    private static Config instance;
    String navColor;
    String textColor;
    String statusBarColor;
    String secondaryColor;
    String primaryColor;
    String termsAndConditionUrl;

    ArrayList<HashMap<String,String>> hintsScreen;

    public Config() {
    }

    public static Config getInstance() {

        if (instance == null)
            instance = new Config();
        return instance;
    }

    public void setNavColor(String navColor){
        this.navColor=navColor;
    }

    public String  getNavColor(){
        return this.navColor;
    }

    public void setTextColor(String textColor){
        this.textColor=textColor;
    }

    public String  getTextColor(){
        return this.textColor;
    }

    public void setStatusBarColor(String statusBarColor){
        this.statusBarColor=statusBarColor;
    }

    public String  getStatusBarColor(){
        return this.statusBarColor;
    }

    public void setSecondaryColor(String secondaryColor){
        this.secondaryColor=secondaryColor;
    }

    public String  getSecondaryColor(){
        return this.secondaryColor;
    }

    public void setHintsScreen(ArrayList<HashMap<String ,String>> hintsScreen){
        this.hintsScreen=hintsScreen;
    }
    public ArrayList<HashMap<String ,String>> getHintsScreen(){
        return this.hintsScreen;
    }

    public void setPrimaryColor(String primaryColor){
        this.primaryColor=primaryColor;
    }

    public String getPrimaryColor(){
        return this.primaryColor;
    }

    public void setTermsAndConditionUrl(String termsAndConditionUrl){
        this.termsAndConditionUrl=termsAndConditionUrl;
    }

    public String getTermsAndConditionUrl(){
        return this.termsAndConditionUrl;
    }
}
