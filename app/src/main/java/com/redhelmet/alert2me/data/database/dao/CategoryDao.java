package com.redhelmet.alert2me.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.redhelmet.alert2me.data.model.Category;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM Category")
    Single<List<Category>> getCategories();

    @Query("SELECT * FROM Category WHERE category = :category")
    Single<Category> getEventCategory(String category);

    @Query("SELECT * FROM Category")
    List<Category> getCategoriesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCategories(List<Category> categories);

    @Query("DELETE FROM Category")
    void nukeTable();

    @Update
    void updateCategories(List<Category> categories);

    @Query("SELECT * FROM Category WHERE id IN (:ids)")
    Single<List<Category>> getCategoriesWithIds(List<Long> ids);

    @Query("SELECT * FROM Category WHERE userEdited = 1")
    Single<List<Category>> getFilterOnCategories();
}
