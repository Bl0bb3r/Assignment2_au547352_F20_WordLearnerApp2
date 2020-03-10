package com.example.assignment2_au547352_f20_wordlearnerapp2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

public interface WordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Word word);




}
