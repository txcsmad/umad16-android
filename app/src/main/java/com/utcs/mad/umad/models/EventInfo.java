package com.utcs.mad.umad.models;

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
public class EventInfo {
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
    private int regTime;

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

    public int getRegTime() { return regTime; }

    public void setRegTime(int regTime) { this.regTime = regTime; }
}
