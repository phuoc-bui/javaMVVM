package com.redhelmet.alert2me.ui.base;

import com.redhelmet.alert2me.ui.home.HomeActivity;

/**
 * Fragments representing main navigation destinations (shown by {@link HomeActivity}) implement this
 * interface.
 */
public interface NavigationFragment {
    /**
     * Called by the hosting activity when the Back button is pressed.
     * @return True if the fragment handled the back press, false otherwise.
     */
    boolean onBackPressed();

    /** Called by the hosting activity when the user interacts with it. */
    void onUserInteraction();
}
