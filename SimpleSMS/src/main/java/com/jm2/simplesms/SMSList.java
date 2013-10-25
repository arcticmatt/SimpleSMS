package com.jm2.simplesms;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class SMSList extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String KEY_THEMES_ACTIVITY = "pref_key_themes_activities";

    String columns[];
    SharedPreferences sharedPrefs;

    // This is the Adapter being used to display the list's data.
    SMSCursorAdapter mAdapter;
    SimpleSMSApp mOmnotifyApp;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv = getListView();

        if (lv != null) {
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                    TextView body = (TextView) arg1.findViewById(R.id.body);
                    CharSequence charBody = body.getText();
                    String stringBody = "";
                    if (charBody != null && charBody.length() > 0) {
                        stringBody = charBody.toString();
                    }
                    ClipboardManager clipboard = (ClipboardManager)
                            getActivity().getSystemService(getActivity().getBaseContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", stringBody);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getActivity().getBaseContext(), "Message copied",
                    Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        setHasOptionsMenu(true);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        int prefCount = Integer.parseInt(sharedPrefs.getString("pref_list_sms_count", "50"));
        if (prefCount != 0) {
            setEmptyText("No text messages");
        }
        else {
            setEmptyText("");
        }

        mOmnotifyApp = (SimpleSMSApp) getActivity().getApplication();

        mAdapter = mOmnotifyApp.getMessageBoxAdapter();

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        //set Line Divider color
        if (lv != null) {
            // If dark theme, set divider color to light gray
            if (sharedPrefs.getString(KEY_THEMES_ACTIVITY, "1").equals("2")) {
                lv.setDivider(getActivity().getResources().getDrawable(R.color.light_gray));
            }
            // Else (if light theme), set divider color to gray
            else {
                lv.setDivider(getActivity().getResources().getDrawable(R.color.gray));
            }
            lv.setDividerHeight(2);
        }
    }


    /*private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mAdapter.getFilter().filter(s);
        }

    };*/

    // On list item click, put the clicked-on person's phone number in the
    // contact text view, and put the focus on the message text view (which brings up keyboard)
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CustomAutoComplete contact = (CustomAutoComplete) getActivity().findViewById(R.id.chooseContact);

        Cursor c = ((SMSCursorAdapter) l.getAdapter()).getCursor();
        if (c != null) {
            c.moveToPosition(position);
            String contactNumber = c.getString(c.getColumnIndex("address"));

            TextView name = (TextView) v.findViewById(R.id.address);
            // Prevent NPE
            CharSequence charContactName = name.getText();
            String contactName = "";
            if (charContactName != null && charContactName.length() > 0) {
                contactName = charContactName.toString();
            }
            if (contactNumber != null && contactNumber.equals(contactName)) {
                contact.setText("<" + contactNumber + ">");
            } else {
                contact.setText("" + contactName + "<" + contactNumber + ">");
            }

            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);

            EditText editText = (EditText) getActivity().findViewById(R.id.message);
            editText.requestFocus();
        }
    }

    // Create a loader that displays all received and sent SMS messages
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mSmsinboxQueryUri = Uri.parse("content://sms");
        columns = new String[]{"address", "_id", "type", "body"};

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select1 = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + Contacts.DISPLAY_NAME + " != '' ))";

        //String select = "((" + "thread_id" + "=4))";
        //String select = "((" + "_id" + ">_id-10))";
        return new CursorLoader(getActivity(), mSmsinboxQueryUri,
                columns, null, null,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

        int length = data.getCount();

        // The list should now be shown.
        if (isResumed()) {
            // Make sure cursor is valid
            // before showing the list. Otherwise
            // the app will crash
            if (data.moveToFirst() && data.moveToLast()) {
                /*Only set the list adapter after the cursor
                has been switched in. This is so that the
                getCount() method in SMSCursorAdapter can run
                getCount() on said cursor. No need to call setListShown,
                as it is done automatically*/
                setListAdapter(mAdapter);
            }
            else {
                // To get rid of the progress indicator,
                // set an empty adapter and show
                // the list
                setListAdapter(null);
                setListShown(true);

            }
        } else {
            // Not sure about this stuff
            setListAdapter(mAdapter);
            setListShownNoAnimation(true);

        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
