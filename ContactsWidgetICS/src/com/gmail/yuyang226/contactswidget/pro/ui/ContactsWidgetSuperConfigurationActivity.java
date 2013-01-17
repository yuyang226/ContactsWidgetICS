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
ContactsWidgetDirectDialConfigurationActivity {

	/**
	 * 
	 */
	public ContactsWidgetSuperConfigurationActivity() {
		super(R.layout.appwidget_configure, R.layout.contact_entry_large);
	}

	protected Rect getImageSize() {
		return ContactsWidgetProvider.IMAGE_SIZE_LARGE_RECT;
	}

	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity#canShowPeopleApp()
	 */
	@Override
	protected boolean canShowPeopleApp() {
		return true;
	}

}
