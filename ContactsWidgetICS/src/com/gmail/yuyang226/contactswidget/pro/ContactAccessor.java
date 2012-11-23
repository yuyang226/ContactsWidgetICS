/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Directory;
import android.util.LruCache;

import com.gmail.yuyang226.contactswidget.pro.models.Contact;
import com.gmail.yuyang226.contactswidget.pro.models.ContactDirectory;
import com.gmail.yuyang226.contactswidget.pro.models.ContactGroup;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 * 
 */
public class ContactAccessor {
	private static final String STARRED_IN_ANDROID = "Starred in Android"; //$NON-NLS-1$
	
	private static final LruCache<String, Bitmap> IMAGES_CACHE;
	private static final int CACHE_SIZE = 8 * 1024 * 1024; // 4MiB
	
	static {
		IMAGES_CACHE = new LruCache<String, Bitmap>(CACHE_SIZE) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
	}

	/**
	 * 
	 */
	public ContactAccessor() {
		super();
	}

	public List<ContactDirectory> getContactDirectories(Context context) {
		final List<ContactDirectory> directories = new ArrayList<ContactDirectory>();
		// Run query
		Uri uri = ContactsContract.Directory.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Directory._ID,
				ContactsContract.Directory.ACCOUNT_NAME,
				ContactsContract.Directory.ACCOUNT_TYPE, };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Directory.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC"; //$NON-NLS-1$

