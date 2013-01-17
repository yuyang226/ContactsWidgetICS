/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.models;

/**
 * @author yayu
 *
 */
public class PhoneNumber {
	private int type;
	private String number;
	private boolean primary = false;

	/**
	 * 
	 */
	public PhoneNumber() {
		super();
	}

	/**
	 * @param type
	 * @param number
	 */
	public PhoneNumber(int type, String number) {
		super();
		this.type = type;
		this.number = number;
	}
	
	public PhoneNumber(int type, String number, boolean primary) {
		super();
		this.type = type;
		this.number = number;
		this.primary = primary;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	
	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + type;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhoneNumber other = (PhoneNumber) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PhoneNumber [type=" + type + ", number=" + number
				+ ", primary=" + primary + "]";
	}

}
