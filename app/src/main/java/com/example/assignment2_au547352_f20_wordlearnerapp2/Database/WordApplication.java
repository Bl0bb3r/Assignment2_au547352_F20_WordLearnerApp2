package com.example.assignment2_au547352_f20_wordlearnerapp2.Database;

import android.app.Application;

import androidx.room.Room;

public class WordApplication extends Application {

    private WordDB wordDatabase;

    public WordDB getWordDatabase(){
        if (wordDatabase == null){
            wordDatabase = Room.databaseBuilder(this, WordDB.class,"word_db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return wordDatabase;
    }
}
