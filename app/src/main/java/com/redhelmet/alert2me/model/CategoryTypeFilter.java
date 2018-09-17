package com.redhelmet.alert2me.model;

import java.io.Serializable;
import java.util.List;

public class CategoryTypeFilter implements Serializable {
    private String code;
    private List<String> status;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getStatus() {
        return this.status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }
}