package com.example.assignment2_au547352_f20_wordlearnerapp2.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;

// heavily inspired from https://www.youtube.com/watch?v=Ta4pw2nUUE4 and demo on using Room

@Database(entities = {Word.class},version = 2)
public abstract class WordDB  extends RoomDatabase {
        public abstract WordDAO wordDAO();

}
