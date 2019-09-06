package com.example.myfutsal.Model;

import java.util.Date;

public class Comments {

    private String message, tim_id;
    private Date timestamp;

    public Comments(){

    }

    public Comments(String message, String tim_id, Date timestamp) {
        this.message = message;
        this.tim_id = tim_id;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTim_id() {
        return tim_id;
    }

    public void setTim_id(String tim_id) {
        this.tim_id = tim_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}