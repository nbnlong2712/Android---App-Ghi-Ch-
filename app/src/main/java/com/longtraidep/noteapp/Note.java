package com.longtraidep.noteapp;

import java.time.DateTimeException;
import java.util.Date;
import java.util.UUID;

public class Note {
    private UUID mId;
    private String mTitle;
    private String mContent;
    private Date mDateTime;
    private String mTag;

    public Note()
    {
        this(UUID.randomUUID());
    }
    public Note(UUID id)
    {
        mId = id;
        mDateTime = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public Date getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Date dateTime) {
        mDateTime = dateTime;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }
}
