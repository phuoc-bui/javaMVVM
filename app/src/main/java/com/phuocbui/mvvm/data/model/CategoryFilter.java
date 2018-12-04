package com.phuocbui.mvvm.data.model;

import java.io.Serializable;
import java.util.List;

public class CategoryFilter  implements Serializable {
    private List<CategoryTypeFilter> types;

    public List<CategoryTypeFilter> getTypes() {
        return this.types;
    }

    public void setTypes(List<CategoryTypeFilter> types) {
        this.types = types;
    }


}
