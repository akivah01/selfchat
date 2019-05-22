package com.example.self_chat;

public class MyMessage {
    private String Id,TimeStamp, Text;

    public MyMessage(String Id, String time, String text)
    {
        this.Text = text;
        this.TimeStamp = time;
        this.Id = Id;
    }
    @Override
    public String toString()
    {
        return this.TimeStamp + ":" + Text;
    }
    public String getMsgText() {
        return Text;
    }

    public String getMsgId() {
        return Id;
    }

    public String getMsgTimeStamp() {
        return TimeStamp;
    }


}
