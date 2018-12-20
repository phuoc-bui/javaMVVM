package com.phuocbui.basemodule.ui.dialog;

import android.os.Bundle;
import android.view.View;

import com.phuocbui.basemodule.R;
import com.phuocbui.basemodule.databinding.DialogGeneralAlertBinding;
import com.phuocbui.basemodule.ui.base.BaseDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.FragmentManager;

public class GeneralAlertDialog extends BaseDialogFragment<DialogGeneralAlertBinding> {

    public ObservableInt title = new ObservableInt();
    public ObservableInt message = new ObservableInt();
    public ObservableInt negativeText = new ObservableInt(R.string.cancel);
    public ObservableInt positiveText = new ObservableInt();

    public GeneralAlertDialog() {
    }

    public static GeneralAlertDialog newInstance(@StringRes int title, @StringRes int message, @StringRes int negativeText, @StringRes int positiveText) {

        Bundle args = new Bundle();

        GeneralAlertDialog fragment = new GeneralAlertDialog();
        args.putInt("title", title);
        args.putInt("message", message);
        args.putInt("negative", negativeText);
        args.putInt("positive", positiveText);
        fragment.setArguments(args);
        return fragment;
    }

    public static GeneralAlertDialog newInstance(@StringRes int title, @StringRes int message, @StringRes int positiveText) {

        Bundle args = new Bundle();

        GeneralAlertDialog fragment = new GeneralAlertDialog();
        args.putInt("title", title);
        args.putInt("message", message);
        args.putInt("negative", -1);
        args.putInt("positive", positiveText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_general_alert;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            title.set(getArguments().getInt("title"));
            message.set(getArguments().getInt("message"));
            positiveText.set(getArguments().getInt("positive"));

            int negativeId = getArguments().getInt("negative");
            if (negativeId < 0) binder.btCancel.setVisibility(View.GONE);
            else {
                binder.btCancel.setText(negativeId);
                binder.btCancel.setOnClickListener(v -> dismiss());
            }
        }
    }

    public static void showAlertDialog(FragmentManager manager, @StringRes int title, @StringRes int message, @StringRes int negativeText, @StringRes int positiveText, View.OnClickListener positiveClick) {
        GeneralAlertDialog dialog = GeneralAlertDialog.newInstance(title, message, negativeText, positiveText);
        dialog.show(manager, "evaAlertDialog", positiveClick);
    }

    public static void showConfirmDialog(FragmentManager manager, @StringRes int title, @StringRes int message, @StringRes int positiveText, View.OnClickListener positiveClick) {
        GeneralAlertDialog dialog = GeneralAlertDialog.newInstance(title, message, positiveText);
        dialog.show(manager, "evaAlertDialog", positiveClick);
    }

    public static void showConfirmDialog(FragmentManager manager, @StringRes int title, @StringRes int message, @StringRes int positiveText) {
        GeneralAlertDialog dialog = GeneralAlertDialog.newInstance(title, message, positiveText);
        dialog.show(manager, "evaAlertDialog");
    }
}
