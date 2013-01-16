/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import android.graphics.Rect;

import com.gmail.yuyang226.contactswidget.pro.ContactsWidgetProvider;
import com.gmail.yuyang226.contactswidget.pro.R;

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
