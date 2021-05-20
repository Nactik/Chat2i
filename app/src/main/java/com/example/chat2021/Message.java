package com.example.chat2021;

import com.google.gson.annotations.SerializedName;

public class Message {

    String id;
    @SerializedName("contenu")
    String content;
    @SerializedName("auteur")
    String author;
    @SerializedName("couleur")
    String color;

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
