/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.models;

/**
 * @author yayu
 *
 */
public class ContactGroup {
	private long groupId;
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
	public ContactGroup(long groupId, String accountType, String title) {
		super();
		this.groupId = groupId;
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
