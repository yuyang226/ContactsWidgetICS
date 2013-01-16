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
	protected boolean supportDirectDial() {
		return true;
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			ContactsWidgetStackConfigurationActivity.deletePreference(context, appWidgetId, 
					ContactsWidgetConfigurationActivity.PREF_DIRECTDIAL_PREFIX);
		}
	}

}
