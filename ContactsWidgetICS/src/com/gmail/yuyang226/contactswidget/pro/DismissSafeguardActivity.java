/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author yayu
 *
 */
public class DismissSafeguardActivity extends Activity {
	private static final String TAG = DismissSafeguardActivity.class.getName();

	/**
	 * 
	 */
	public DismissSafeguardActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dismiss_activity);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				DismissSafeguardActivity.this.finish();
			}
		}, 200);
	}

	@Override
	protected void onStop() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		final String action = getIntent().getStringExtra(ContactsWidgetProvider.INTENT_TAG_ACTION);
		Log.d(TAG, "Action: " + action);
		//we have to put the finish and launch of the QuickContact dialog in a separate thread
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (ContactsWidgetProvider.SHOW_QUICK_CONTACT_ACTION.equals(action)) {
					QuickContact.showQuickContact(DismissSafeguardActivity.this.getBaseContext(), 
							(Rect)getIntent().getExtras().get("sourceBundle"), 
							getIntent().getData(), ContactsContract.QuickContact.MODE_SMALL, null);
				} else if (ContactsWidgetProvider.LAUNCH_PEOPLE_ACTION.equals(action)) {
					//For the contacts (viewing them) 
					Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
					/*
						 //For the contacts (picking one)
						 Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
						//For the dial pad
						 Intent intent = new Intent(Intent.ACTION_DIAL, null);
						//For viewing the call log
						 Intent intent = new Intent(Intent.ACTION_VIEW, CallLog.Calls.CONTENT_URI);*/
					DismissSafeguardActivity.this.startActivity(intent);
				}
			}
		}, 500);
		super.onStop();
	}
	

}
