/**
 * 
 */
package com.gmail.yuyang226.contactswidget.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactItem implements Parcelable {

	
	private long contactId;
	private String displayName;
	private String photoUri;
	
	/**
	 * 
	 */
	public ContactItem() {
		super();
	}

	/**
	 * 
	 */
	public ContactItem(Parcel in) {
		super();
	}
	
	/**
	 * @param contactId
	 * @param displayName
	 * @param photoUri
	 */
	public ContactItem(long contactId, String displayName, String photoUri) {
		super();
		this.contactId = contactId;
		this.displayName = displayName;
		this.photoUri = photoUri;
	}

	/**
	 * @return the contactId
	 */
	public long getContactId() {
		return contactId;
	}

	/**
	 * @param contactId the contactId to set
	 */
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the photoUri
	 */
	public String getPhotoUri() {
		return photoUri;
	}

	/**
	 * @param photoUri the photoUri to set
	 */
	public void setPhotoUri(String photoUri) {
		this.photoUri = photoUri;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.contactId);
		dest.writeString(this.displayName);
		dest.writeString(this.photoUri != null ? this.photoUri : ""); //$NON-NLS-1$
	}
	
	public void readFromParcel(Parcel in) {
        this.contactId = in.readLong();
        this.displayName = in.readString();
        String photoUri = in.readString();
        if (photoUri.length() > 0) {
        	this.photoUri = photoUri;
        }
    }
	
	public static final Parcelable.Creator<ContactItem> CREATOR
	= new Parcelable.Creator<ContactItem>() {
		public ContactItem createFromParcel(Parcel in) {
			return new ContactItem(in);
		}

		public ContactItem[] newArray(int size) {
			return new ContactItem[size];
		}
	};
	
	

}
