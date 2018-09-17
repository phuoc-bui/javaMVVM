package com.redhelmet.alert2me.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {


    private static Category instance;
    public static ArrayList<Category> category_data;
    private String category;
    private String nameLabel;
    private boolean displayOnly;
    private String filterDescription;
    private String filterOrder;
    private List<CategoryType> types;


    public Category() {
    }

    public static Category getInstance() {

        if (instance == null)
            instance = new Category();
        return instance;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(String nameLabel) {
        this.nameLabel = nameLabel;
    }

    public boolean isDisplayOnly() {
        return displayOnly;
    }

    public void setDisplayOnly(boolean displayOnly) {
        this.displayOnly = displayOnly;
    }

    public String getFilterDescription() {
        return filterDescription;
    }

    public void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription;
    }

    public String getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(String filterOrder) {
        this.filterOrder = filterOrder;
    }


    public List<CategoryType> getTypes() {
        return types;
    }

    public void setTypes(List<CategoryType> types) {
        this.types = types;
    }


    public void setCategoryArray(ArrayList<Category> category_data) {

        this.category_data = category_data;
    }

    public ArrayList<Category> getCategoryArray() {

        return this.category_data;
    }


}
