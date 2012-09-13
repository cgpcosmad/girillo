package com.girillo.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representa un archivo (normalente multimedia) que se adjuntará al correo.
 * @author Carlos García. cgpcosmad@gmail.com
 */
public class Attachement implements Parcelable {
	public enum AttachementType { Audio, Image, Video, Other }
	
	private String			fileName;
	private String	  		fullPath;
	private AttachementType type;
	
	public Attachement(Parcel parcel) {
		this.fileName  = parcel.readString();
		this.fullPath  = parcel.readString();
		
		int typeCode = parcel.readInt();
		if (typeCode == 1){
			this.type = AttachementType.Audio;
		} else if (typeCode == 1){
			this.type = AttachementType.Image;
		} else {
			this.type = AttachementType.Other;
		}
	}	
	
	public Attachement(String fullFilePath, AttachementType type){
		this.fullPath = fullFilePath;
		this.type	  = type;
		
		int pos = fullFilePath.lastIndexOf("/");
		if (pos == -1){
			this.fileName = fullFilePath;
		} else {
			this.fileName = fullFilePath.substring(pos + 1);	
		}
	}

	public String getFullPath() {
		return fullPath;
	}
	
	public AttachementType getAttachementType(){
		return type;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullPath == null) ? 0 : fullPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Attachement other = (Attachement) obj;
			return this.fullPath.equals(other.fullPath);
		} catch (Exception ex){
			return false;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (fileName == null){fileName = "";}
		if (fileName == null){fileName = "";}
		
		dest.writeString(fileName);
		dest.writeString(fullPath);
		
		if (AttachementType.Audio.equals(type)){
			dest.writeInt(1);	
		} else if (AttachementType.Image.equals(type)){
			dest.writeInt(2);
		} else {
			dest.writeInt(3);	
		}
	}
	
    public static final Parcelable.Creator<Attachement> CREATOR = new Parcelable.Creator<Attachement>() {
		public Attachement createFromParcel(Parcel in) {
		    return new Attachement(in);
		}

		public Attachement[] newArray(int size) {
			return new Attachement[size];
		}
    };	
}
