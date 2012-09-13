package com.girillo.entities;

import android.os.Parcel;
import android.os.Parcelable;


public class Contact implements Comparable<Contact>, Parcelable {
	private String  email;
	private String  name;
	private boolean selected;

	public Contact(Parcel parcel) {
		this.selected = false;
		this.name = parcel.readString();
		this.email = parcel.readString();
	}	
	
	public Contact() {
		this.selected = false;
	}
	
	public Contact(String email, String name) {
		super();
		this.email    = email;
		this.name     = name;
		this.selected = false;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		try {
			return email.equalsIgnoreCase(((Contact) obj).email);
		} catch (Exception ex){
			return false;
		}
	}
	
	@Override
	public String toString() {
		if ((name != null) && (name.length() > 0)){
			return name;
		} else {
			return email;
		}
/*		
		StringBuilder buf = new StringBuilder(80);
		buf.append(email).append(" ");
		if (name != null){
			buf.append("\n").append(name);
		}
		return buf.toString();
*/		
	}

	@Override
	public int compareTo(Contact arg0) {
	   return email.compareTo(arg0.email);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (name == null){
			name = "";
		}
		
		if (email == null){
			email = "";
		}
		
		dest.writeString(name);
		dest.writeString(email);
	}
	
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
		    return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
    };
}

