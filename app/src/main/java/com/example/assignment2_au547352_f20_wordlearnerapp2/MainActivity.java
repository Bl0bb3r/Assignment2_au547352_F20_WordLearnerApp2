package com.example.assignment2_au547352_f20_wordlearnerapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Word> wordList;
    private WordArrayAdapter wordListAdaptor;
    private ListView wordListView;
    private Word selectedWord;
    private WordService wordService;
    private EditText searchFieldET;
    private Button btnAdd;
    private Button btnExit;

    private int wordIndex;

    private BroadcastReceiver receiver;
    private ServiceConnection serviceConnection;
    private boolean isBound = false;


    static final int REQUEST_EDIT = 1;
    static final int REQUEST_WORD = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordService = new WordService();
        wordList = new ArrayList<>();

        // Retrieving Wordlist
        //wordList = serviceWord.GetWordList(getApplicationContext());

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
        setWordService();
        startService();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("Update wordList"));
    }

    //region Lifecycle stuff
    @Override
    protected void onResume(){
        super.onResume();
        startService();
    }
    @Override
    protected void onStart(){
        super.onStart();
        bindService();
    }
    @Override
    protected void onStop(){
        super.onStop();
        unbindService();
    }

    //endregion

    @Override
    public void onBackPressed(){
        unbindService();
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
                    wordService.addWord(changedWord);
                    ((BaseAdapter)wordListView.getAdapter()).notifyDataSetChanged();
                }
                break;
            case REQUEST_WORD:
                ((BaseAdapter)wordListView.getAdapter()).notifyDataSetChanged();
                break;
        }
    }

    //region Component Handlers
    private void MatchObjectWithComponents() {
        btnExit = findViewById(R.id.btn_Exit_main);
        btnAdd = findViewById(R.id.btn_Add_main);
        wordListView = findViewById(R.id.LVmainActivity_main);
        searchFieldET = findViewById(R.id.ETsearchField_main);

    }

    private void AddEventsToComponents() {
        wordListView.setAdapter(wordListAdaptor);

        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                selectedWord = wordList.get(i);
                unbindService();
                intent.putExtra("wordInput",selectedWord);
                startActivityForResult(intent, REQUEST_EDIT);
                wordIndex = i;
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService();
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add functionality for adding new word to wordListView
                wordService.GetAPIWords(searchFieldET.getText().toString(),getApplicationContext(),false);
                wordList = (ArrayList<Word>)wordService.getAllWords();
                wordListAdaptor.Update(wordList);
                ((BaseAdapter)wordListView.getAdapter()).notifyDataSetChanged();
                Log.i("ADD", "onClick add: Added word to list");

            }
        });

        //Broadcast receiver calling when list updated through service
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wordList = (ArrayList<Word>)wordService.getAllWords();
                wordListAdaptor.Update(wordList);
                ((BaseAdapter)wordListView.getAdapter()).notifyDataSetChanged();
                Log.i("BRN","Broadcast receive notification");
            }
        };
    }

    //endregion

    private void setWordService(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wordService = ((WordService.WordBinder)service).getService();
                wordList = (ArrayList<Word>)wordService.getAllWords();

                // If loop - should update if wordList empty - inserting words including searchKey:"School"
                if (wordList.size() == 0){
                    wordService.insertWords(wordService.GetWordList(getApplicationContext()));
                    wordService.GetAPIWords("School",getApplicationContext(),true);
                }
                wordListAdaptor.Update(wordList);
                ((BaseAdapter)wordListView.getAdapter()).notifyDataSetChanged();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                wordService = null;
            }
        };
    }

    // on save instance for saving state, used to update content and prevent refreshing entire app
    // should save users edits and position in app
    @Override
    protected void onSaveInstanceState(Bundle outstate) {

        for(Word word: wordList) {
            outstate.putSerializable(word.getName(),word);
        }

        super.onSaveInstanceState(outstate);
    }

    //region Service bindings
    private void bindService(){
        Intent serviceIntent = new Intent(MainActivity.this, WordService.class);
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void unbindService(){
        if (isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void startService(){
        Intent serviceIntent = new Intent(MainActivity.this,WordService.class);
        startService(serviceIntent);
        bindService();
    }

    //endregion

}
