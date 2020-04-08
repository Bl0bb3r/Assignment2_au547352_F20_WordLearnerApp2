package com.example.assignment2_au547352_f20_wordlearnerapp2.Model;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.assignment2_au547352_f20_wordlearnerapp2.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "words")
public class Word implements Serializable {

    private String name;
    private String pronunciation;
    private String description;
    private Double userRating;
    private String notes;
    private String image;
    @PrimaryKey (autoGenerate = true)
    private int ID;
    public String URL;

    /*@Ignore
    public Word() {
        this.name = "word";
        this.pronunciation = "wuhd";
        this.description = "let me see you try to describe the word word";
        this.userRating = 7.2;
        this.notes = "Insert Notes";
        this.image = R.drawable.nophoto;
        this.ID = 1337;

    }*/

    public Word(int ID, String name, String pronunciation, String description, Double userRating, String notes, String image) {
        this.name = name;
        this.pronunciation = pronunciation;
        this.description = description;
        this.userRating = userRating;
        this.notes = notes;
        this.image = image;
        this.ID = ID;

    }

    // Get og Set methods

    // name
    public String getName() {
        return this.name;
    }

    public void setName(String Name) {
        this.name = Name;
    }



    // image
    public String getImage() { return image; }

    public void setImage(String Image) { image = Image;
    }



    // pronunciation
    public String getPronunciation() {
        return this.pronunciation;
    }

    public void setPronunciation(String Name) {
        this.pronunciation = Name;
    }



    // description
    public String getDescription() { return this.description; }

    public void setDescription(String Description) { this.description = Description; }



    // userRating
    public Double getUserRating() {
        return this.userRating;
    }

    public void setUserRating(Double UserRating) {
        this.userRating = UserRating;
    }



    // notes
    public String getNotes() { return notes; }

    public void setNotes(String Notes) { notes = Notes; }



    // ID
    public Integer getID() {
        return ID;
    }

    public void setID(Integer id) {
        ID = id;
    }


}
