/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetProvider extends AppWidgetProvider {
	static final String INTENT_TAG_ACTION = "action";
	public static final String SHOW_QUICK_CONTACT_ACTION = "com.gmail.yuyang226.contactswidget.SHOW_QUICK_CONTACT_ACTION"; //$NON-NLS-1$
	public static final String LAUNCH_PEOPLE_ACTION = "com.gmail.yuyang226.contactswidget.LAUNCH_PEOPLE_ACTION"; //$NON-NLS-1$
	public static final String CONTACT_URI = "com.gmail.yuyang226.contactswidget.CONTACT_URI"; //$NON-NLS-1$
	public static final String CONTACTS = "contacts"; //$NON-NLS-1$
	public static final String CONTACT_ENTRY_LAYOUT_ID = "contact.entry.layout.id"; //$NON-NLS-1$
	public static final String IMAGE_SIZE = "contact.entry.image.size"; //$NON-NLS-1$
	private static final int IMAGE_SIZE_SMALL_HEIGHT = 72;
	public static final Rect IMAGE_SIZE_SMALL_RECT = new Rect(0, 0, IMAGE_SIZE_SMALL_HEIGHT, IMAGE_SIZE_SMALL_HEIGHT);
	private static final int IMAGE_SIZE_LARGE_HEIGHT = 128;
	public static final Rect IMAGE_SIZE_LARGE_RECT = new Rect(0, 0, IMAGE_SIZE_LARGE_HEIGHT, IMAGE_SIZE_LARGE_HEIGHT);
	private static final String TAG = ContactsWidgetProvider.class.getName();

	/**
	 * 
	 */
	public ContactsWidgetProvider() {
		super();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean isRunningKeyguard(final Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			KeyguardManager km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
			return km.inKeyguardRestrictedInputMode() || km.isKeyguardLocked();
		}
		return false;
	}
	
	@Override
    public void onReceive(final Context context, final Intent intent) {
		boolean isKeyguard = isRunningKeyguard(context);
        if (intent.getAction().equals(SHOW_QUICK_CONTACT_ACTION)) {
            final Uri uri = intent.getData();
            if (uri != null) {
            	if (isKeyguard) {
            		Intent in = new Intent(context, DismissSafeguardActivity.class);
            		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		in.setData(uri);
            		in.putExtra("sourceBundle", intent.getSourceBounds());
            		in.putExtra(INTENT_TAG_ACTION, SHOW_QUICK_CONTACT_ACTION);
            		context.startActivity(in);
            	} else {
            		QuickContact.showQuickContact(context, intent.getSourceBounds(), 
                			uri, ContactsContract.QuickContact.MODE_SMALL, null);
            	}
            	
            }
        } else if (intent.getAction().equals(LAUNCH_PEOPLE_ACTION)) {
        	if (isKeyguard) {
        		Intent in = new Intent(context, DismissSafeguardActivity.class);
        		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		in.putExtra(INTENT_TAG_ACTION, LAUNCH_PEOPLE_ACTION);
        		context.startActivity(in);
        	} else {
        		Intent in = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				/*
					 //For the contacts (picking one)
					 Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					//For the dial pad
					 Intent intent = new Intent(Intent.ACTION_DIAL, null);
					//For viewing the call log
					 Intent intent = new Intent(Intent.ACTION_VIEW, CallLog.Calls.CONTENT_URI);*/
        		context.startActivity(in);
        	}
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	//making sure your friends' new pictures will be loaded
        ContactAccessor.clearImageCache();
        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; i++) {
        	updateAppWidget(context, appWidgetManager, appWidgetIds[i], getWidgetEntryLayoutId(),
        			canLaunchPeopleApp(), getImageSize());
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted"); //$NON-NLS-1$
		super.onDeleted(context, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			for (String prefPrefix : ContactsWidgetConfigurationActivity.PREFS_PREFIX) {
				ContactsWidgetConfigurationActivity.deletePreference(context, appWidgetId, prefPrefix);
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	static boolean checkIsKeyguard(AppWidgetManager appWidgetManager, int appWidgetId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			//only do the check if it is running Jelly Bean or higher
			Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
			int category = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
			boolean isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;
			if (isKeyguard) {
				Log.d(TAG, "Running on lockscreen. appWidgetId=" + appWidgetId); //$NON-NLS-1$
			}
			return isKeyguard;
		}
		return false;
	}
	
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId, int widgetEntryLayoutId, boolean canLaunchPeopleApp, Rect imageSize) {
		Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId); //$NON-NLS-1$
		AppWidgetProviderInfo widgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
		if (context == null || widgetProviderInfo == null) {
			Log.w(TAG, context.getResources().getString(
					R.string.invalid_provider_info) + widgetProviderInfo);
			/*Toast.makeText(context, context.getResources().getString(
					R.string.invalid_provider_info) + widgetProviderInfo, Toast.LENGTH_LONG).show();*/
			return;
		}
		//check if it is running on the lock screen
		boolean isKeyguard = checkIsKeyguard(appWidgetManager, appWidgetId);
		
		// Here we setup the intent which points to the StackViewService which will
        // provide the views for this collection.
        Intent intent = new Intent(context, ContactsWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(CONTACT_ENTRY_LAYOUT_ID, widgetEntryLayoutId);
        intent.putExtra(IMAGE_SIZE, imageSize);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), 
        		widgetProviderInfo.initialLayout);
        rv.setRemoteAdapter(R.id.contactList, intent);
        if (isKeyguard || canLaunchPeopleApp) {
        	Intent launchPeopleIntent = new Intent(context, ContactsWidgetProvider.class);
        	launchPeopleIntent.setAction(ContactsWidgetProvider.LAUNCH_PEOPLE_ACTION);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, launchPeopleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        	rv.setOnClickPendingIntent(R.id.buttonPeople, pi);	
        }
        
        // The empty view is displayed when the collection has no items. It should be a sibling
        // of the collection view.
        rv.setEmptyView(R.id.contactList, R.id.empty_view);
        
        // Here we setup the a pending intent template. Individuals items of a collection
        // cannot setup their own pending intents, instead, the collection as a whole can
        // setup a pending intent template, and the individual items can set a fillInIntent
        // to create unique before on an item to item basis.
        Intent toastIntent = new Intent(context, ContactsWidgetProvider.class);
        toastIntent.setAction(ContactsWidgetProvider.SHOW_QUICK_CONTACT_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        toastIntent.putExtra(CONTACT_ENTRY_LAYOUT_ID, widgetEntryLayoutId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.contactList, toastPendingIntent);
        
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.contactList);
	}
	
	protected int getWidgetEntryLayoutId() {
		return R.layout.contact_entry;
	}
	
	protected boolean canLaunchPeopleApp() {
		return false;
	}
	
	protected Rect getImageSize() {
		return IMAGE_SIZE_SMALL_RECT;
	}

}
