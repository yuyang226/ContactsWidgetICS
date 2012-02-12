/**
 * 
 */
package com.gmail.yuyang226.contactswidget.models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class Contact {
	private long contactId;
	private Uri contactUri;
	private String displayName;
	private String photoUri;
	private boolean isFavourite;
	private Bitmap photo;

	/**
	 * 
	 */
	public Contact() {
		super();
		//currently we only support favourite contacts
		isFavourite = true;
	}
	
	/**
	 * @return the contactUri
	 */
	public Uri getContactUri() {
		return contactUri;
	}

	/**
	 * @param contactUri the contactUri to set
	 */
	public void setContactUri(Uri contactUri) {
		this.contactUri = contactUri;
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

	/**
	 * @return the isFavourite
	 */
	public boolean isFavourite() {
		return isFavourite;
	}

	/**
	 * @param isFavourite the isFavourite to set
	 */
	public void setFavourite(boolean isFavourite) {
		this.isFavourite = isFavourite;
	}

	/**
	 * @return the photo
	 */
	public Bitmap getPhoto() {
		return photo;
	}

	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	
	

}
