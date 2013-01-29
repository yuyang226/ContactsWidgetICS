/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import android.content.Context;


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
	
	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.ContactsWidgetProvider#getWidgetEntryLayoutId()
	 */
	@Override
	protected int getWidgetEntryLayoutId() {
		return R.layout.contact_entry_large;
	}
	
	protected int getDefaultImageSize(Context context) {
		return context.getResources().getDimensionPixelSize(R.dimen.size_large);
	}

}
