/**
 * 
 */
package com.gmail.yuyang226.contactswidget.models;

/**
 * @author yayu
 *
 */
public class ContactGroup {
	private long groupId;
	private String accountName;
	private String accountType;
	private String title;

	/**
	 * 
	 */
	public ContactGroup() {
		super();
	}

	/**
	 * @param groupId
	 * @param accountName
	 * @param accountType
	 * @param title
	 */
	public ContactGroup(long groupId, String accountName, String accountType, String title) {
		super();
		this.groupId = groupId;
		this.accountName = accountName;
		this.accountType = accountType;
		this.title = title;
	}

	/**
	 * @return the groupId
	 */
	public long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
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

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	

}
