package com.phuocbui.mvvm.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.phuocbui.mvvm.data.model.base.UserModel;

import java.util.List;

@Entity(tableName = "Category")
public class Category extends UserModel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String category;
    private String nameLabel;
    private boolean displayOnly;
    private String filterDescription;
    private String filterOrder;
    private List<CategoryType> types;
    private List<CategoryStatus> statuses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<CategoryStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<CategoryStatus> statuses) {
        this.statuses = statuses;
    }
}
