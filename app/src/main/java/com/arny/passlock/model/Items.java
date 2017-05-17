package com.arny.passlock.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.arny.arnylib.utils.Utility;

public class Items implements Parcelable {

    private String title,link;
    private int ID,parent,type;
    private long datetime;

    public Items() {
    }

    private Items(Parcel in) {
        title = in.readString();
        link = in.readString();
        ID = in.readInt();
        type = in.readInt();
        parent = in.readInt();
        datetime = in.readLong();
    }

    public static final Creator<Items> CREATOR = new Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel in) {
            return new Items(in);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeInt(ID);
        dest.writeInt(parent);
        dest.writeInt(type);
        dest.writeLong(datetime);
    }

    @Override
    public String toString() {
        return "\nid=" + this.getID() + "; title=" +
                this.getTitle() + "; link=" + this.getLink()
                + "; parent=" + this.getParent() + "; type="
                + this.getType() + "; dateTime = " + Utility.getDateTime(datetime);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
