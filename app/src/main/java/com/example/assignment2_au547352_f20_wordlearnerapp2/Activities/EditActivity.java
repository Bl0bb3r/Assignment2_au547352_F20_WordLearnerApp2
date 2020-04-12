package com.example.assignment2_au547352_f20_wordlearnerapp2.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;
import com.example.assignment2_au547352_f20_wordlearnerapp2.R;
import com.example.assignment2_au547352_f20_wordlearnerapp2.Service.WordService;

public class EditActivity extends AppCompatActivity {

    private Button btnCancel;
    private Button btnUpdate;
    private TextView TVwordName;
    private TextView TVuserWordRating;
    private SeekBar SBuserWordRating;
    private EditText ETnotes;

    private Boolean isBound = false;
    private ServiceConnection serviceConnection;
    private WordService wordService;

    private Word myWord;
    private String detailInputRecieved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (savedInstanceState != null) {
            myWord = (Word)savedInstanceState.getSerializable("words");
        }

        Intent receiveIntent = getIntent();
        detailInputRecieved = receiveIntent.getStringExtra("DetailToEdit");

        MatchObjectsWithComponents();

        AddEventsToComponents();

        setServiceConnection();

        bindService();
    }

    @Override
    public void onBackPressed() { finish(); }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }

    private void AddEventsToComponents() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,cancelIntent);
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                // inserted slight functionality here to workaround our Word Model using UserRating as a double value
                // but the value we read from the TextView is a string. This converts it the right way.
                // slight inspiration from here - https://androidforums.com/threads/get-the-double-value-from-string.210387/
                double tempRating = Double.parseDouble(TVuserWordRating.getText().toString());
                myWord.setUserRating(tempRating);
                myWord.setNotes(ETnotes.getText().toString());
                wordService.updateWord(myWord);

                setResult(Activity.RESULT_OK,sendIntent);
                finish();
            }
        });

        SBuserWordRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double sbValue = ((double)progress/10);
                myWord.setUserRating(sbValue);
                TVuserWordRating.setText((myWord.getUserRating().toString()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setServiceConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wordService = ((WordService.WordBinder)service).getService();
                myWord = wordService.getWord(detailInputRecieved);
                isBound = true;
                UpdateWord();
                Toast.makeText(getApplicationContext(),"Service connected",Toast.LENGTH_LONG);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceConnection = null;
                isBound = false;
                Toast.makeText(getApplicationContext(),"Service disconnected",Toast.LENGTH_LONG);
            }
        };
    }

    private void bindService(){
        Intent serviceIntent = new Intent(EditActivity.this,WordService.class);
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void unbindService(){
        if (isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void UpdateWord() {

        TVwordName.setText(myWord.getName());
        TVuserWordRating.setText(myWord.getUserRating().toString());
        ETnotes.setText(myWord.getNotes());
        ETnotes.setMovementMethod(new ScrollingMovementMethod());

        //Sequence updating how Seekbar shows its rating
        SBuserWordRating.setMax(100);
        SBuserWordRating.setProgress((int)(myWord.getUserRating()*10));
        double tempRating = Double.parseDouble(myWord.getUserRating().toString());
        // using trick to convert my double into an int - might have been able to workaround it?
        int tempIntRating = (int) (tempRating);
        SBuserWordRating.setProgress(tempIntRating);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("words",myWord);
        super.onSaveInstanceState(outState);
    }

    private void MatchObjectsWithComponents() {
        btnCancel = findViewById(R.id.btn_Cancel_edit);
        btnUpdate = findViewById(R.id.btn_Update_edit);
        TVwordName = findViewById(R.id.TVWordName_edit);
        TVuserWordRating = findViewById(R.id.TVuserWordRating_edit);
        SBuserWordRating = findViewById(R.id.SBuserWordRating_edit);
        ETnotes = findViewById(R.id.ETnotes_edit);
    }
}
