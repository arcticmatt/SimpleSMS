package com.jm2.simplesms;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /*---- General Noti ----*/
    public static String KEY_ROWID;
    public static String KEY_MESSAGE;
    public static String KEY_SNOOZE_UNIQUE_ID;
    public static String KEY_IS_PINNED;
    public static String KEY_PHONE_NUMBER;
    public static String KEY_SEND_NOTI;

    /*---- Queue Noti ----*/
    public static String KEY_IS_QUEUED_NOTI;

    /*---- Bundles ----*/
    public static String KEY_QUEUE_BUNDLE;
    public static String KEY_ALARM_BUNDLE;
    public static String KEY_VIBRATE_BUNDLE;

    /*---- Intents ----*/
    public static String KEY_VIBRATE_INTENT;
    public static String KEY_ALARM_INTENT;

    private static final String KEY_QUEUE_SMS_ON_OFF = "pref_key_queue_sms_on_off";
    private static final String KEY_START_NOTI_ON_OFF = "pref_start_noti_on_off_key";
    private static final String KEY_QUEUE_NOTI_ON_OFF = "pref_key_queue_noti_on_off";
    private static final String KEY_KEYBOARD_ON_OFF = "pref_key_keyboard_on_off";
    private static final String KEY_SEND_NOTI_ON_OFF = "pref_key_send_noti_on_off";
    private static final String KEY_THEMES_ACTIVITY = "pref_key_themes_activities";
    private static final String KEY_THEMES_SMS_LIST = "pref_key_themes_sms_list";

    CustomAutoComplete txtPhoneNo;
    EditText txtMessage;

    SharedPreferences sharedPrefs;

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter contactsAdapter;

    // Bundle used for notis
    Bundle notiBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = this.getResources();

        /*---- General Noti ----*/
        KEY_ROWID = res.getString(R.string.KEY_ROWID);
        KEY_MESSAGE = res.getString(R.string.KEY_MESSAGE);
        KEY_SNOOZE_UNIQUE_ID = res.getString(R.string.KEY_SNOOZE_UNIQUE_ID);
        KEY_IS_PINNED = res.getString(R.string.KEY_IS_PINNED);
        KEY_PHONE_NUMBER = res.getString(R.string.KEY_PHONE_NUMBER);
        KEY_SEND_NOTI = res.getString(R.string.KEY_SEND_NOTI);

        /*---- Queue Noti ----*/
        KEY_IS_QUEUED_NOTI = res.getString(R.string.KEY_IS_QUEUED_NOTI);

        /*---- Bundles ----*/
        KEY_QUEUE_BUNDLE = res.getString(R.string.KEY_QUEUE_BUNDLE);
        KEY_ALARM_BUNDLE = res.getString(R.string.KEY_ALARM_BUNDLE);
        KEY_VIBRATE_BUNDLE = res.getString(R.string.KEY_VIBRATE_BUNDLE);

        /*---- Intents ----*/
        KEY_VIBRATE_INTENT = res.getString(R.string.KEY_VIBRATE_INTENT);
        KEY_ALARM_INTENT = res.getString(R.string.KEY_ALARM_INTENT);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this); // why is it default here, but not in the userpreferences fragment?

        /*** Show one time dialog after install ***/
        final String welcomeDialogShownPref = "showWelcomeDialog";
        Boolean welcomeDialogShown = sharedPrefs.getBoolean(welcomeDialogShownPref, false);

        if (!welcomeDialogShown) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(welcomeDialogShownPref, true);
            editor.commit();

            Intent toWelcome = new Intent(this, WelcomeDialogActivity.class);
            startActivity(toWelcome);
        }

        // Themes
        boolean dark = false;
        if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {

            if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "1").equals("1")) {
                setTheme(R.style.AppTheme);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "2").equals("2")) {
                setTheme(R.style.AppThemeRed);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "3").equals("3")) {
                setTheme(R.style.AppThemeOrange);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "4").equals("4")) {
                setTheme(R.style.AppThemeYellow);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "5").equals("5")) {
                setTheme(R.style.AppThemeGreen);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "6").equals("6")) {
                setTheme(R.style.AppThemeBlue);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "7").equals("7")) {
                setTheme(R.style.AppThemePurple);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("8")) {
                setTheme(R.style.AppThemeTurquoise);
            }

            // If light theme, manually set background color. Otherwise, it will
            // be an off white color, which looks weird against the white of the SMS list
            View view = this.getWindow().getDecorView();
            int color = res.getColor(R.color.white);
            view.setBackgroundColor(color);
        } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
            dark = true;

            if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "1").equals("1")) {
                setTheme(R.style.DarkTheme);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "2").equals("2")) {
                setTheme(R.style.DarkThemeRed);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "3").equals("3")) {
                setTheme(R.style.DarkThemeOrange);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "4").equals("4")) {
                setTheme(R.style.DarkThemeYellow);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "5").equals("5")) {
                setTheme(R.style.DarkThemeGreen);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "6").equals("6")) {
                setTheme(R.style.DarkThemeBlue);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "7").equals("7")) {
                setTheme(R.style.DarkThemePurple);
            } else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("8")) {
                setTheme(R.style.DarkThemeTurquoise);
            }

        } else {
            setTheme(R.style.AppTheme);
            View view = this.getWindow().getDecorView();
            int color = res.getColor(R.color.white);
            view.setBackgroundColor(color);
        }
        setContentView(R.layout.activity_main);

        // If dark theme, make the settings button a lighter drawable
        if (dark) {
            ImageButton prefsButton = (ImageButton) findViewById(R.id.prefsButton);
            prefsButton.setBackgroundResource(R.drawable.ic_settings_light);
        }

        // Initialize views
        txtPhoneNo = (CustomAutoComplete) findViewById(R.id.chooseContact);
        txtMessage = (EditText) findViewById(R.id.message);

        // Initialize noti bundle
        notiBundle = new Bundle();



        // Create an empty adapter we will use to display the loaded contact data
        contactsAdapter = new SimpleCursorAdapter(this,
                R.layout.contact_auto_complete, null,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone._ID},
                new int[]{R.id.name, R.id.number, R.id.id}, 0);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(1, null, this);

        // If pref allows it, create starting noti
        if (sharedPrefs.getBoolean(KEY_START_NOTI_ON_OFF, true)) {
            StartingNotiManager startingnoti = new StartingNotiManager(MainActivity.this);
            if(sharedPrefs.getString("pref_start_noti_settings", "Pinned").equals(getString(R.string.pref_start_noti_clearable))){
                startingnoti.createClearableStartingNoti();
            } else{
                startingnoti.createStartingNoti();
            }
        }
    }

    // Sends an SMS message to another device
    public void sendSms(View view) {
        int uniqueId = (int) System.currentTimeMillis();
        // Get the message and phoneNo from the edit text
        // views
        // Prevent NPE
        CharSequence charMessage = txtMessage.getText();
        String message = "";
        if (charMessage != null && charMessage.length() > 0) {
            message = charMessage.toString();
        }
        // Prevent NPE
        CharSequence charPhoneNo = txtPhoneNo.getText();
        String phoneNo = "";
        if (charPhoneNo != null && charPhoneNo.length() > 0) {
            phoneNo = charPhoneNo.toString();
        }

        // Make smsBundle and put key extras into it
        Bundle smsBundle = new Bundle();
        smsBundle.putString(KEY_MESSAGE, message);
        smsBundle.putString(KEY_PHONE_NUMBER, phoneNo);
        smsBundle.putInt(KEY_ROWID, uniqueId);

        // If the preference for queue sms is on,
        // start the queue sms dialog activity
        if (sharedPrefs.getBoolean(KEY_QUEUE_SMS_ON_OFF, false)) {
            Intent toQueueSmsDialog = new Intent(this, QueueSmsDialogActivity.class);
            toQueueSmsDialog.putExtra(KEY_QUEUE_BUNDLE, smsBundle);
            startActivity(toQueueSmsDialog);

            // hide keyboard
            hideKeyboard(txtMessage, R.id.activity_main_parent_view);
        }
        // Else, just send the sms
        else {
            MySmsManager mySmsManager = new MySmsManager(this, smsBundle);
            if (mySmsManager.checkSmsTextFields()) {
                mySmsManager.sendSms();

                // Clear the text fields
                txtMessage.setText(R.string.blank_string);
                txtPhoneNo.setText(R.string.blank_string);

                // hide keyboard
                hideKeyboard(txtMessage, R.id.activity_main_parent_view);
            }
        }

    }

    // If the new intent is a vibrate/alarmIntent,
    // set myBundle to the bundle extra in that intent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        /* If the new intent holds the correct boolean value,
        set myBundle to the bundle extra in the intent. This
        makes it so that myBundle is always up to date, and
        prevents us from having to manually add extras to the bundle*/
        if (intent.getBooleanExtra(KEY_VIBRATE_INTENT, false)) {
            notiBundle = intent.getBundleExtra(KEY_VIBRATE_BUNDLE);
        }
        if (intent.getBooleanExtra(KEY_ALARM_INTENT, false)) {
            notiBundle = intent.getBundleExtra(KEY_ALARM_BUNDLE);
        }
    }



    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(this, baseUri,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone._ID},
                select, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        contactsAdapter.swapCursor(data);

        contactsAdapter.setStringConversionColumn(data.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

        contactsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                String s = '%' + constraint.toString() + '%';
                return getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? OR " +
                                ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?",
                        new String[]{s, s},
                        null);
            }
        });
        final CustomAutoComplete contactsView =
                (CustomAutoComplete) findViewById(R.id.chooseContact);

        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                TextView nameView = (TextView) arg1.findViewById(R.id.name);
                TextView numberView = (TextView) arg1.findViewById(R.id.number);

                CharSequence name = nameView.getText();
                CharSequence number = numberView.getText();

                contactsView.setText("" + name + "<" + number + ">");

                EditText editText = (EditText) findViewById(R.id.message);
                editText.requestFocus();

            }


        });

        contactsView.setAdapter(contactsAdapter);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        contactsAdapter.swapCursor(null);
    }


    // Method that brings up preferences activity
    public void launchPrefsOnClick(View view) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }



    // keyboard will be hidden every time activity resumes
    // and focus will be put on whole linear layout (if that pref is checked)
    @Override
    protected void onResume() {
        super.onResume();
        Intent dialogIntent = getIntent();
        // If the intent that resumed this activity
        // has the extra "clear_text", clear both text fields
        if (dialogIntent.getBooleanExtra("clear_text", false)) {
            txtMessage.setText(R.string.blank_string);
            txtPhoneNo.setText(R.string.blank_string);
        }
        // If the preference for auto keyboard is on, automatically
        // show the keyboard when activity resumes
        if (sharedPrefs.getBoolean(KEY_KEYBOARD_ON_OFF, false)) {
            showKeyboard();
        }
        // Else, hide the keyboard
        else {
            hideKeyboard(txtMessage, R.id.activity_main_parent_view);
        }

    }

    // Method that hides the keyboard and puts focus
    // on whole linear layout. Use various methods to close
    // the keyboard because they are finnicky and don't always work
    public void hideKeyboard(View view, int id) {
        // if the keyboard was brought up by clicking an item in
        // SMS list, this will hide it
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(txtMessage.getWindowToken(), 0);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // brings focus away from any edit text object (usually
        // hides keyboard as well)
        findViewById(id).requestFocus();
    }

    // Method that shows keyboard and puts
    // focus on the message edit text field.
    public void showKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);

        // Set an listener that, when the txtmessage edit text view has focus,
        // brings up the keyboard
        txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // make sure softinputmode is not ALWAYS visible - otherwise, even
                    // when the checkbox for auto keyboard pop up is off, the keyboard
                    // will still sometimes pop up
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            }
        });
        // Request focus for this edit text view - should set
        // off the above listener
        txtMessage.requestFocus();
    }

    // Method to test buttons
    public void test(View view) {
        DBAdapter myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        myDBAdapter.remakeOnBoot();
        myDBAdapter.close();

    }


}

