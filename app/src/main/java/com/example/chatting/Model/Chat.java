package com.example.chatting.Model;

public class Chat {

    private  String SENDER, RECEIVER, MESSAGE;
    private  boolean ISSEEN;

    public Chat(String SENDER, String RECEIVER, String MESSAGE, boolean ISSEEN) {
        this.SENDER = SENDER;
        this.RECEIVER = RECEIVER;
        this.MESSAGE = MESSAGE;
        this.ISSEEN = ISSEEN;
    }

    public Chat() {
    }

    public String getSENDER() {
        return SENDER;
    }

    public void setSENDER(String SENDER) {
        this.SENDER = SENDER;
    }

    public String getRECEIVER() {
        return RECEIVER;
    }

    public void setRECEIVER(String RECEIVER) {
        this.RECEIVER = RECEIVER;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public boolean isISSEEN() {
        return ISSEEN;
    }

    public void setISSEEN(boolean ISSEEN) {
        this.ISSEEN = ISSEEN;
    }
}
