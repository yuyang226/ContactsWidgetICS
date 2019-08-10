/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import android.content.Context;

import com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity;
import com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetStackConfigurationActivity;

/**
 * @author yayu
 *
 */
public class ContactsWidgetDirectDialProvider extends ContactsWidgetProvider {

	/**
	 * 
	 */
	public ContactsWidgetDirectDialProvider() {
		super();
	}
	
	@Override
	protected int getWidgetEntryLayoutId() {
		return R.layout.contact_entry_direct_dial;
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			ContactsWidgetStackConfigurationActivity.deletePreference(context, appWidgetId, 
					ContactsWidgetConfigurationActivity.PREF_DIRECTDIAL_PREFIX);
			ContactsWidgetStackConfigurationActivity.deletePreference(context, appWidgetId, 
					ContactsWidgetConfigurationActivity.PREF_SHOWPHONENUMBER_PREFIX);
		}
	}

}
