/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

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

	protected int getImageSizeId() {
		return R.dimen.size_large;
	}

	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity#canShowPeopleApp()
	 */
	@Override
	protected boolean canShowPeopleApp() {
		return true;
	}

}
