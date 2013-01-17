/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import android.graphics.Rect;

/**
 * @author yayu
 *
 */
public class ContactsWidgetSuperProvider extends ContactsWidgetDirectDialProvider {

	/**
	 * 
	 */
	public ContactsWidgetSuperProvider() {
		super();
	}
	
	@Override
	protected boolean canLaunchPeopleApp() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.ContactsWidgetProvider#getWidgetEntryLayoutId()
	 */
	@Override
	protected int getWidgetEntryLayoutId() {
		return R.layout.contact_entry_large;
	}
	
	@Override
	protected Rect getImageSize() {
		return IMAGE_SIZE_LARGE_RECT;
	}

}
