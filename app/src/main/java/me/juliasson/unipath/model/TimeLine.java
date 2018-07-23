package me.juliasson.unipath.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TimeLine implements Parcelable{
    private String mMessage;
    private Date dDate;
    private String mDate;
    private OrderStatus mStatus;

    public TimeLine() {
    }

    public TimeLine(String mMessage, String mDate, Date dDate, OrderStatus mStatus) {
        this.mMessage = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
        this.dDate = dDate;
    }

    public String getMessage() {
        return mMessage;
    }

    public void semMessage(String message) {
        this.mMessage = message;
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

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected TimeLine(Parcel in) {
        this.mMessage = in.readString();
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
}
