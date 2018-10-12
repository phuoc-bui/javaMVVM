package com.redhelmet.alert2me.ui;

public class SnackbarMessage {
    /** Resource string ID of the message to show */
    public int messageId;

    /** Optional resource string ID for the action (example: "Got it!") */
    public int actionId;

    /** Set to true for a Snackbar with long duration  */
    public boolean longDuration;

    /** Optional change ID to avoid repetition of messages */
    public String requestChangeId;
}
