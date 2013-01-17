/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import android.content.Context;

import com.gmail.yuyang226.contactswidget.pro.R;

/**
 * @author yayu
 *
 */
public class ContactsWidgetDirectDialConfigurationActivity extends
		ContactsWidgetConfigurationActivity {

	/**
	 * 
	 */
	public ContactsWidgetDirectDialConfigurationActivity() {
		super(R.layout.appwidget_configure, R.layout.contact_entry_direct_dial);
	}
	
	/**
	 * @param configActivityLayoutId
	 * @param widgetEntryLayoutId
	 */
	public ContactsWidgetDirectDialConfigurationActivity(
			int configActivityLayoutId, int widgetEntryLayoutId) {
		super(configActivityLayoutId, widgetEntryLayoutId);
	}

	@Override
	protected void savePreferences(Context context, int appWidgetId) {
		super.savePreferences(context, appWidgetId);
		saveSupportDirectDial(context, appWidgetId, true);
	}

}
