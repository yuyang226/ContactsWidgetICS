/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

/**
 * @author yayu
 *
 */
public class ContactsWidgetLargeProvider extends ContactsWidgetProvider {

	/**
	 * 
	 */
	public ContactsWidgetLargeProvider() {
		super();
	}

	@Override
	protected boolean supportDirectDial() {
		return true;
	}

}
