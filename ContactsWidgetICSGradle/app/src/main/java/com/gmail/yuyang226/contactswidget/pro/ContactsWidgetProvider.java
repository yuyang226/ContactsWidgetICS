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
import android.view.View;
import android.widget.RemoteViews;

import com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity;
import com.gmail.yuyang226.contactswidget.pro.ui.DismissSafeguardActivity;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetProvider extends AppWidgetProvider {
	public static final String INTENT_TAG_ACTION = "action";
	public static final String SHOW_QUICK_CONTACT_ACTION = "com.gmail.yuyang226.contactswidget.SHOW_QUICK_CONTACT_ACTION"; //$NON-NLS-1$
	public static final String DIRECT_DIAL_ACTION = "com.gmail.yuyang226.contactswidget.DIRECT_DIAL_ACTION"; //$NON-NLS-1$
	public static final String DIRECT_SMS_ACTION = "com.gmail.yuyang226.contactswidget.DIRECT_SMS_ACTION"; //$NON-NLS-1$
	public static final String LAUNCH_PEOPLE_ACTION = "com.gmail.yuyang226.contactswidget.LAUNCH_PEOPLE_ACTION"; //$NON-NLS-1$
	public static final String CONTACT_URI = "com.gmail.yuyang226.contactswidget.CONTACT_URI"; //$NON-NLS-1$
	public static final String CONTACTS = "contacts"; //$NON-NLS-1$
	public static final String CONTACT_ENTRY_LAYOUT_ID = "contact.entry.layout.id"; //$NON-NLS-1$
	public static final String IMAGE_SIZE = "contact.entry.image.size"; //$NON-NLS-1$
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
            	if (intent.hasExtra(INTENT_TAG_ACTION) && intent.getStringExtra(INTENT_TAG_ACTION) != null) {
            		boolean isDirectDial = DIRECT_DIAL_ACTION.equals(intent.getStringExtra(INTENT_TAG_ACTION));
            		//direct dial
            		Intent dialIntent = new Intent(isDirectDial ? Intent.ACTION_CALL : Intent.ACTION_SENDTO);
            		dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		dialIntent.setData(uri);
            		boolean notStartActivity = false;
            		if (!isDirectDial) {
            			//direct sms
            			dialIntent.putExtra("compose_mode", true);
            			if (isKeyguard) {
                			//need to dismiss the keyguard screen
                    		Intent in = new Intent(context, DismissSafeguardActivity.class);
                    		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    		in.setData(uri);
                    		in.putExtra("sourceBundle", intent.getSourceBounds());
                    		in.putExtra(INTENT_TAG_ACTION, DIRECT_SMS_ACTION);
                    		context.startActivity(in);
                    		notStartActivity = true;
                    	}
            		}
            		if (!notStartActivity) {
            			//it is running keyguard mode and users want direct SMS
            			context.startActivity(dialIntent);
            		}
            	} else {
            		//show quick contacts
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
        	int appWidgetId = appWidgetIds[i];
        	int entryLayoutId = ContactsWidgetConfigurationActivity.loadEntryLayoutId(context, appWidgetId, 
        			getWidgetEntryLayoutId());
        	int imageSize = ContactsWidgetConfigurationActivity.loadImageSize(context, appWidgetId, getDefaultImageSize(context));
        	updateAppWidget(context, appWidgetManager, appWidgetId, entryLayoutId,
        			ContactsWidgetConfigurationActivity.loadShowPeopleApp(context, appWidgetId), 
        			new Rect(0, 0, imageSize, imageSize));
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
	
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
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
        
        int layoutId = widgetProviderInfo.initialLayout;
        if (widgetEntryLayoutId == R.layout.contact_entry_direct_dial
        		&& (!ContactsWidgetConfigurationActivity.loadSupportDirectDial(context, appWidgetId)
        		|| ContactsWidgetConfigurationActivity.loadViaContactIcon(context, appWidgetId))) {
        	//This is the small direct dial, the user has choose not to use direct dial or want to direct dial via contact icon
        	layoutId = R.layout.contact_manager;
        }
        RemoteViews rv = new RemoteViews(context.getPackageName(), 
        		layoutId);
        rv.setRemoteAdapter(R.id.contactList, intent);
        if (isKeyguard || canLaunchPeopleApp) {
        	Intent launchPeopleIntent = new Intent(context, ContactsWidgetProvider.class);
        	launchPeopleIntent.setAction(ContactsWidgetProvider.LAUNCH_PEOPLE_ACTION);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, launchPeopleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        	rv.setOnClickPendingIntent(R.id.buttonPeople, pi);
        }
        rv.setViewVisibility(R.id.buttonPeople, canLaunchPeopleApp ? View.VISIBLE : View.GONE);
        
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
	
	protected int getDefaultImageSize(Context context) {
		return context.getResources().getDimensionPixelSize(R.dimen.size_small);
	}
	
}
