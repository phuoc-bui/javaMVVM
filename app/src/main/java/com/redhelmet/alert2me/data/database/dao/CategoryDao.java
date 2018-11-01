package com.redhelmet.alert2me.data.database.dao;

import com.redhelmet.alert2me.data.model.Category;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM Category")
    Single<List<Category>> getCategories();

    @Query("SELECT * FROM Category WHERE category = :category")
    Single<Category> getEventCategory(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveCategories(List<Category> categories);

    @Query("DELETE FROM Category")
    int nukeTable();

    @Update
    Completable updateCategories(List<Category> categories);

    @Query("SELECT * FROM Category WHERE userEdited = 1")
    Single<List<Category>> getFilterOnCategories();
}
