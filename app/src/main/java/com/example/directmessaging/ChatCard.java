package com.example.directmessaging;

public class ChatCard {
    private String nameText;
    private String mesPrevText;
    private String date;

    public ChatCard(String nameText, String mesPrevText, String date) {
        this.nameText = nameText;
        this.mesPrevText = mesPrevText;
        this.date = date;
    }

    public void changeText(String text) {
        nameText = text;
    }

    public String getNameText() {
        return nameText;
    }

    public String getMesPrevText() {
        return mesPrevText;
    }

    public String getDate() {
        return date;
    }
}
