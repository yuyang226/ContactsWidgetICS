/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.gmail.yuyang226.contactswidget.pro.models.PhoneNumber;
import com.gmail.yuyang226.contactswidget.pro.ui.ContactsWidgetConfigurationActivity;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 * 
 */
public class ContactAccessor {
	private static final String STARRED_CONTACTS_ENG = "Starred in Android"; //$NON-NLS-1$
	private static final LruCache<String, Bitmap> IMAGES_CACHE;
	private static final int CACHE_SIZE = 8 * 1024 * 1024; // 8MiB
	
	private static final String[] CONTACT_PROJECTION = { 
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.PHOTO_URI,
			ContactsContract.Contacts.HAS_PHONE_NUMBER};
	
	private static final String[] CONTACTS_BY_GROUP_PROJECTION = {
		ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
		ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };
	
	private static final String CONTACT_SELECTION = Contacts._ID + "=?"; //$NON-NLS-1$
	
	private static final String[] PHONENUNBER_PROJECTION = { 
			ContactsContract.CommonDataKinds.Phone._ID,
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.IS_PRIMARY};
	
	private static final String PHONENUMBER_SELECTION = 
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"; //$NON-NLS-1$
	private static final String PHONENUMBER_SORTSTRING = ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED + " DESC"; //$NON-NLS-1$
	
