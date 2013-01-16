/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.gmail.yuyang226.contactswidget.pro.ContactsWidgetProvider;
import com.gmail.yuyang226.contactswidget.pro.R;
import com.gmail.yuyang226.contactswidget.pro.R.layout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
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
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DismissSafeguardActivity extends Activity {
	private static final String TAG = DismissSafeguardActivity.class.getName();
	private String action = null;

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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		setContentView(R.layout.dismiss_activity);
		this.action = getIntent().getStringExtra(ContactsWidgetProvider.INTENT_TAG_ACTION);
		Log.d(TAG, "Action: " + action);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				DismissSafeguardActivity.this.setResult(RESULT_OK);
				DismissSafeguardActivity.this.finish();
			}
		}, 150);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		}, 10);
	}
	
	
	

}
