package com.redhelmet.alert2me.util;

import androidx.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.SnackbarMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A single source of Snackbar messages related to reservations.
 * <p>
 * Only shows one Snackbar related to one change across all screens
 * <p>
 * Emits new values on request (when a Snackbar is dismissed and ready to show a new message)
 * <p>
 * It keeps a list of [MAX_ITEMS] items, enough to figure out if a message has already been shown,
 * but limited to avoid wasting resources.
 */
public class SnackbarMessageManager {
    public final int MAX_ITEMS = 10;
    private PreferenceStorage preferenceStorage;

    private List<Event<SnackbarMessage>> messages = new ArrayList<>();

    private MutableLiveData<Event<SnackbarMessage>> result = new MutableLiveData<>();

    public SnackbarMessageManager(PreferenceStorage pref) {
        this.preferenceStorage = pref;
    }

    public void addMessage(SnackbarMessage msg) {
//        if (isSnackbarShouldBeIgnored(msg)) {
//            return
//        }
        // If the new message is about the same change as a pending one, keep the new one. (rare)
        List<Event<SnackbarMessage>> sameRequestId = new ArrayList<>();
        List<Event<SnackbarMessage>> alreadyHandledWithSameId = new ArrayList<>();
        for (Event<SnackbarMessage> event : messages) {
            if (event.peekContent().requestChangeId.equals(msg.requestChangeId)) {
                if (event.isHasBeenHandled()) sameRequestId.add(event);
                else alreadyHandledWithSameId.add(event);
            }
        }
        if (!sameRequestId.isEmpty()) {
            messages.removeAll(sameRequestId);
        }
        if (alreadyHandledWithSameId.isEmpty()) {
            messages.add(new Event<>(msg));
            loadNextMessage();
        }

        // Remove old messages
        if (messages.size() > MAX_ITEMS) {
            List<Event<SnackbarMessage>> oldMessages = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                if (i >= MAX_ITEMS) {
                    oldMessages.add(messages.get(i));
                }
            }
            messages.removeAll(oldMessages);
        }
    }

    public void loadNextMessage() {
        for (Event<SnackbarMessage> event : messages) {
            if (!event.isHasBeenHandled()) {
                result.postValue(event);
                return;
            }
        }
    }

    public MutableLiveData<Event<SnackbarMessage>> observeNextMessage() {
        return result;
    }

//    private fun isSnackbarShouldBeIgnored(msg: SnackbarMessage): Boolean {
//        return preferenceStorage.observableSnackbarIsStopped.value == true &&
//                msg.actionId == R.string.dont_show
//    }
}
