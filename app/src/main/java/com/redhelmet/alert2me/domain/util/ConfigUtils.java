package com.redhelmet.alert2me.domain.util;

public class ConfigUtils {
//
//    private EventConfig eventConfig;
//
//    public ConfigUtils(EventConfig eventConfig) {
//        this.eventConfig = eventConfig;
//    }
//
//    public String getPrimaryColor(final String category, final String status) {
//
//        if (category == null)
//            return "";
//        if (status == null)
//            return "";
//        Category selectedCategory = CollectionUtils.find(eventConfig.getCategories(), new Predicate<Category>() {
//            @Override
//            public boolean evaluate(Category object) {
//                return category.equals(object.getCategory());
//            }
//        });
//        if (selectedCategory == null) return "#FFFFFF";
//        List<CategoryStatus> statuses = selectedCategory.getStatuses();
//        CategoryStatus categoryStatus = CollectionUtils.find(statuses, new Predicate<CategoryStatus>() {
//            @Override
//            public boolean evaluate(CategoryStatus object) {
//
//                return status.equals(object.getName());
//            }
//        });
//        if (categoryStatus == null) return "#FFFFFF";
//        return categoryStatus.getPrimaryColor();
//    }
//
//    public String getSecondaryColor(final String category, final String status) {
//
//        Category selectedCategory = CollectionUtils.find(eventConfig.getCategories(), new Predicate<Category>() {
//            @Override
//            public boolean evaluate(Category object) {
//                return category.equals(object.getCategory());
//            }
//        });
//        if (selectedCategory == null) return "#FFFFFF";
//        List<CategoryStatus> statuses = selectedCategory.getStatuses();
//        CategoryStatus categoryStatus = CollectionUtils.find(statuses, new Predicate<CategoryStatus>() {
//            @Override
//            public boolean evaluate(CategoryStatus object) {
//
//                return status.equals(object.getName());
//            }
//        });
//        if (categoryStatus == null) return "#FFFFFF";
//        return categoryStatus.getSecondaryColor();
//    }
//    public String getTextColor(final String category, final String status) {
//
//        if (category == null)
//            return "";
//        if (status == null)
//            return "";
//        Category selectedCategory = CollectionUtils.find(eventConfig.getCategories(), new Predicate<Category>() {
//            @Override
//            public boolean evaluate(Category object) {
//                return category.equals(object.getCategory());
//            }
//        });
//        if (selectedCategory == null) return "#000000";
//        List<CategoryStatus> statuses = selectedCategory.getStatuses();
//        CategoryStatus categoryStatus = CollectionUtils.find(statuses, new Predicate<CategoryStatus>() {
//            @Override
//            public boolean evaluate(CategoryStatus object) {
//
//                return status.equals(object.getName());
//            }
//        });
//        if (categoryStatus == null) return "#000000";
//        return categoryStatus.getTextColor();
//    }
}
