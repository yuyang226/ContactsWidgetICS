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
	
	/**
	 * @param configActivityLayoutId
	 * @param widgetEntryLayoutId
	 */
	public ContactsWidgetDirectDialConfigurationActivity(
			int configActivityLayoutId, int widgetEntryLayoutId) {
		super(configActivityLayoutId, widgetEntryLayoutId);
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity#canDirectDial()
	 */
	@Override
	protected boolean canDirectDial() {
		return true;
	}

	@Override
	protected void savePreferences(Context context, int appWidgetId) {
		super.savePreferences(context, appWidgetId);
		CheckBox canDirectDial = (CheckBox)findViewById(R.id.checkDirectDial);
		if (canDirectDial != null) {
			saveSupportDirectDial(context, appWidgetId, canDirectDial.isChecked());
		}
		
		CheckBox showPhoneNumber = (CheckBox)findViewById(R.id.checkShowPhoneNumber);
		if (showPhoneNumber != null) {
			saveShowPhoneNumber(context, appWidgetId, showPhoneNumber.isChecked());
		}
	}

}
