package com.redhelmet.alert2me.util;

import android.arch.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.data.PreferenceHelper;
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
    private PreferenceHelper preferenceHelper;

    private List<Event<SnackbarMessage>> messages = new ArrayList<>();

    private MutableLiveData<Event<SnackbarMessage>> result = new MutableLiveData<>();

    public SnackbarMessageManager(PreferenceHelper pref) {
        this.preferenceHelper = pref;
    }

    public void addMessage(SnackbarMessage msg) {
//        if (isSnackbarShouldBeIgnored(msg)) {
//            return
//        }
        // If the new message is about the same change as a pending one, keep the new one. (rare)
        val sameRequestId = messages.filter {
            it.peekContent().requestChangeId == msg.requestChangeId && !it.hasBeenHandled
        }
        if (sameRequestId.isNotEmpty()) {
            messages.removeAll(sameRequestId)
        }

        // If the new message is about a change that was already notified, ignore it.
        val alreadyHandledWithSameId = messages.filter {
            it.peekContent().requestChangeId == msg.requestChangeId && it.hasBeenHandled
        }

        // Only add the message if it hasn't been handled before
        if (alreadyHandledWithSameId.isEmpty()) {
            messages.add(Event(msg))
            loadNextMessage()
        }

        // Remove old messages
        if (messages.size > MAX_ITEMS) {
            messages.retainAll(messages.drop(messages.size - MAX_ITEMS))
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
