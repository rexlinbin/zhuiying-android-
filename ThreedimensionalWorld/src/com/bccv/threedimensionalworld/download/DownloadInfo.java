package com.bccv.threedimensionalworld.download;

import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

import java.io.File;

/**
 * Author: wyouflf
 * Date: 13-11-10
 * Time: 下午8:11
 */
public class DownloadInfo {

    public DownloadInfo() {
    }

    private long id;

    @Transient
    private HttpHandler<File> handler;

    private HttpHandler.State state;

    private String downloadUrl;

    private String fileName;

    private String fileSavePath;

    private long progress;

    private long fileLength;

    private boolean autoResume;

    private boolean autoRename;
    
    private String app_idString;

    private String app_urlString;
    private String app_nameString;
    private String packageString;
    private float app_score_interfaceString;
    private String app_introString;

    
    
    public float getApp_score_interfaceString() {
		return app_score_interfaceString;
	}

	public void setApp_score_interfaceString(float app_score_interfaceString) {
		this.app_score_interfaceString = app_score_interfaceString;
	}

	public String getApp_introString() {
		return app_introString;
	}

	public void setApp_introString(String app_introString) {
		this.app_introString = app_introString;
	}

	public String getApp_nameString() {
		return app_nameString;
	}

	public void setApp_nameString(String app_nameString) {
		this.app_nameString = app_nameString;
	}

	public String getPackageString() {
		return packageString;
	}

	public void setPackageString(String packageString) {
		this.packageString = packageString;
	}

	public String getApp_urlString() {
		return app_urlString;
	}

	public void setApp_urlString(String app_urlString) {
		this.app_urlString = app_urlString;
	}

	public String getApp_idString() {
		return app_idString;
	}

	public void setApp_idString(String app_idString) {
		this.app_idString = app_idString;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HttpHandler<File> getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler<File> handler) {
        this.handler = handler;
    }

    public HttpHandler.State getState() {
        return state;
    }

    public void setState(HttpHandler.State state) {
        this.state = state;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
    }

    public boolean isAutoRename() {
        return autoRename;
    }

    public void setAutoRename(boolean autoRename) {
        this.autoRename = autoRename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadInfo)) return false;

        DownloadInfo that = (DownloadInfo) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
