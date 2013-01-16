/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import android.content.Context;
import android.widget.CheckBox;

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
	
	@Override
	protected void savePreferences(Context context, int appWidgetId) {
		super.savePreferences(context, appWidgetId);
		saveSupportDirectDial(context, appWidgetId, true);
	}

}
