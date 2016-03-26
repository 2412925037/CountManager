package com.tower.countmanager.bean;

import java.io.Serializable;

public class ImageItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String longitude;
	String latitude;
	String DOC_NAME;
	String DOC_ID;
	String DOC_PATH;


	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longtitude) {
		this.longitude = longtitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getDoc_name() {
		return DOC_NAME;
	}

	public void setDoc_name(String doc_name) {
		this.DOC_NAME = doc_name;
	}

	public String getDoc_id() {
		return DOC_ID;
	}

	public void setDoc_id(String doc_id) {
		this.DOC_ID = doc_id;
	}

	public String getDoc_path() {
		return DOC_PATH;
	}

	public void setDoc_path(String doc_path) {
		this.DOC_PATH = doc_path;
	}
}
