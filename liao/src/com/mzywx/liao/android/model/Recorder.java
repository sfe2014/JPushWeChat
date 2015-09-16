package com.mzywx.liao.android.model;

public class Recorder {
    private float seconds;
    private String filePath;

    public Recorder(float seconds, String filePath) {
        super();
        this.seconds = seconds;
        this.filePath = filePath;
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
        return "Recorder [seconds=" + seconds + ", filePath=" + filePath + "]";
    }

}
