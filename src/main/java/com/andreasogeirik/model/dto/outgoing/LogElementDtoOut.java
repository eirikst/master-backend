package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.ContentType;
import com.andreasogeirik.model.entities.LogElement;

import java.util.Date;

/**
 * Created by eirikstadheim on 08/02/16.
 */
public class LogElementDtoOut {
    private int id;
    private Date time;
    private String content;
    private int type;
    private int contentId;


    public LogElementDtoOut() {
    }

    public LogElementDtoOut(LogElement element) {
        this.id = element.getId();
        this.time = element.getTime();
        this.content = element.getContent();
        this.type = element.getType().getNumber();
        this.contentId = element.getContentId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
}
