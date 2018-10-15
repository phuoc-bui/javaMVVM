package com.redhelmet.alert2me.ui.hint;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;

import java.util.List;

import javax.inject.Inject;

public class HintViewModel extends BaseViewModel {
    private DataManager dataManager;
    private List<Hint> hints;

    @Inject
    public HintViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
        hints = dataManager.getHintData();
    }

    public List<Hint> getHints() {
        return hints;
    }

    public void onLastPageScrolled(boolean fromHelp) {
        dataManager.setInitialLaunch(true);
        if (fromHelp) {
            navigateTo(new NavigationItem(NavigationItem.FINISH));
        } else {
            navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, TermConditionActivity.class));
        }
    }
}
