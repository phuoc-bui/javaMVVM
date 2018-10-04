package com.redhelmet.alert2me.ui.hint;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;

import java.util.List;

public class HintViewModel extends BaseViewModel {
    private List<Hint> hints;

    public HintViewModel(DataManager dataManager) {
        super(dataManager);
        hints = dataManager.getHintData();
    }

    public List<Hint> getHints() {
        return hints;
    }

    public void onLastPageScrolled(boolean fromHelp) {
        dataManager.setInitialLaunch(true);
        if (fromHelp) {
            navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.FINISH)));
        } else {
            navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, TermConditionActivity.class)));
        }
    }
}
