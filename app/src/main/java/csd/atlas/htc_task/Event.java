package csd.atlas.htc_task;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by FRAME on 2/25/2018.
 */

public class Event implements Comparable<Event> {
    private long mGid;
    private double longitude;
    private double latitude;
    private Date mStartDate;
    private Date mEndDate;
    private String mName;
    private String mThumbnail;
    private String mDescription;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getGid() {
        return mGid;
    }

    public void setGid(long gid) {
        mGid = gid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    @Override
    public int compareTo(@NonNull Event event) {
        if (getStartDate() == null || event.getStartDate() == null)
            return 0;
        return getStartDate().compareTo(event.getStartDate());
    }

}
