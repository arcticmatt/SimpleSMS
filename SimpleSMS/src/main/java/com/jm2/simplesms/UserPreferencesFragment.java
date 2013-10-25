package com.jm2.simplesms;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;

public class UserPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_START_NOTI_ON_OFF = "pref_start_noti_on_off_key";
    private static final String KEY_START_NOTI_BEHAVIOR = "pref_start_noti_settings";
    private static final String KEY_QUEUE_SMS_ON_OFF = "pref_key_queue_sms_on_off";
    private static final String KEY_QUEUE_NOTI_ON_OFF = "pref_key_queue_noti_on_off";
    private static final String KEY_KEYBOARD_ON_OFF = "pref_key_keyboard_on_off";
    private static final String KEY_VOLUME_CRESCENDO_ON_OFF = "pref_key_volume_crescendo_on_off";
    private static final String KEY_ALARM_SOUND_TYPE = "pref_key_alarm_sound_type";
    private static final String KEY_SOUND_PICKER = "pref_key_sound_picker";
    private static final String KEY_SEND_NOTI_ON_OFF = "pref_key_send_noti_on_off";
    CheckBoxPreference startNotiCheckBox;
    CheckBoxPreference queueSmsCheckbox;
    CheckBoxPreference queueNotiCheckbox;
    CheckBoxPreference keyboardCheckbox;
    CheckBoxPreference volumeCrescendoCheckbox;
    CheckBoxPreference sendNotiCheckbox;
    PreferenceManager prefsManager;
    SharedPreferences sharedPrefs;
    SimpleSMSApp mSimpleSMSApp;
    SMSCursorAdapter messageBoxAdapter;
    ListPreference startNotiBehavior;
    Preference soundPicker;
    StartingNotiManager startingNotiManager;


    RingtoneManager mRingtoneManager;
    Uri soundUri;
    String soundName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mSimpleSMSApp = (SimpleSMSApp) getActivity().getApplication();

        startingNotiManager = mSimpleSMSApp.getStartingNotiManager();

        prefsManager = getPreferenceManager();
        sharedPrefs = prefsManager.getSharedPreferences();

        startNotiCheckBox = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_START_NOTI_ON_OFF);
        startNotiCheckBox.setChecked(sharedPrefs.getBoolean(KEY_START_NOTI_ON_OFF, true));
        startNotiCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(newValue.toString().equals("false")){
                    startingNotiManager.cancelStartingNoti();

                } else{
                    startingNotiManager.createStartingNoti();
                }

                return true;
            }
        });

        startNotiBehavior = (ListPreference) getPreferenceScreen().findPreference(KEY_START_NOTI_BEHAVIOR);



        queueSmsCheckbox = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_QUEUE_SMS_ON_OFF);
        queueSmsCheckbox.setChecked(sharedPrefs.getBoolean(KEY_QUEUE_SMS_ON_OFF, false));

        keyboardCheckbox = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_KEYBOARD_ON_OFF);
        keyboardCheckbox.setChecked(sharedPrefs.getBoolean(KEY_KEYBOARD_ON_OFF, false));


        messageBoxAdapter = mSimpleSMSApp.getMessageBoxAdapter();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefsManager != null && prefsManager.getSharedPreferences() != null) {
            prefsManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onPause() {
        if (prefsManager != null && prefsManager.getSharedPreferences() != null) {
            prefsManager.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("pref_list_text_size")){
            messageBoxAdapter.notifyDataSetChanged();
        }
        if(key.equals("pref_start_noti_settings")){

            if(sharedPrefs.getString("pref_start_noti_settings", "Pinned").equals(getString(R.string.pref_start_noti_clearable))){
                startingNotiManager.cancelStartingNoti();
                startingNotiManager.createClearableStartingNoti();
                //startNotiBehavior.setSummary("dummy");
                startNotiBehavior.setSummary(getString(R.string.pref_start_noti_settings_summ_clearable));
            } else{
                startingNotiManager.cancelStartingNoti();
                startingNotiManager.createStartingNoti();
                //startNotiBehavior.setSummary("dummy");
                startNotiBehavior.setSummary(getString(R.string.pref_start_noti_settings_summ_pinned));
            }
        }

        if (key.equals("pref_list_sms_count")){
            messageBoxAdapter.notifyDataSetChanged();
        }
    }
}
