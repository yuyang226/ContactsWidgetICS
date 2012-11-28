/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import android.graphics.Rect;

/**
 * @author yayu
 *
 */
public class ContactsWidgetSuperConfigurationActivity extends
		ContactsWidgetConfigurationActivity {

	/**
	 * 
	 */
	public ContactsWidgetSuperConfigurationActivity() {
		super(R.layout.appwidget_configure, R.layout.contact_entry_large);
	}
	
	protected Rect getImageSize() {
		return ContactsWidgetProvider.IMAGE_SIZE_LARGE_RECT;
	}

}
