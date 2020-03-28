package com.example.assignment2_au547352_f20_wordlearnerapp2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private Button btnCancel;
    private Button btnEdit;
    private ImageView wordImage;
    private TextView wordName;
    private TextView wordPronunciation;
    private TextView userWordRating;
    private TextView wordDescription;
    private TextView Notes;

    private Boolean isBound = false;
    private ServiceConnection serviceConnection;
    private WordService wordService;

    private Word myWord;

    static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(savedInstanceState != null) {
            myWord = (Word)savedInstanceState.getSerializable("wordSave");
        }
        MatchObjectsWithComponents();
        AddEventsToComponents();
        UpdateView();
        setServiceConnection();
        bindService();
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res,data);
        switch (req){
            case REQUEST_EDIT:
                if (res == Activity.RESULT_OK){

                    myWord = (Word)data.getSerializableExtra("passChangesToDetails");
                    Intent intentResult = new Intent(DetailsActivity.this, MainActivity.class);
                    intentResult.putExtra("passChangesToMain",myWord);
                    setResult(Activity.RESULT_OK,intentResult);
                    finish();

                }
                break;
        }
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
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,EditActivity.class);
                intent.putExtra("DetailToEdit",myWord);
                startActivityForResult(intent, REQUEST_EDIT);

            }
        });
    }

    private void setServiceConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wordService = ((WordService.WordBinder)service).getService();
                isBound = true;
                Toast.makeText(getApplicationContext(),"Service is connected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getApplicationContext(),"Service disconnected",Toast.LENGTH_LONG).show();
                isBound = false;
            }
        };
    }

    private void UpdateView() {
        myWord = (Word)getIntent().getSerializableExtra("wordInput");

        wordImage.setImageResource(myWord.getImage());
        wordName.setText(myWord.getName());
        wordPronunciation.setText(myWord.getPronunciation());
        userWordRating.setText(myWord.getUserRating().toString());
        wordDescription.setText(myWord.getDescription());
        Notes.setText(myWord.getNotes());
        if (myWord.URL==null || myWord.URL.equals("null")){
            wordImage.setImageResource(myWord.getImage());
        }
        else {
            Picasso.get().load(myWord.URL).into(wordImage);
        }
    }

    private void bindService(){
        Intent serviceIntent = new Intent(DetailsActivity.this,WordService.class);
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void unbindService(){
        if (isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    public void onBackPressed() {
        unbindService();
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
        unbindService();
    }


    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable("wordSave",myWord);
        super.onSaveInstanceState(bundle);
    }

    private void MatchObjectsWithComponents() {
        btnCancel = findViewById(R.id.btn_Cancel_details);
        btnEdit = findViewById(R.id.btn_Edit_details);
        wordImage = findViewById(R.id.IVWordImage_details);
        wordName = findViewById(R.id.TVWordName_details);
        wordPronunciation = findViewById(R.id.TVWordPronunciation_details);
        userWordRating = findViewById(R.id.TVuserWordRating_details);
        wordDescription = findViewById(R.id.TVWordDescription_details);
        Notes = findViewById(R.id.TVWordNotes_details);
    }

}