		CursorLoader loader = new CursorLoader(context, uri, projection,
				selection, selectionArgs, sortOrder);
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
				long directoryId = cursor.getLong(0);
				String accountName = cursor.getString(1);
				if (accountName == null && directoryId == Directory.DEFAULT) {
					accountName = "Local"; //$NON-NLS-1$
				}
				String accountType = cursor.getString(2);
				ContactDirectory directory = new ContactDirectory(directoryId,
						accountName, accountType);
				directories.add(directory);
				cursor.moveToNext();
			}
		} finally {
			loader.stopLoading();
			if (cursor != null) {
				cursor.close();
			}
		}
		return directories;
	}

	public Collection<ContactGroup> getContactGroups(Context context,
			long directoryId) {
		Comparator<ContactGroup> comparator = new Comparator<ContactGroup>() {

			@Override
			public int compare(ContactGroup g1, ContactGroup g2) {
				if (STARRED_IN_ANDROID.equalsIgnoreCase(g1.getTitle())) {
					return -1;
				} else if (STARRED_IN_ANDROID.equalsIgnoreCase(g2.getTitle())) {
					return 1;
				}
				return g1.getTitle().compareTo(g2.getTitle());
			}

		};
		final Collection<ContactGroup> groups = new TreeSet<ContactGroup>(
				comparator);
		// Run query
		Uri uri = ContactsContract.Groups.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Groups._ID,
				ContactsContract.Groups.ACCOUNT_NAME,
				ContactsContract.Groups.ACCOUNT_TYPE,
				ContactsContract.Groups.TITLE, };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Groups.TITLE
				+ " COLLATE LOCALIZED ASC"; //$NON-NLS-1$

		CursorLoader loader = new CursorLoader(context, uri, projection,
				selection, selectionArgs, sortOrder);
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
				long groupId = cursor.getLong(0);
				String accountName = cursor.getString(1);
				String accountType = cursor.getString(2);
				String title = cursor.getString(3);
				if (title.equalsIgnoreCase("My Contacts")) { //$NON-NLS-1$
					// we dont want to handle My Contacts
					cursor.moveToNext();
					continue;
				}
				ContactGroup group = new ContactGroup(groupId, accountName,
						accountType, title);
				groups.add(group);
				cursor.moveToNext();
			}
		} finally {
			loader.stopLoading();
			if (cursor != null) {
				cursor.close();
			}
		}
		return groups;
	}

	/**
	 * Obtains the contact list for the currently selected account.
	 * 
	 * @return A cursor for for accessing the contact list.
	 */
	public List<Contact> getContacts(ContentResolver contentResolver,
			Context context, int appWidgetId, Rect size) {
		final List<Contact> contacts = new ArrayList<Contact>();
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.PHOTO_URI, };

		String selection = null;

		String groupId = ContactsWidgetConfigurationActivity
				.loadSelectionString(context, appWidgetId);
		String sortOrder = ContactsWidgetConfigurationActivity
				.loadSortingString(context, appWidgetId);
		if (ContactsWidgetConfigurationActivity.CONTACT_STARRED.equals(groupId)) {
			selection = groupId;
		} else {
			return getContactsByGroup(contentResolver, context, appWidgetId,
					groupId, sortOrder, size);
		}
		String[] selectionArgs = null;

		boolean showHighRes = ContactsWidgetConfigurationActivity
				.loadShowHighRes(context, appWidgetId);
		CursorLoader loader = new CursorLoader(context, uri, projection,
				selection, selectionArgs, sortOrder);
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
				contact.setDisplayName(trimDisplayName(displayName));
				contact.setPhotoUri(photoUri);
				contact.setContactUri(ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI, contactId));
				contacts.add(contact);
				if (photoUri != null && photoUri.length() > 0) {
					contact.setPhoto(loadContactPhoto(contentResolver,
							contact.getContactUri(), showHighRes, size));
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

	private List<Contact> getContactsByGroup(ContentResolver contentResolver,
			Context context, int appWidgetId, String groupID, String sortOrder, 
			Rect size) {
		final List<Contact> contacts = new ArrayList<Contact>();
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
				ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };
		String selection = new StringBuffer(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)
				.append("=").append(groupID).toString(); //$NON-NLS-1$
		boolean showHighRes = ContactsWidgetConfigurationActivity
				.loadShowHighRes(context, appWidgetId);
		String[] selectionArgs = null;

		CursorLoader loader = new CursorLoader(context, uri, projection,
				selection, selectionArgs, sortOrder);
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
//				long groupRowId = cursor.getLong(0);
				long contactId = cursor.getLong(1);

				contacts.add(loadContactById(contentResolver, context,
						contactId, sortOrder, showHighRes, size));
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

	private Contact loadContactById(ContentResolver contentResolver,
			Context context, long contactId, String sortOrder, boolean showHighRes, 
			Rect size) {
		Contact contact = new Contact();
		contact.setContactId(contactId);
		Uri contactUri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		contact.setContactUri(contactUri);

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.PHOTO_URI, };
		String selection = new StringBuffer(Contacts._ID).append("=?").toString(); //$NON-NLS-1$
		CursorLoader loader = new CursorLoader(context, uri, projection,
				selection, new String[]{String.valueOf(contactId)}, sortOrder);
		
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			if (cursor.moveToFirst()) {
				String displayName = cursor.getString(1);
				String photoUri = cursor.getString(2);
				contact.setContactId(contactId);
				contact.setDisplayName(trimDisplayName(displayName));
				contact.setPhotoUri(photoUri);
				if (photoUri != null && photoUri.length() > 0) {
					contact.setPhoto(loadContactPhoto(contentResolver,
							contact.getContactUri(), showHighRes, size));
				}
			}
		} finally {
			loader.stopLoading();
			if (cursor != null) {
				cursor.close();
			}
		}
		return contact;
	}

	private Bitmap loadContactPhoto(ContentResolver contentResolver, Uri uri, boolean showHighRes, 
			Rect size) {
		final String imageKey = uri.toString() + showHighRes + size;
		Bitmap pic = IMAGES_CACHE.get(imageKey);
		if (pic == null) {
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(contentResolver, uri, showHighRes);
			if (input == null) {
				return null;
			}
			pic = BitmapFactory.decodeStream(input);
			if (showHighRes && size != null) {
				//performance enhancement
				pic = Bitmap.createScaledBitmap(
						pic, size.width(), size.height(), false);
			}
			
			IMAGES_CACHE.put(imageKey, pic);
		}
		return pic;
	}
	
	/**
	 * Trim the display name if it is too long
	 * @param displayName
	 * @return
	 */
	public static String trimDisplayName(String displayName) {
		if (displayName != null && displayName.length() > 9) {
			displayName = new StringBuffer(displayName.substring(0, 8)).append("..").toString(); //$NON-NLS-1$
		}
		return displayName;
	}

}
