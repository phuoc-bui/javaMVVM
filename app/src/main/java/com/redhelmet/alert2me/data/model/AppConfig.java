package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.Model;

/**
 * Created by inbox on 2/1/18.
 */

public class AppConfig implements Model {
    private String termsAndConditionUrl;
    private String supportUrl;
    private String baseWms;

    public AppConfig() {
        termsAndConditionUrl = "https://a2me-api.redhelmet.tech/legal";
    }

    public void setTermsAndConditionUrl(String termsAndConditionUrl) {
        this.termsAndConditionUrl = termsAndConditionUrl;
    }

    public String getTermsAndConditionUrl() {
        return this.termsAndConditionUrl;
    }

    public String getSupportUrl() {
        return supportUrl;
    }

    public void setSupportUrl(String supportUrl) {
        this.supportUrl = supportUrl;
    }

    public String getBaseWms() {
        return baseWms;
    }

    public void setBaseWms(String baseWms) {
        this.baseWms = baseWms;
    }
}