/**
 * 
 */
package com.gmail.yuyang226.contactswidget;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gmail.yuyang226.contactswidget.models.Contact;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactsWidgetService extends RemoteViewsService {
	private static final String TAG = ContactsWidgetService.class.getName();

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
	
	class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	    private final List<Contact> mWidgetItems = new ArrayList<Contact>();
	    private Context mContext;
	    private int mAppWidgetId;
	    private int widgetEntryLayoutId;

	    public GridRemoteViewsFactory(Context context, Intent intent) {
	    	//Log.d(TAG, String.format("Input Params: %s, %s", String.valueOf(context), String.valueOf(intent))); //$NON-NLS-1$
	        mContext = context;
	        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
	                AppWidgetManager.INVALID_APPWIDGET_ID);
	        
	        widgetEntryLayoutId = intent.getIntExtra(ContactsWidgetProvider.CONTACT_ENTRY_LAYOUT_ID,
	                R.layout.contact_entry);
	    }

	    public void onCreate() {
	    	mWidgetItems.clear();
		    mWidgetItems.addAll(new ContactAccessor().getContacts(
		    		getContentResolver(), this.mContext, this.mAppWidgetId));
	    }

	    public void onDestroy() {
	    	for (Contact contact: mWidgetItems) {
	    		if (contact.getPhoto() != null && !contact.getPhoto().isRecycled()) {
	    			contact.getPhoto().recycle();
	    		}
	    	}
	        mWidgetItems.clear();
	    }

	    public int getCount() {
	        return mWidgetItems.size();
	    }

	    public RemoteViews getViewAt(int position) {
	        // position will always range from 0 to getCount() - 1.

	        // We construct a remote views item based on our widget item xml file, and set the
	        // text based on the position.
	        RemoteViews rv = new RemoteViews(mContext.getPackageName(), widgetEntryLayoutId);
	        Contact contact = mWidgetItems.get(position);
	        if (ContactsWidgetStackConfigurationActivity.loadShowName(this.mContext, this.mAppWidgetId)) {
	        	rv.setViewVisibility(R.id.contactEntryText, View.VISIBLE);
	        	rv.setTextViewText(R.id.contactEntryText, contact.getDisplayName());
	        } else {
	        	rv.setViewVisibility(R.id.contactEntryText, View.INVISIBLE);
	        }
	        
	        Bitmap photo = contact.getPhoto();
	        if (photo != null) {
	        	//the contact has an icon
	        	rv.setImageViewBitmap(R.id.contactPhoto, photo);
	        } else {
	        	rv.setImageViewResource(R.id.contactPhoto, R.drawable.icon);
	        }
	        
	        Bundle extras = new Bundle();
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            fillInIntent.setData(contact.getContactUri());
            // Make it possible to distinguish the individual on-click
            // action of a given item
            rv.setOnClickFillInIntent(R.id.contactPhoto, fillInIntent);

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
	    	//Toast.makeText(this.mContext, "contacts refresh " + mAppWidgetId, Toast.LENGTH_LONG).show();
	    }
	}

}
