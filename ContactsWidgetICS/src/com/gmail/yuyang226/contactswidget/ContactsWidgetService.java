/**
 * 
 */
package com.gmail.yuyang226.contactswidget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetService extends RemoteViewsService {

	/**
	 * 
	 */
	public ContactsWidgetService() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.widget.RemoteViewsService#onGetViewFactory(android.content.Intent)
	 */
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
	}
	
	public Bitmap loadContactPhoto(long  id) {
	    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
	    if (input == null) {
	        return null;
	    }
	    return BitmapFactory.decodeStream(input);
	}
	
	class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	    private final List<Contact> mWidgetItems = new ArrayList<Contact>();
	    private Context mContext;
	    private int mAppWidgetId;

	    public GridRemoteViewsFactory(Context context, Intent intent) {
	        mContext = context;
	        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
	                AppWidgetManager.INVALID_APPWIDGET_ID);
	    }
	    
	    /**
	     * Obtains the contact list for the currently selected account.
	     *
	     * @return A cursor for for accessing the contact list.
	     */
	    private List<Contact> getContacts() {
	    	final List<Contact> contacts = new ArrayList<Contact>();
	        // Run query
	        Uri uri = ContactsContract.Contacts.CONTENT_URI;
	        String[] projection = new String[] {
	                ContactsContract.Contacts._ID,
	                ContactsContract.Contacts.DISPLAY_NAME,
	                ContactsContract.Contacts.PHOTO_URI, 
	        };
	        String selection =  ContactsContract.Contacts.STARRED + " = '1'";
	        String[] selectionArgs = null;
	        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

	        CursorLoader loader = new CursorLoader(this.mContext, uri, projection, selection, selectionArgs, sortOrder); //(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
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
	                contacts.add(contact);
	                if (photoUri != null && photoUri.length() > 0) {
	                	contact.setPhoto(loadContactPhoto(contactId));
	                }
	                
	                cursor.moveToNext();
	            }
	        	/*SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contact_entry, cursor,
	        			fields, new int[] {R.id.contactEntryText, R.id.contactPhoto});*/
	        	//return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	        } finally {
	        	loader.stopLoading();
	        	if (cursor != null) {
	        		cursor.close();
	        	}
	        }
	        return contacts;
	    }

	    public void onCreate() {
	        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
	        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
	        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
	       mWidgetItems.clear();
	       mWidgetItems.addAll(getContacts());

	        // We sleep for 3 seconds here to show how the empty view appears in the interim.
	        // The empty view is set in the StackWidgetProvider and should be a sibling of the
	        // collection view.
	        try {
	            Thread.sleep(3000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }

	    public void onDestroy() {
	        // In onDestroy() you should tear down anything that was setup for your data source,
	        // eg. cursors, connections, etc.
	        mWidgetItems.clear();
	    }

	    public int getCount() {
	        return mWidgetItems.size();
	    }

	    public RemoteViews getViewAt(int position) {
	        // position will always range from 0 to getCount() - 1.

	        // We construct a remote views item based on our widget item xml file, and set the
	        // text based on the position.
	        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.contact_entry);
	        rv.setTextViewText(R.id.contactEntryText, mWidgetItems.get(position).getDisplayName());
	        Bitmap photo = mWidgetItems.get(position).getPhoto();
	        if (photo != null) {
	        	//the contact has an icon
	        	rv.setImageViewBitmap(R.id.contactPhoto, photo);
	        } else {
	        	rv.setImageViewResource(R.id.contactPhoto, R.drawable.icon);
	        }

	        // You can do heaving lifting in here, synchronously. For example, if you need to
	        // process an image, fetch something from the network, etc., it is ok to do it here,
	        // synchronously. A loading view will show up in lieu of the actual contents in the
	        // interim.
	        try {
	            System.out.println("Loading view " + position);
	            Thread.sleep(500);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }

	        // Return the remote views object.
	        return rv;
	    }

	    public RemoteViews getLoadingView() {
	        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
	        // return null here, you will get the default loading view.
	        return null;
	    }

	    public int getViewTypeCount() {
	        return 1;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public boolean hasStableIds() {
	        return true;
	    }

	    public void onDataSetChanged() {
	        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
	        // on the collection view corresponding to this factory. You can do heaving lifting in
	        // here, synchronously. For example, if you need to process an image, fetch something
	        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
	        // in its current state while work is being done here, so you don't need to worry about
	        // locking up the widget.
	    }
	}

}
