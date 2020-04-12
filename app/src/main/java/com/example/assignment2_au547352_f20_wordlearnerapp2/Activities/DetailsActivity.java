package com.example.assignment2_au547352_f20_wordlearnerapp2.Activities;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;
import com.example.assignment2_au547352_f20_wordlearnerapp2.R;
import com.example.assignment2_au547352_f20_wordlearnerapp2.Service.WordService;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private Button btnCancel;
    private Button btnDelete;
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
    private String mainInputReceived;

    static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(savedInstanceState != null) {
            myWord = (Word)savedInstanceState.getSerializable("wordSave");
        }

        Intent receiveIntent = getIntent();
        mainInputReceived = receiveIntent.getStringExtra("wordInput");

        MatchObjectsWithComponents();


        AddEventsToComponents();

        // Set service connection also contains (not easily spotted) an updateWord functionality
        setServiceConnection();

        bindService();
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res,data);
        switch (req){
            case REQUEST_EDIT:
                if (res == Activity.RESULT_OK){

                    //myWord = (Word)data.getSerializableExtra("passChangesToDetails");
                    Intent intentResult = new Intent(DetailsActivity.this, MainActivity.class);
                    //intentResult.putExtra("passChangesToMain",myWord);
                    setResult(Activity.RESULT_OK,intentResult);
                    finish();

                }
                break;
        }
    }

    private void setServiceConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // using wordService here to update the actual word contained in the DetailsActivity resulting individually based on which we clicked from list.
                wordService = ((WordService.WordBinder)service).getService();
                myWord = wordService.getWord(mainInputReceived.toLowerCase());
                UpdateWord();
                isBound = true;
                Toast.makeText(getApplicationContext(),""+getString(R.string.service_connected),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getApplicationContext(),""+getString(R.string.service_disconnected),Toast.LENGTH_LONG).show();
                serviceConnection = null;
                isBound = false;
            }
        };
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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,cancelIntent);
                wordService.deleteWord(myWord.getID());
                unbindService();
                finish();

            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.putExtra("DetailToEdit", mainInputReceived);
                startActivityForResult(intent, REQUEST_EDIT);

            }
        });
    }


    private void UpdateWord() {
        //myWord = (Word)getIntent().getSerializableExtra("wordInput");

        wordName.setText(myWord.getName());
        wordPronunciation.setText(myWord.getPronunciation());
        wordDescription.setText(myWord.getDescription());

        //getting this warning - could be avoided somply by chaning the datatype permanent from double to string -
        // however if we later on want to use the value as a number we would have to change structure.
        userWordRating.setText(myWord.getUserRating().toString());

        Notes.setText(myWord.getNotes());

        // Loads image URL - so either default image we inserted with service or API image :)
        // using Picasso to insert image from URL - https://square.github.io/picasso/
        Picasso.get().load(myWord.getImage()).into(wordImage);

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
        btnDelete = findViewById(R.id.btn_Delete_details);
        btnEdit = findViewById(R.id.btn_Edit_details);
        wordImage = findViewById(R.id.IVWordImage_details);
        wordName = findViewById(R.id.TVWordName_details);
        wordPronunciation = findViewById(R.id.TVWordPronunciation_details);
        userWordRating = findViewById(R.id.TVuserWordRating_details);
        wordDescription = findViewById(R.id.TVWordDescription_details);
        Notes = findViewById(R.id.TVWordNotes_details);
    }

}
