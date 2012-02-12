/**
 * 
 */
package com.gmail.yuyang226.contactswidget.models;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class ContactDirectory {
	private long directoryId;
	private String accountName;
	private String accountType;

	/**
	 * 
	 */
	public ContactDirectory() {
		super();
	}

	/**
	 * @param directoryId
	 * @param accountName
	 * @param accountType
	 */
	public ContactDirectory(long directoryId, 
			String accountName, String accountType) {
		super();
		this.directoryId = directoryId;
		this.accountName = accountName;
		this.accountType = accountType;
	}

	/**
	 * @return the directoryId
	 */
	public long getDirectoryId() {
		return directoryId;
	}

	/**
	 * @param directoryId the directoryId to set
	 */
	public void setDirectoryId(long directoryId) {
		this.directoryId = directoryId;
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
}
