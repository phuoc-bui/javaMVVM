package com.redhelmet.alert2me.ui.addwatchzone;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentEditStaticZoneNameBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class EditStaticZoneNameFragment extends BaseFragment<AddStaticZoneViewModel, FragmentEditStaticZoneNameBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    private RingtonePickerDialog.Builder ringtonePickerBuilder;
    private Uri ringtoneURI = null;
    private String ringtoneName = null;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_static_zone_name;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, AddStaticZoneViewModel.class);

        ringtonePickerBuilder = new RingtonePickerDialog.Builder(getBaseActivity(), getChildFragmentManager());
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn());
        ringtonePickerBuilder.setListener((RingtonePickerListener) (ringtoneName, ringtoneUri) -> {
            this.ringtoneName = ringtoneName;
            ringtoneURI = ringtoneUri;

//            notification_sound_text.setText(ringtoneName);
        });

        if (ringtoneURI == null) {
            ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(getBaseActivity(), ringtoneURI);
        ringtoneName = ringtone.getTitle(getBaseActivity());
        binder.tvNotificationSoundValue.setText(ringtoneName);
//        select_ringtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ringtonePickerBuilder.show();
//            }
//        });
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