	private static final Comparator<PhoneNumber> PHONENUMBER_COMPARATOR = new Comparator<PhoneNumber>() {
		@Override
		public int compare(PhoneNumber n1, PhoneNumber n2) {
			if (n1.isPrimary()) {
				return -1;
			} else if (n2.isPrimary()) {
				return 1;
			} else if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == n1.getType()) {
				return -1;
			} else if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == n2.getType()) {
				return 1;
			} else if (ContactsContract.CommonDataKinds.Phone.TYPE_MAIN == n1.getType()) {
				return -1;
			} else if (ContactsContract.CommonDataKinds.Phone.TYPE_MAIN == n2.getType()) {
				return 1;
			}
			return 0;
		}
	};
	
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
	
	static void clearImageCache() {
		IMAGES_CACHE.evictAll();
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
		final String starredContacts = context.getString(R.string.starredContacts);
		final String myContacts = context.getString(R.string.myContacts);
		Comparator<ContactGroup> comparator = new Comparator<ContactGroup>() {

			@Override
			public int compare(ContactGroup g1, ContactGroup g2) {
				if (myContacts.equalsIgnoreCase(g1.getTitle())) {
					return -1;
				} else if (myContacts.equalsIgnoreCase(g2.getTitle())) {
					return 1;
				} else if (starredContacts.equalsIgnoreCase(g1.getTitle())
						|| STARRED_CONTACTS_ENG.equalsIgnoreCase(g1.getTitle())) {
					return -1;
				} else if (starredContacts.equalsIgnoreCase(g2.getTitle())
						|| STARRED_CONTACTS_ENG.equalsIgnoreCase(g2.getTitle())) {
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
		boolean foundStarredGroup = false;
		boolean foundMyContacts = false;
		
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			if (cursor == null) {
				return groups;
			}
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
				long groupId = cursor.getLong(0);
				String accountName = cursor.getString(1);
				String accountType = cursor.getString(2);
				String title = cursor.getString(3);
				if (title == null || title.equalsIgnoreCase(myContacts)) { //$NON-NLS-1$
					// the title is null and we don't want to handle My Contacts
					if (foundMyContacts) {
						//two my contacts appear
						cursor.moveToNext();
						continue;
					}
					foundMyContacts = true;
					groupId = ContactsWidgetConfigurationActivity.CONTACT_MY_CONTACTS_GROUP_ID;
					title = myContacts;
				} else if (title.equalsIgnoreCase(starredContacts)
						|| STARRED_CONTACTS_ENG.equalsIgnoreCase(title)) {
					foundStarredGroup = true;
					groupId = ContactsWidgetConfigurationActivity.CONTACT_STARRED_GROUP_ID;
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
		if (!foundStarredGroup) {
			//not containing the starredContacts
			ContactGroup group = new ContactGroup(
					ContactsWidgetConfigurationActivity.CONTACT_STARRED_GROUP_ID, starredContacts,
					null, starredContacts);
			groups.add(group);
		}
		if (!foundMyContacts) {
			//not containing the my contacts group
			ContactGroup group = new ContactGroup(
					ContactsWidgetConfigurationActivity.CONTACT_MY_CONTACTS_GROUP_ID, myContacts,
					null, myContacts);
			groups.add(group);
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
		String selection = null;

		String groupId = ContactsWidgetConfigurationActivity
				.loadSelectionString(context, appWidgetId);
		String sortOrder = ContactsWidgetConfigurationActivity
				.loadSortingString(context, appWidgetId);
		int maxNumber = ContactsWidgetConfigurationActivity.loadMaxNumbers(context, appWidgetId);
		if (ContactsWidgetConfigurationActivity.CONTACT_STARRED.equals(groupId)) {
			selection = groupId;
		} else  if (groupId == "") { //$NON-NLS-1$
			//all contacts;
			selection = groupId;
		} else {
			return getContactsByGroup(contentResolver, context, appWidgetId,
					groupId, sortOrder, size, maxNumber);
		}
		String[] selectionArgs = null;
		
		
		boolean supportDirectDial = ContactsWidgetConfigurationActivity
				.loadSupportDirectDial(context, appWidgetId);

		boolean showHighRes = ContactsWidgetConfigurationActivity
				.loadShowHighRes(context, appWidgetId);
		CursorLoader loader = new CursorLoader(context, uri, CONTACT_PROJECTION,
				selection, selectionArgs, sortOrder);
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			if (cursor == null) {
				return contacts;
			}
			cursor.moveToFirst();
			int count = 0;
			while (cursor.isAfterLast() == false && count < maxNumber) {
				long contactId = cursor.getLong(0);
				String displayName = cursor.getString(1);
				String photoUri = cursor.getString(2);
				Contact contact = new Contact();
				contact.setContactId(contactId);
				contact.setDisplayName(displayName);
				contact.setPhotoUri(photoUri);
				contact.setContactUri(ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI, contactId));
				if (photoUri != null && photoUri.length() > 0) {
					contact.setPhoto(loadContactPhoto(contentResolver,
							contact.getContactUri(), showHighRes, size));
				}
				
				int hasPhoneNumber = cursor.getInt(3);
				if (supportDirectDial && hasPhoneNumber > 0) {
					contact.setPhoneNumbers(loadPhoneNumbers(
							contentResolver, context, contactId));
				}
				
				if (!supportDirectDial || hasPhoneNumber > 0) {
					//either not support direct dial, or has phone number
					contacts.add(contact);
					count++;
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
	
	private List<PhoneNumber> loadPhoneNumbers(ContentResolver contentResolver, 
			Context context, long contactId) {
		Cursor pCur = null;
		CursorLoader loader = new CursorLoader(context, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				PHONENUNBER_PROJECTION,
				PHONENUMBER_SELECTION, new String[] { String.valueOf(contactId) }, PHONENUMBER_SORTSTRING);
		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		try {
			loader.startLoading();
			pCur = loader.loadInBackground();
			if (pCur == null) {
				return phoneNumbers;
			}
			pCur.moveToFirst();
			while (pCur.isAfterLast() == false) {
				int type = pCur.getInt(1);
				String phone = pCur.getString(2);
				int primary = pCur.getInt(3);
				if (primary > 0) {
					//primary number will be used first
					phoneNumbers.add(0, new PhoneNumber(type, phone, true));
				} else {
					phoneNumbers.add(new PhoneNumber(type, phone, false));
				}
				pCur.moveToNext();
			}
		} finally {
			loader.stopLoading();
        	if (pCur != null) {
        		pCur.close();
        	}
        }
		
		Collections.sort(phoneNumbers, PHONENUMBER_COMPARATOR);
		return phoneNumbers;
	}

	private List<Contact> getContactsByGroup(ContentResolver contentResolver,
			Context context, int appWidgetId, String groupID, String sortOrder, 
			Rect size, int maxNumber) {
		final List<Contact> contacts = new ArrayList<Contact>();
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String selection = new StringBuffer(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)
				.append("=").append(groupID).toString(); //$NON-NLS-1$
		boolean showHighRes = ContactsWidgetConfigurationActivity
				.loadShowHighRes(context, appWidgetId);
		boolean supportDirectDial = ContactsWidgetConfigurationActivity
				.loadSupportDirectDial(context, appWidgetId);
		String[] selectionArgs = null;

		CursorLoader loader = new CursorLoader(context, uri, CONTACTS_BY_GROUP_PROJECTION,
				selection, selectionArgs, sortOrder);
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			if (cursor == null) {
				return contacts;
			}
			cursor.moveToFirst();
			int count = 0;
			while (cursor.isAfterLast() == false
					&& count < maxNumber) {
//				long groupRowId = cursor.getLong(0);
				long contactId = cursor.getLong(1);
				Contact contact = loadContactById(contentResolver, context,
						contactId, sortOrder, showHighRes, supportDirectDial, size);
				if (contact != null) {
					contacts.add(contact);
					count++;
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

	private Contact loadContactById(ContentResolver contentResolver,
			Context context, long contactId, String sortOrder, boolean showHighRes, 
			boolean supportDirectDial, Rect size) {
		Contact contact = new Contact();
		contact.setContactId(contactId);
		Uri contactUri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		contact.setContactUri(contactUri);

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		CursorLoader loader = new CursorLoader(context, uri, CONTACT_PROJECTION,
				CONTACT_SELECTION, new String[]{String.valueOf(contactId)}, sortOrder);
		
		Cursor cursor = null;
		try {
			loader.startLoading();
			cursor = loader.loadInBackground();
			if (cursor == null) {
				//did not find the contact
				return null;
			} else if (cursor.moveToFirst()) {
				String displayName = cursor.getString(1);
				String photoUri = cursor.getString(2);
				contact.setContactId(contactId);
				contact.setDisplayName(displayName);
				contact.setPhotoUri(photoUri);
				if (photoUri != null && photoUri.length() > 0) {
					contact.setPhoto(loadContactPhoto(contentResolver,
							contact.getContactUri(), showHighRes, size));
				}
				int hasPhoneNumber = cursor.getInt(3);
				if (supportDirectDial) {
					if (hasPhoneNumber > 0) {
						contact.setPhoneNumbers(loadPhoneNumbers(
								contentResolver, context, contactId));
					} else {
						//users want direct dial but this contact has no phone number
						return null;
					}
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
	
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)reqHeight);
			} else {
				inSampleSize = Math.round((float)width / (float)reqWidth);
			}
		}
		return inSampleSize;
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

			if (showHighRes && size != null) {
				//performance enhancement
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(input, null, options);
				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, size.width(), size.height());
				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;
				//opening another inputstream because the first one has been auto closed
				input = ContactsContract.Contacts
						.openContactPhotoInputStream(contentResolver, uri, showHighRes);
				if (input == null) {
					return null;
				}
				pic = BitmapFactory.decodeStream(input, null, options);
			} else {
				pic = BitmapFactory.decodeStream(input);
			}
			if (pic != null) {
				IMAGES_CACHE.put(imageKey, pic);
			}
		}
		return pic;
	}
	
}
