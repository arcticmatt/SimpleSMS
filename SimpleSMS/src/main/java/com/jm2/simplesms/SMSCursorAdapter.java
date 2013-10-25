package com.jm2.simplesms;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SMSCursorAdapter extends CursorAdapter {

    private static final String UNKNOWN_BODY_ERROR = "Unknown Body Error";
    private static final String TAG = "SMSCursorAdapter";
    private static final String KEY_THEMES_SMS_LIST = "pref_key_themes_sms_list";
    private static final String KEY_THEMES_ACTIVITY = "pref_key_themes_activities";

    Context context;
    Cursor cursor;
    SharedPreferences sharedPrefs;

    int colorSentBox;
    int colorReceivedBox;
    int colorSentText;
    int colorReceivedText;

    String[][] contactInfo = new String[2][512];

    private static class ViewHolder {
        int textSize;
        TextView addressView;
        TextView bodyView;
    }

    public SMSCursorAdapter(Context context, Cursor c) {
        // Since we never use this constructor with a non-null
        // cursor, the deprecated constructor does no harm
        super(context, c);
        this.context = context;
        cursor = c;

        Resources res = context.getResources();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("1")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_gray);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {

                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.gray);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("2")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_red);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.dark_red);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("3")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_orange);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.dark_orange);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("4")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_yellow);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.dark_yellow);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("5")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_green);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.dark_green);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("6")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_blue);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.dark_blue);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("7")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.light_purple);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.dark_purple);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.white);
            }
        }

        else if (sharedPrefs.getString(KEY_THEMES_SMS_LIST, "8").equals("8")) {
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("1")) {
                colorSentBox = res.getColor(R.color.white);
                colorReceivedBox = res.getColor(R.color.turquoise);
                colorSentText = res.getColor(R.color.black);
                colorReceivedText = res.getColor(R.color.black);
            } else if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "2").equals("2")) {
                colorSentBox = res.getColor(R.color.black);
                colorReceivedBox = res.getColor(R.color.turquoise);
                colorSentText = res.getColor(R.color.white);
                colorReceivedText = res.getColor(R.color.black);
            }
        }

        else {
            colorSentBox = res.getColor(R.color.white);
            colorReceivedBox = res.getColor(R.color.light_blue);
            colorSentText = res.getColor(R.color.black);
            colorReceivedText = res.getColor(R.color.black);
        }
    }

    /* This method will return either the count value entered in
    the prefs or, if this value is greater than the amount of
    existing SMS messages, the number of rows of the cursor (the number
    of SMS messages). This will ensure that no cursor out-of-bounds exceptions
    are thrown
     */
    @Override
    public int getCount() {
        int prefCount = Integer.parseInt(sharedPrefs.getString("pref_list_sms_count", "50"));
        Cursor myCursor = getCursor();
        if (myCursor != null) {
            int cursorCount = myCursor.getCount();
            if (prefCount < cursorCount) {
                return prefCount;
            } else {
                return cursorCount;
            }
        }
        return 0;
    }

    /*
    * These methods, newView and bindView define the layouts we will use in the listview
    *
    * newView is called when the listview is first created, so its only called on application onCreate()
    *
    * here in newView, we create a layoutInflater, which basically just takes an xml layout file and converts it to set the views and stuff
    * for EACH ROW of the listview
    *
    * We pass sms_list_item to the inflator, and now each row has that default layout
    * */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.sms_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.textSize = Integer.parseInt(sharedPrefs.getString("pref_list_text_size", "14"));
        holder.addressView = (TextView) view.findViewById(R.id.address);
        holder.bodyView = (TextView) view.findViewById(R.id.body);
        view.setTag(holder);
        return view;
    }


    /*
    * In bindView, we can do some cool stuff to customize each row in the listview
    * For example, here we want to check if the sms for the list are incoming and outgoing, and change
    * that corresponding rows background to orange or white, respectively
    * */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //make sure there is data in the cursor, keeps the runtime errors low
        if (cursor != null) {
            ViewHolder holder = (ViewHolder) view.getTag();

            String addressValue = cursor.getString(cursor.getColumnIndex("address")); //get phone number from sms database at column: address
            /**
             *Insert an integer value for cursor.getColumnIndex("address") instead of recalculating it each time. Also, I do not know if I use the right format for matrices, you should probably check it.
             **/
            int i = 0;
            String addressName = null;
            /**
             * I added a matrix, contactInfo, which will store all the names you get, so that you do not keep cycling through the array of all the names each time
             */
            while (contactInfo[0][i] != null) {
                if (contactInfo[0][i].equals(addressValue)) {
                    addressName = contactInfo[1][i];
                    break;
                }
                i++;
            }
            if (addressName == null) /** If the element is not in contactInfo, it will be added to it**/ {
                addressName = findNameByAddress(context, addressValue);
                if (addressName == null) /** If the number does not have a name, the phone number is used as a name**/
                    addressName = addressValue;
                contactInfo[0][i] = addressValue;
                contactInfo[1][i] = addressName;
            }

            holder.addressView.setText(addressName);
            holder.addressView.setTextSize(holder.textSize);

            String bodyText = cursor.getString(cursor.getColumnIndex("body")); //get body text from the database column: body
            if (bodyText != null) {

                holder.bodyView.setText(bodyText);

            } else {

                holder.bodyView.setText(UNKNOWN_BODY_ERROR); //shows text as an error if no text found

            }
            holder.bodyView.setTextSize(holder.textSize);

            String messageType = cursor.getString(cursor.getColumnIndex("type"));
            /*
            * if sms is a received sms (incoming) then set background to funky orange color
            * */
            if (messageType != null && messageType.equals("1")) {
                view.setBackgroundColor(colorReceivedBox);
                holder.addressView.setGravity(Gravity.LEFT);
                holder.addressView.setTextColor(colorReceivedText);
                holder.bodyView.setGravity(Gravity.LEFT);
                holder.bodyView.setTextColor(colorReceivedText);
            }
            /*
            * else if sms is a sent sms (outgoing) then background is white
            * */
            else if (messageType != null && messageType.equals("2")) {
                view.setBackgroundColor(colorSentBox);
                holder.addressView.setGravity(Gravity.RIGHT);
                holder.addressView.setTextColor(colorSentText);
                holder.bodyView.setGravity(Gravity.RIGHT);
                holder.bodyView.setTextColor(colorSentText);
            }

        }
    }

    private String findNameByAddress(Context context, String addressValue) {
        String name = addressValue;
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME};
        // define the columns I want the query to return
        // encode the phone number and build the filter URI
        if (ContactsContract.PhoneLookup.CONTENT_FILTER_URI != null) {
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(addressValue));
            Cursor contactCursor = context.getContentResolver().query(contactUri, projection, null, null, null);

            if (contactCursor != null && contactCursor.moveToFirst()) {
                name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactCursor.close();
            }
        }

        return name;
    }



}
