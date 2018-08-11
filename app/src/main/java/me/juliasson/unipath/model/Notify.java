package me.juliasson.unipath.model;

import android.os.Parcelable;

import org.parceler.Parcel;

import java.util.Objects;

@Parcel
public class Notify implements Parcelable{
    public String title;
    public String body;

    public Notify() {
        this.title = "title";
        this.body = "body";
    }

    public Notify(String title, String body) {
        this.title = title;
        this.body = body;
    }

    protected Notify(android.os.Parcel in) {
        title = in.readString();
        body = in.readString();
    }

    public static final Creator<Notify> CREATOR = new Creator<Notify>() {
        @Override
        public Notify createFromParcel(android.os.Parcel in) {
            return new Notify(in);
        }

        @Override
        public Notify[] newArray(int size) {
            return new Notify[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title= title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(body);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notify notify = (Notify) o;
        return Objects.equals(body, notify.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
