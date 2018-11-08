package com.redhelmet.alert2me.ui.watchzone;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentMobileWatchZoneBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class MobileWatchZoneFragment extends BaseFragment<WatchZoneViewModel, FragmentMobileWatchZoneBinding> {
    @Inject
    ViewModelProvider.Factory factory;

    private RingtonePickerDialog.Builder ringtonePickerBuilder;
    private String ringtoneUri = null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mobile_watch_zone;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, WatchZoneViewModel.class);

        ringtonePickerBuilder = new RingtonePickerDialog.Builder(getBaseActivity(), getChildFragmentManager());
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn());
        ringtonePickerBuilder.setListener( (ringtoneName, ringtoneUri) -> {
            if (ringtoneUri != null) {
                this.ringtoneUri = ringtoneUri.toString();
                viewModel.setRingSound(this.ringtoneUri);
            }
        });

        if (ringtoneUri == null || ringtoneUri.isEmpty()) {
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        }

        viewModel.setRingSound(ringtoneUri);

        binder.tvNotificationSoundTitle.setOnClickListener(v -> ringtonePickerBuilder.show());
        binder.tvNotificationSoundValue.setOnClickListener(v -> ringtonePickerBuilder.show());
    }

    private boolean checkVibrationIsOn() {
        boolean status = false;
        AudioManager am = (AudioManager) getBaseActivity().getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                status = true;
                break;
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                break;
        }

        return status;
    }
}
