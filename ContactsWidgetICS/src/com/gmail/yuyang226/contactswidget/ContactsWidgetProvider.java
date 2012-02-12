/**
 * 
 */
package com.gmail.yuyang226.contactswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetProvider extends AppWidgetProvider {
	public static final String SHOW_QUICK_CONTACT_ACTION = "com.gmail.yuyang226.contactswidget.SHOW_QUICK_CONTACT_ACTION"; //$NON-NLS-1$
	public static final String CONTACT_URI = "com.gmail.yuyang226.contactswidget.CONTACT_URI"; //$NON-NLS-1$
	public static final String CONTACTS = "contacts"; //$NON-NLS-1$
	private static final String TAG = ContactsWidgetProvider.class.getName();

	/**
	 * 
	 */
	public ContactsWidgetProvider() {
		super();
	}
	
	@Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SHOW_QUICK_CONTACT_ACTION)) {
            Uri uri = intent.getData();
            if (uri != null) {
            	QuickContact.showQuickContact(context, intent.getSourceBounds(), 
            			uri, ContactsContract.QuickContact.MODE_SMALL, null);
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; i++) {
        	updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
            
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			ContactsWidgetConfigurationActivity.deleteSelectionString(context, appWidgetId);
			ContactsWidgetConfigurationActivity.deleteSortingString(context, appWidgetId);
		}
	}
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {
		Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId);
		// Here we setup the intent which points to the StackViewService which will
        // provide the views for this collection.
        Intent intent = new Intent(context, ContactsWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.contact_manager);
        rv.setRemoteAdapter(appWidgetId, R.id.contactList, intent);

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
        //toastIntent.putParcelableArrayListExtra(CONTACTS, getContacts(context));
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.contactList, toastPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.contactList);
	}
}
