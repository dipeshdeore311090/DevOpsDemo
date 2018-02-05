package com.hackathon;

import java.util.List;

/**
 * Created by dipeshd on 11/14/2017.
 */

public class PollDetailsModel {

    private String userID;
    private String questionText;
    private String pollType;
    private List<String> contacts;
    private List<GridModel> options;
    private String id;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPollType() {
        return pollType;
    }

    public void setPollType(String pollType) {
        this.pollType = pollType;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public List<GridModel> getOptions() {
        return options;
    }

    public void setOptions(List<GridModel> options) {
        this.options = options;
    }
}
