package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import androidx.lifecycle.MutableLiveData;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;

import com.redhelmet.alert2me.data.model.EventGroup;

public class EventGroupItemViewModel {

    public MutableLiveData<EventGroup> eventGroup = new MutableLiveData<>();
    private DefaultFilterViewModel.OnSwitchChangedListener listener;

    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();

    public ObservableBoolean showToggleSwitch = new ObservableBoolean(false);
    public ObservableBoolean showToggleAlways = new ObservableBoolean(false);
    public ObservableBoolean toggleChecked = new ObservableBoolean(false);

    public EventGroupItemViewModel(EventGroup eventGroup, DefaultFilterViewModel.OnSwitchChangedListener listener) {
        this.eventGroup.setValue(eventGroup);
        this.listener = listener;
        title.set(eventGroup.getName());
        description.set(eventGroup.getDescription());
        showToggleSwitch.set(eventGroup.isFilterToggle());
        showToggleAlways.set(!eventGroup.isFilterToggle());
        toggleChecked.set(eventGroup.isFilterOn());
    }

    public void onToggleSwitchClick(View v) {
        eventGroup.getValue().setFilterOn(((SwitchCompat) v).isChecked());
        eventGroup.getValue().setUserEdited(true);
        if (listener != null) listener.onSwitchChanged((SwitchCompat) v, eventGroup.getValue());
    }
}
