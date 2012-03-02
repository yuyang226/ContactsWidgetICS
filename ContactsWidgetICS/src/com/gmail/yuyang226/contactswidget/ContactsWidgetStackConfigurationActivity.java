/**
 * 
 */
package com.gmail.yuyang226.contactswidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetStackConfigurationActivity extends
		ContactsWidgetConfigurationActivity {
	public static final String PREF_SHOWNAME_PREFIX = "showname_"; //$NON-NLS-1$
	public static final String PREF_LOOPCONTACTS_PREFIX = "loopcontacts_"; //$NON-NLS-1$

	public ContactsWidgetStackConfigurationActivity() {
		super(R.layout.appwidget_configure_stack, R.layout.contact_entry_large);
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.ContactsWidgetConfigurationActivity#savePreferences()
	 */
	@Override
	protected void savePreferences(Context context, int appWidgetId) {
		super.savePreferences(context, appWidgetId);
		CheckBox showName = (CheckBox)findViewById(R.id.checkShowName);
		saveShowName(context, appWidgetId, showName.isChecked());
		CheckBox loopContacts = (CheckBox)findViewById(R.id.loopContacts);
		saveLoopContacts(context, appWidgetId, loopContacts.isChecked());
	}

	static void saveShowName(Context context, int appWidgetId, boolean showName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SHOWNAME_PREFIX, 0).edit();
        prefs.putString(PREF_SHOWNAME_PREFIX + appWidgetId, String.valueOf(showName));
        prefs.commit();
    }

    static boolean loadShowName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SHOWNAME_PREFIX, 0);
        String value = prefs.getString(PREF_SHOWNAME_PREFIX + appWidgetId, Boolean.TRUE.toString());
        return Boolean.valueOf(value);
    }
    
    static void deleteShowName(Context context, int appWidgetId) {
        deletePreference(context, appWidgetId, PREF_SHOWNAME_PREFIX);
    }
    
    static void saveLoopContacts(Context context, int appWidgetId, boolean loopContacts) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_LOOPCONTACTS_PREFIX, 0).edit();
        prefs.putString(PREF_LOOPCONTACTS_PREFIX + appWidgetId, String.valueOf(loopContacts));
        prefs.commit();
    }

    static boolean loadLoopContacts(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_LOOPCONTACTS_PREFIX, 0);
        String value = prefs.getString(PREF_LOOPCONTACTS_PREFIX + appWidgetId, Boolean.TRUE.toString());
        return Boolean.valueOf(value);
    }
    
    static void deleteLoopContacts(Context context, int appWidgetId) {
        deletePreference(context, appWidgetId, PREF_LOOPCONTACTS_PREFIX);
    }

}
