package com.phuocbui.mvvm.data.database.dao;

import com.phuocbui.mvvm.data.model.Category;

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable saveCategories(List<Category> categories);

    @Query("DELETE FROM Category")
    int nukeTable();

    @Update
    Completable updateCategories(List<Category> categories);

    @Query("SELECT * FROM Category WHERE userEdited = 1")
    Single<List<Category>> getFilterOnCategories();
}
