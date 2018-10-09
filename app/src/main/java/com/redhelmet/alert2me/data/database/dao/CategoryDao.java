package com.redhelmet.alert2me.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.redhelmet.alert2me.data.model.Category;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM Category")
    Single<List<Category>> getCategories();

    @Query("SELECT * FROM Category")
    List<Category> getCategoriesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCategories(List<Category> categories);

    @Update
    void updateCategories(List<Category> categories);

    @Query("SELECT * FROM Category WHERE id IN (:ids)")
    Single<List<Category>> getCategoriesWithIds(List<Long> ids);

    @Query("SELECT * FROM Category WHERE userEdited = 1")
    Single<List<Category>> getFilterOnCategories();
}
