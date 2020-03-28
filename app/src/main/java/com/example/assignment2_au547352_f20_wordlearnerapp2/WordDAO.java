package com.example.assignment2_au547352_f20_wordlearnerapp2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface WordDAO {

    // Crud operations

    // Add Word
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addWord(Word word);

    // Insert Words
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWords(List<Word> wordList);

    // Delete Word
    @Query("DELETE FROM words WHERE ID = :id")
    void deleteWord(int id);

    // Update Word
    @Update
    void updateWord(Word word);

    // Get Word
    @Query("SELECT * FROM words WHERE name = :name")
    Word getWord(String name);

    // Get Words
    @Query("SELECT * FROM words")
    List<Word> getAllWords();

}
