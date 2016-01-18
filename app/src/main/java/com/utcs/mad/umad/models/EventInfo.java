package com.utcs.mad.umad.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * EventInfo Model
 * This is to hold and represent all the information for events in one location
 */
public class EventInfo implements Parcelable {
    private String companyName;
    private String companyWebsite;
    private String startingTime;
    private String endingTime;
    private String room;
    private String sessionName;
    private String speaker;
    private String twitterHandle;
    private String description;
    private ArrayList<String> topics;
    private int capacity;
    private String email;

    public EventInfo(ParseObject parseEvent) {
        try {
            topics = new ArrayList<>();
            for (Object wannaBeString : parseEvent.fetchIfNeeded().getList("topicTags")) {
                topics.add((String) wannaBeString);
            }
            setStartingTime(parseEvent.getDate("startTime"));
            setEndingTime(parseEvent.getDate("endTime"));
            description = parseEvent.fetchIfNeeded().getString("descriptionText");
            sessionName = parseEvent.fetchIfNeeded().getString("name");
            capacity = parseEvent.fetchIfNeeded().getInt("capacity");
            room = parseEvent.fetchIfNeeded().getString("room");
            email = parseEvent.fetchIfNeeded().getString("email");
            speaker = parseEvent.fetchIfNeeded().getString("speaker");

            ParseObject companyParse = parseEvent.getParseObject("company");
            companyName = companyParse.fetchIfNeeded().getString("name");
            companyWebsite = companyParse.fetchIfNeeded().getString("website");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Date startingTime) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.startingTime = dateFormat.format(startingTime);
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Date endingTime) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.endingTime = dateFormat.format(endingTime);
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    /*
     * PARCELABLE
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyName);
        dest.writeString(companyWebsite);
        dest.writeString(startingTime);
        dest.writeString(endingTime);
        dest.writeString(room);
        dest.writeString(sessionName);
        dest.writeString(speaker);
        dest.writeString(twitterHandle);
        dest.writeString(description);
        dest.writeInt(topics.size());
        for(String topic : topics) {
            dest.writeString(topic);
        }
        dest.writeInt(capacity);
        dest.writeString(email);
    }

    private void readFromParcel(Parcel in) {
        companyName = in.readString();
        companyWebsite = in.readString();
        startingTime = in.readString();
        endingTime = in.readString();
        room = in.readString();
        sessionName = in.readString();
        speaker = in.readString();
        twitterHandle = in.readString();
        description = in.readString();
        int topicSize = in.readInt();
        topics = new ArrayList<>();
        for (int topic = 0; topic < topicSize; topic++) {
            topics.add(in.readString());
        }
        capacity = in.readInt();
        email = in.readString();
    }

    private EventInfo(Parcel in) {
        readFromParcel(in);
    }

    public static Parcelable.Creator<EventInfo> CREATOR = new Parcelable.Creator<EventInfo>() {
        public EventInfo createFromParcel(Parcel source) {
            return new EventInfo(source);
        }

        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };
}
