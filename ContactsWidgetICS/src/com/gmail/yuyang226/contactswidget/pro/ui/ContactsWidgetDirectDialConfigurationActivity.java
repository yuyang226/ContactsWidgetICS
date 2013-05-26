/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.gmail.yuyang226.contactswidget.pro.R;

/**
 * @author yayu
 *
 */
public class ContactsWidgetDirectDialConfigurationActivity extends
		ContactsWidgetConfigurationActivity {

	/**
	 * 
	 */
	public ContactsWidgetDirectDialConfigurationActivity() {
		super(R.layout.appwidget_configure, R.layout.contact_entry_direct_dial);
	}
	
	/**
	 * @param configActivityLayoutId
	 * @param widgetEntryLayoutId
	 */
	public ContactsWidgetDirectDialConfigurationActivity(
			int configActivityLayoutId, int widgetEntryLayoutId) {
		super(configActivityLayoutId, widgetEntryLayoutId);
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity#canDirectDial()
	 */
	@Override
	protected boolean canDirectDial() {
		return true;
	}
	
	@Override
	protected boolean supportContactNameBottom() {
    	return false;
    }

	@Override
	protected void savePreferences(Context context, int appWidgetId) {
		CheckBox canDirectDial = (CheckBox)findViewById(R.id.checkDirectDial);
		if (canDirectDial != null) {
			boolean supportDirectDial = canDirectDial.isChecked() && canDirectDial.getVisibility() == View.VISIBLE;
			saveSupportDirectDial(context, appWidgetId, 
					supportDirectDial);
			if (!supportDirectDial) {
				
			}
		}
		
		CheckBox showPhoneNumber = (CheckBox)findViewById(R.id.checkShowPhoneNumber);
		if (showPhoneNumber != null) {
			saveShowPhoneNumber(context, appWidgetId, 
					showPhoneNumber.isChecked() && showPhoneNumber.getVisibility() == View.VISIBLE);
		}
		
		CheckBox viaContactIcon = (CheckBox)findViewById(R.id.checkViaContactPic);
		if (viaContactIcon != null) {
			saveViaContactIcon(context, appWidgetId, 
					viaContactIcon.isChecked() && viaContactIcon.getVisibility() == View.VISIBLE);
		}
		
		CheckBox canDirectSms = (CheckBox)findViewById(R.id.checkDirectSms);
		if (canDirectSms != null) {
			saveDirectSms(context, appWidgetId, 
					canDirectSms.isChecked() && canDirectSms.getVisibility() == View.VISIBLE);
		}
		
		super.savePreferences(context, appWidgetId);
	}

}
