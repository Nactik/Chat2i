package com.example.chat2021;

import com.example.chat2021.Enseignant;

import java.util.ArrayList;

public class Promo {
    String promo;
    ArrayList<Enseignant> enseignants;

    @Override
    public String toString() {
        return "Promo{" +
                "promo='" + promo + '\'' +
                ", enseignants=" + enseignants +
                '}';
    }
    //{"promo":"2020-2021",
    // "enseignants":[
    // {"prenom":"Mohamed","nom":"Boukadir"},
    // {"prenom":"Thomas","nom":"Bourdeaud'huy"},
    // {"prenom":"Mathieu","nom":"Haussher"},
    // {"prenom":"Slim","nom":"Hammadi"}]}
}
