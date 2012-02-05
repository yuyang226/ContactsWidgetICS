/**
 * 
 */
package com.gmail.yuyang226.contactswidget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.QuickContact;
import android.widget.RemoteViews;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetProvider extends AppWidgetProvider {
	public static final String SHOW_QUICK_CONTACT_ACTION = "com.gmail.yuyang226.contactswidget.SHOW_QUICK_CONTACT_ACTION"; //$NON-NLS-1$
	public static final String CONTACT_URI = "com.gmail.yuyang226.contactswidget.CONTACT_URI"; //$NON-NLS-1$
	public static final String CONTACTS = "com.gmail.yuyang226.contactswidget.CONTACTS"; //$NON-NLS-1$

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

            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ContactsWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.contact_manager);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.contactList, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.contactList, R.id.empty_view);

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            Intent toastIntent = new Intent(context, ContactsWidgetProvider.class);
            toastIntent.setAction(ContactsWidgetProvider.SHOW_QUICK_CONTACT_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.contactList, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    
    /**
     * Obtains the contact list for the currently selected account.
     *
     * @return A cursor for for accessing the contact list.
     */
    private List<Contact> getContacts(Context context) {
    	final List<Contact> contacts = new ArrayList<Contact>();
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI, 
        };
        String selection =  ContactsContract.Contacts.STARRED + " = '1'"; //$NON-NLS-1$
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"; //$NON-NLS-1$

        CursorLoader loader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        Cursor cursor = null;
        try {
        	loader.startLoading();
        	cursor = loader.loadInBackground();
        	cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                long contactId = cursor.getLong(0);
                String displayName = cursor.getString(1);
                String photoUri = cursor.getString(2);
                Contact contact = new Contact();
                contact.setContactId(contactId);
                contact.setDisplayName(displayName);
                contact.setPhotoUri(photoUri);
                contact.setContactUri(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId));
                contacts.add(contact);
                if (photoUri != null && photoUri.length() > 0) {
                	contact.setPhoto(loadContactPhoto(context.getContentResolver(), contact.getContactUri()));
                }
                cursor.moveToNext();
            }
        } finally {
        	loader.stopLoading();
        	if (cursor != null) {
        		cursor.close();
        	}
        }
        return contacts;
    }
    
    private Bitmap loadContactPhoto(ContentResolver contentResolver, Uri uri) {
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
	    if (input == null) {
	        return null;
	    }
	    return BitmapFactory.decodeStream(input);
	}
    
}
