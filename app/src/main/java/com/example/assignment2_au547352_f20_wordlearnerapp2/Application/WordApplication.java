package com.example.assignment2_au547352_f20_wordlearnerapp2.Application;

import android.app.Application;

import androidx.room.Room;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Database.WordDB;

// Closely inspired from demo

public class WordApplication extends Application {

    private WordDB wordDatabase;

    public WordDB getWordDatabase(){
        if (wordDatabase == null){
            wordDatabase = Room.databaseBuilder(this, WordDB.class,"word_db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return wordDatabase;
    }
}
