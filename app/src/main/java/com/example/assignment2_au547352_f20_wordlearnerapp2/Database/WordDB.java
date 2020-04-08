package com.example.assignment2_au547352_f20_wordlearnerapp2.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;

@Database(entities = {Word.class},version = 2)
public abstract class WordDB  extends RoomDatabase {
        public abstract WordDAO wordDAO();

}
