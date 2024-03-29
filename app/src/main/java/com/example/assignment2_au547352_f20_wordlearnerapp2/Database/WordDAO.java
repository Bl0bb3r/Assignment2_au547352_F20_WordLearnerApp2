package com.example.assignment2_au547352_f20_wordlearnerapp2.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;

import java.util.List;

// heavily inspired from https://www.youtube.com/watch?v=Ta4pw2nUUE4 and demo on using Room

@Dao
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
