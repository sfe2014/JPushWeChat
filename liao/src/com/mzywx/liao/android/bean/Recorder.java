package com.mzywx.liao.android.bean;

import org.litepal.crud.DataSupport;

public class Recorder extends DataSupport {
	private int id;
	private float seconds;
	private String filePath;
	
	public Recorder(float seconds, String filePath) {
		super();
		this.seconds = seconds;
		this.filePath = filePath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getSeconds() {
		return seconds;
	}

	public void setSeconds(float seconds) {
		this.seconds = seconds;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "Recorder [id=" + id + ", seconds=" + seconds + ", filePath="
				+ filePath + "]";
	}

}
