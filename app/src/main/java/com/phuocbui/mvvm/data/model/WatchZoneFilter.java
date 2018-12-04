package com.phuocbui.mvvm.data.model;

import com.phuocbui.mvvm.data.model.base.Model;

import java.util.List;

public class WatchZoneFilter implements Model {

    private List<Type> warning;
    private List<Type> incident;
    private List<Type> restriction;
    private List<Type> support_service;

    public List<Type> getWarning() {
        return warning;
    }

    public void setWarning(List<Type> warning) {
        this.warning = warning;
    }

    public List<Type> getIncident() {
        return incident;
    }

    public void setIncident(List<Type> incident) {
        this.incident = incident;
    }

    public List<Type> getRestriction() {
        return restriction;
    }

    public void setRestriction(List<Type> restriction) {
        this.restriction = restriction;
    }

    public List<Type> getSupport_service() {
        return support_service;
    }

    public void setSupport_service(List<Type> support_service) {
        this.support_service = support_service;
    }

    public static class Type {
        private String code;
        private String[] status;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String[] getStatus() {
            return status;
        }

        public void setStatus(String[] status) {
            this.status = status;
        }
    }
}
