/**
 * 
 */
package com.gmail.yuyang226.contactswidget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.QuickContact;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author yayu
 *
 */
public class DismissSafeguardActivity extends Activity {

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
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//we have to put the finish and launch of the QuickContact dialog in a separate thread
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				QuickContact.showQuickContact(DismissSafeguardActivity.this.getBaseContext(), 
						(Rect)getIntent().getExtras().get("sourceBundle"), 
		    			getIntent().getData(), ContactsContract.QuickContact.MODE_SMALL, null);
				DismissSafeguardActivity.this.finish();
			}
		}, 400);
	}

	@Override
	protected void onStop() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		super.onStop();
	}
	

}
