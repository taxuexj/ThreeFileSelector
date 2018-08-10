package com.three.fileselector.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liangqida on 2018/08/08.
 */
public class FileInfoBean implements Parcelable {

    private String fileName;//文件名字
    private String filePath;//文件路径
    private long fileSize;//文件大小
    private String time;//文件日期
    private String type;

    public String getFilePath() {
        return filePath == null ? "" : filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.filePath);
        dest.writeLong(this.fileSize);
        dest.writeString(this.time);
        dest.writeString(this.type);
    }

    public FileInfoBean() {
    }

    protected FileInfoBean(Parcel in) {
        this.fileName = in.readString();
        this.filePath = in.readString();
        this.fileSize = in.readLong();
        this.time = in.readString();
        this.type = in.readString();
    }

    public static final Creator<FileInfoBean> CREATOR = new Creator<FileInfoBean>() {
        @Override
        public FileInfoBean createFromParcel(Parcel source) {
            return new FileInfoBean(source);
        }

        @Override
        public FileInfoBean[] newArray(int size) {
            return new FileInfoBean[size];
        }
    };
}
