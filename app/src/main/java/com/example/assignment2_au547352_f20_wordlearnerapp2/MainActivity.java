package com.example.assignment2_au547352_f20_wordlearnerapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Word> wordList;
    private WordArrayAdapter wordListAdaptor;
    private ListView wordListView;
    private Word selectedWord;
    private Word serviceWord;
    private Button BtnExit;

    private int wordIndex;

    static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceWord = new Word();
        wordList = new ArrayList<>();

        // Retrieving Wordlist
        wordList = serviceWord.GetWordList(getApplicationContext());

        // Update listview on rotation of phone
        if(savedInstanceState != null) {
            ArrayList<Word> list = new ArrayList<>();

            for(Word word: wordList) {
                list.add(((Word)savedInstanceState.getSerializable(word.getName())));
            }
            wordList = list;
        }

        wordListAdaptor = new WordArrayAdapter(this, wordList);
        MatchObjectWithComponents();
        AddEventsToComponents();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res,data);
        switch (req){
            case REQUEST_EDIT:
                if (res == Activity.RESULT_OK){

                    Word changedWord = (Word)data.getSerializableExtra("passChangesToMain");
                    wordList.set(wordIndex,changedWord);
                    ((BaseAdapter)wordListView.getAdapter()).notifyDataSetChanged();
                }
                break;
        }
    }

    private void MatchObjectWithComponents() {
        BtnExit = findViewById(R.id.btn_Exit_main);
        wordListView = findViewById(R.id.LVmainActivity_main);
    }

    private void AddEventsToComponents() {
        wordListView.setAdapter(wordListAdaptor);

        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                selectedWord = wordList.get(i);
                intent.putExtra("wordInput",selectedWord);
                startActivityForResult(intent, REQUEST_EDIT);
                wordIndex = i;
            }
        });

        BtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {

        for(Word word: wordList) {
            outstate.putSerializable(word.getName(),word);
        }

        super.onSaveInstanceState(outstate);
    }
}
