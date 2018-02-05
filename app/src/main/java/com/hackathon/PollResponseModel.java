package com.hackathon;

/**
 * Created by dipeshd on 11/15/2017.
 */

public class PollResponseModel {

    private String PollId;
    private String selectedOptionId;
    private String comment;
    private String responderId;

    public String getPollId() {
        return PollId;
    }

    public void setPollId(String pollId) {
        PollId = pollId;
    }

    public String getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(String selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResponderId() {
        return responderId;
    }

    public void setResponderId(String responderId) {
        this.responderId = responderId;
    }
}
