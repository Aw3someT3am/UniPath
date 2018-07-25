package me.juliasson.unipath.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

public class TimeLine implements Parcelable{
    private Date dDate;
    private String mDate;
    private OrderStatus mStatus;

    public TimeLine() {
    }

    public TimeLine(String mDate, Date dDate, OrderStatus mStatus) {
        this.mDate = mDate;
        this.mStatus = mStatus;
        this.dDate = dDate;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    public Date getDDate() {
        return dDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected TimeLine(Parcel in) {
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Parcelable.Creator<TimeLine> CREATOR = new Parcelable.Creator<TimeLine>() {
        @Override
        public TimeLine createFromParcel(Parcel source) {
            return new TimeLine(source);
        }

        @Override
        public TimeLine[] newArray(int size) {
            return new TimeLine[size];
        }
    };

    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeLine timeLine = (TimeLine) o;
        return Objects.equals(dDate, timeLine.dDate) &&
                Objects.equals(mDate, timeLine.mDate);
    }

    @SuppressLint("NewApi")
    @Override
    public int hashCode() {
        return Objects.hash(dDate, mDate);
    }
}
