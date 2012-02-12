package com.gmail.yuyang226.contactswidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.gmail.yuyang226.contactswidget.models.ContactGroup;

public class ContactsWidgetConfigurationActivity extends Activity  {
	private static final String TAG = ContactsWidgetConfigurationActivity.class.getName();

	public static final String CONTACT_GROUP
	= "com.gmail.yuyang226.contactswidget.config.contact_group"; //$NON-NLS-1$
	public static final String CONTACT_STARRED = ContactsContract.Contacts.STARRED + " = '1'"; //$NON-NLS-1$
	
	public static final String PREF_GROUP_PREFIX = "group_";
	public static final String PREF_SORTING_PREFIX = "sorting_";
	
	private Spinner groupList;
	
	private RadioGroup contactsSorting;
	
	private Button okButton;

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private List<ContactGroup> contactGroups;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.appwidget_configure);

        //groups list
        
        this.groupList = (Spinner)findViewById(R.id.groupList);
        Collection<ContactGroup> contactGroups = new ContactAccessor().getContactGroups(getApplicationContext(), 
				0);
		ContactsWidgetConfigurationActivity.this.contactGroups = new ArrayList<ContactGroup>(contactGroups.size());
		List<String> groupNames = new ArrayList<String>(contactGroups.size());
        for (ContactGroup group : contactGroups) {
        	if (group.getTitle() != null) {
        		groupNames.add(group.getTitle());
        		ContactsWidgetConfigurationActivity.this.contactGroups.add(group);
        	}
        }
        ArrayAdapter<String> adapter = 
        	new ArrayAdapter<String> (ContactsWidgetConfigurationActivity.this, 
        			android.R.layout.simple_spinner_item, groupNames.toArray(new String[groupNames.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ContactsWidgetConfigurationActivity.this.groupList.setAdapter(adapter);
        
        setupButtons();

        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        
        setupButtons();
    }
    
    private void setupButtons() {
    	this.contactsSorting = (RadioGroup)findViewById(R.id.contactSorting);
    	this.contactsSorting.getCheckedRadioButtonId();
    	
    	this.okButton = (Button)findViewById(R.id.okButton);
    	this.okButton.setOnClickListener(mOnClickListener);
    }
    
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ContactsWidgetConfigurationActivity.this;

            String sortString = null;
            switch(contactsSorting.getCheckedRadioButtonId()) {
            case R.id.radioContactDisplayName:
            	sortString = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"; //$NON-NLS-1$
            	break;
            case R.id.radioContactRecent:
            	sortString = ContactsContract.Contacts.LAST_TIME_CONTACTED + " DESC"; //$NON-NLS-1$
            	break;
            case R.id.radioContactFrequency:
            
            default:
            	sortString = ContactsContract.Contacts.TIMES_CONTACTED + " DESC"; //$NON-NLS-1$
            	break;
            }
            saveSortingString(context, mAppWidgetId, sortString);
            String selection = groupList.getSelectedItemPosition() == 0 ? CONTACT_STARRED : 
            	String.valueOf(contactGroups.get(groupList.getSelectedItemPosition()).getGroupId());
            saveSelectionString(context, mAppWidgetId, selection);

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ContactsWidgetProvider.updateAppWidget(context, appWidgetManager,
                    mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    
 // Write the prefix to the SharedPreferences object for this widget
    static void saveSortingString(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SORTING_PREFIX, 0).edit();
        prefs.putString(PREF_SORTING_PREFIX + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadSortingString(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SORTING_PREFIX, 0);
        String prefix = prefs.getString(PREF_SORTING_PREFIX + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return ContactsContract.Contacts.TIMES_CONTACTED + " DESC";
        }
    }
    
    static void deleteSortingString(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SORTING_PREFIX, 0).edit();
        prefs.remove(PREF_SORTING_PREFIX + appWidgetId);
        prefs.commit();
    }
    
    static void saveSelectionString(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_GROUP_PREFIX, 0).edit();
        prefs.putString(PREF_GROUP_PREFIX + appWidgetId, text);
        prefs.commit();
    }

    static String loadSelectionString(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GROUP_PREFIX, 0);
        String prefix = prefs.getString(PREF_GROUP_PREFIX + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return CONTACT_STARRED;
        }
    }
    
    static void deleteSelectionString(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_GROUP_PREFIX, 0).edit();
        prefs.remove(PREF_GROUP_PREFIX + appWidgetId);
        prefs.commit();
    }
    
}