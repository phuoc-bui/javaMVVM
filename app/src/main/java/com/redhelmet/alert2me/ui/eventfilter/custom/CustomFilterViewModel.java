package com.redhelmet.alert2me.ui.eventfilter.custom;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CustomFilterViewModel extends BaseViewModel {
    public MutableLiveData<List<Category>> allCategories = new MutableLiveData<>();

    public CustomFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> allCategories.setValue(list)));
    }

    public void saveData() {
        saveDataToServer();
    }

    public void updateCategory(Category category, int index) {
        List<Category> categories = allCategories.getValue();
        categories.set(index, category);
        allCategories.setValue(categories);
    }

    private void saveDataToServer() {
        List<Category> list = allCategories.getValue();
        if (list == null) return;

        List<Category> editedCat = new ArrayList<>();

        for (Category category : list) {
            if (category.isUserEdited()) {
                editedCat.add(category);
            }
        }
        disposeBag.add(Completable.fromAction(() -> dataManager.saveUserCustomFilters(editedCat))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Intent resultIntent = new Intent();
                    navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.FINISH_AND_RETURN, resultIntent)));
                }));

    }
}
