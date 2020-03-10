package com.example.assignment2_au547352_f20_wordlearnerapp2;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class},version = 2)
public abstract class WordDB  extends RoomDatabase {
        public abstract WordDAO wordDAO();

}
