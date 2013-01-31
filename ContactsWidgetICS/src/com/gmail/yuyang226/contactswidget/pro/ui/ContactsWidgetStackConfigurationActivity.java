/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import com.gmail.yuyang226.contactswidget.pro.R;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetStackConfigurationActivity extends
ContactsWidgetDirectDialConfigurationActivity {

	public ContactsWidgetStackConfigurationActivity() {
		super(R.layout.appwidget_configure, R.layout.contact_entry_large_stack);
	}
	
	@Override
	protected int getImageSizeId() {
		return R.dimen.size_large;
	}
	
    /* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity#isStackView()
	 */
	@Override
	protected boolean isStackView() {
		return true;
	}
	
}
