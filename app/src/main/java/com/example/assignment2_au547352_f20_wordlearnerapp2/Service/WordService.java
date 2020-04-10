package com.example.assignment2_au547352_f20_wordlearnerapp2.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment2_au547352_f20_wordlearnerapp2.ApiModel.ApiWord;
import com.example.assignment2_au547352_f20_wordlearnerapp2.Application.WordApplication;
import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;
import com.example.assignment2_au547352_f20_wordlearnerapp2.R;
import com.example.assignment2_au547352_f20_wordlearnerapp2.Database.WordDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WordService extends Service {

    private IBinder iBinder = new WordBinder();
    private WordDB wordDB;
    private ArrayList<Word> notificationWords;
    private Timer myTimer;
    private myTimer myTimerTask;


    static final String BROADCASTER_UPDATING_LIST = "UpdateList";

    // URL
    private String URL = "https://owlbot.info/api/v4/dictionary/";
    private String myApiToken = "d2e37d2b3a696009061a4d4788c0c48462aaeee3";
    public final String nophoto = "https://www.referanslarim-tr.bosch-thermotechnology.com/Content/UploadedFiles/no-image.png";


    // Gson
    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson;

    // Volley
    //private Context myContext;
    private RequestQueue requestQueue;
    //private StringRequest stringRequest;
    //private ImageRequest imageRequest;

    // Create binder
    public class WordBinder extends Binder {
        public WordService getService() {
            return WordService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connectWordDB();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Bind", "OnBind called");
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void connectWordDB() {
        Log.d("Initiate Database", "Database initiated:");
        if (wordDB == null) {
            wordDB = Room.databaseBuilder(this, WordDB.class, "my_words").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            Log.d("DB connected", wordDB.toString());
        }
    }

    //onStartCommand - starting timer as we launch app through service-
    // Loads DB and starts timer that will then always run in the background.

    public int onStartCommand(Intent intent, int flags, int startId) {
        preLoadToDatabase();

        myTimer = new Timer();
        myTimerTask = new myTimer();
        myTimer.schedule(myTimerTask, 100, 60000);
        return START_STICKY;
    }

    public void RequestAPIAccess(String searchKey) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        final Map<String, String> stringMap;
        stringMap = new HashMap<String, String>();
        stringMap.put("Authorization", "Token " + myApiToken);
        String fullURL = URL + searchKey;

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                fetchApi(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ResponseError", Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return stringMap;
            }
        };
        requestQueue.add(objectRequest);


    }

    //Attempt to create mapper for objects we receive from API.

    public void fetchApi(String apiResponse) {
        gson = gsonBuilder.create();

        ApiWord apiWord = gson.fromJson(apiResponse, ApiWord.class);
        if (apiWord != null) {
            Word newWord = new Word(0,"","","",0.0,"","");

            //Checking if image resource is null - so I can insert a standard URL image, for Picasso to load later on.
            if (apiWord.getDefinitions().get(0).getImageUrl() != null) {
                newWord.setImage(apiWord.getDefinitions().get(0).getImageUrl());
            } else {
                newWord.setImage(nophoto);
            }
            newWord.setNotes("");
            newWord.setUserRating(0.0);
            newWord.setName(apiWord.getWord());
            newWord.setPronunciation(apiWord.getPronunciation());
            newWord.setDescription(apiWord.getDefinitions().get(0).getDefinition());

            this.addWord(newWord);
        }
    }

    //region My WordDB Functionalities

    // Get single
    public Word getWord(String title) {
        return wordDB.wordDAO().getWord(title);
    }

    // Get all
    public List<Word> getAllWords() {
        return wordDB.wordDAO().getAllWords();
    }

    // Add
    public void addWord(Word word) {
        wordDB.wordDAO().addWord(word);
    }

    // Insert
    public void insertWords(List<Word> words) {
        wordDB.wordDAO().insertWords(words);
    }

    // Delete
    public void deleteWord(int ID) {
        wordDB.wordDAO().deleteWord(ID);
    }

    // Update
    public void updateWord(Word word) {
        wordDB.wordDAO().updateWord(word);
    }
    //endregion

    // Schedule Local Notification in Android Step by Step:
    // https://www.youtube.com/watch?v=k-tREnlQsrk
    class myTimer extends TimerTask {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Channel01", "myChannel", NotificationManager.IMPORTANCE_LOW);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            // Creating random functionality to pick a random index based on the size of a DB - previously used length of an Arraylist loaded from CSV using a csv reader -
            // this gave me problems with the image when using the API "string images"
            Random random = new Random();
            WordApplication wordApplication = (WordApplication)getApplication();
            WordDB notiDB = wordApplication.getWordDatabase();
            int randomWord = random.nextInt(notiDB.wordDAO().getAllWords().size());

            Notification notification = new NotificationCompat.Builder(WordService.this, "Channel01")
                    .setContentText("Check out this word: " + notiDB.wordDAO().getAllWords().get(randomWord).getName())
                    .setContentTitle(getText(R.string.app_name))
                    .setSmallIcon(R.mipmap.word_learner_square_yellow_01)
                    .setChannelId("Channel01")
                    .build();
            startForeground(10, notification);
        }
    }

    // Attempt to pre load custom list to db
    // All copied directly from CSV file with parameters used from OwlAPI
    public List<Word> preLoadToDatabase() {

        WordApplication wordApplication = (WordApplication) getApplication();
        WordDB myDB = wordApplication.getWordDatabase();
        if (myDB.wordDAO().getAllWords().isEmpty()){
            myDB.wordDAO().addWord(new Word(0,"Lion","ˈlīən", "A large tawny-coloured cat that lives in prides, found in Africa and NW India. The male has a flowing shaggy mane and takes little part in hunting, which is done cooperatively by the females.", 0.0, "", "https://media.owlbot.info/dictionary/images/ooooow.jpg.400x400_q85_box-23,22,478,477_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Leopard","ˈlepərd", "A large solitary cat that has a fawn or brown coat with black spots, native to the forests of Africa and southern Asia.", 0.0, "", "https://media.owlbot.info/dictionary/images/oooooz.jpg.400x400_q85_box-0,0,500,500_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Cheetah","ˈCHēdə", "A large slender spotted cat found in Africa and parts of Asia. It is the fastest animal on land.", 0.0, "", "https://media.owlbot.info/dictionary/images/sssssb.jpg.400x400_q85_box-0,0,500,500_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Elephant","ˈeləfənt", "A very large plant-eating mammal with a prehensile trunk, long curved ivory tusks, and large ears, native to Africa and southern Asia. It is the largest living land animal.", 0.0, "", "https://media.owlbot.info/dictionary/images/27ti5gwrzr_Julie_Larsen_Maher_3242_African_Elephant_UGA_06_30_10_hr.jpg.400x400_q85_box-356,0,1156,798_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Giraffe","jəˈraf", "A large African mammal with a very long neck and forelegs, having a coat patterned with brown patches separated by lighter lines. It is the tallest living animal.", 0.0, "", "https://media.owlbot.info/dictionary/images/nnnk.jpg.400x400_q85_box-0,0,225,225_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Kudu","ˈko͞odo͞o", "An African antelope that has a greyish or brownish coat with white vertical stripes, and a short bushy tail. The male has long spirally curved horns.", 0.0, "", "https://media.owlbot.info/dictionary/images/kkkkkkj.jpg.400x400_q85_box-0,0,500,500_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Gnu","n(y)o͞o", "A large dark antelope with a long head, a beard and mane, and a sloping back.", 0.0, "", "https://media.owlbot.info/dictionary/images/qqqqql.jpg.400x400_q85_box-0,0,1413,1414_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Oryx","null", "A large antelope living in arid regions of Africa and Arabia, having dark markings on the face and long horns.", 0.0, "", "https://media.owlbot.info/dictionary/images/nnnnnn.jpg.400x400_q85_box-0,0,500,500_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Camel","ˈkaməl", "A large, long-necked ungulate mammal of arid country, with long slender legs, broad cushioned feet, and either one or two humps on the back. Camels can survive for long periods without food or drink, chiefly by using up the fat reserves in their humps.", 0.0, "", "https://media.owlbot.info/dictionary/images/nnnt.png.400x400_q85_box-0,0,500,500_crop_detail.png"));
            myDB.wordDAO().addWord(new Word(0,"Shark","SHärk", "a long-bodied chiefly marine fish with a cartilaginous skeleton, a prominent dorsal fin, and tooth-like scales. Most sharks are predatory, though the largest kinds feed on plankton, and some can grow to a large size.", 0.0, "", "https://media.owlbot.info/dictionary/images/dn.jpg.400x400_q85_box-576,0,1226,649_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Crocodile","ˈkräkəˌdīl", "a large predatory semiaquatic reptile with long jaws, long tail, short legs, and a horny textured skin.", 0.0, "", "https://media.owlbot.info/dictionary/images/rrrrrm.jpg.400x400_q85_box-0,0,500,500_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Snake","snāk", "a long limbless reptile which has no eyelids, a short tail, and jaws that are capable of considerable extension. Some snakes have a venomous bite.", 0.0, "", "https://media.owlbot.info/dictionary/images/llllllg.jpg.400x400_q85_box-0,0,225,225_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Buffalo","ˈbəf(ə)ˌlō", "a heavily built wild ox with backward-curving horns, found mainly in the Old World tropics:", 0.0, "", "https://media.owlbot.info/dictionary/images/kkkkkkw.jpg.400x400_q85_box-0,0,600,600_crop_detail.jpg"));
            myDB.wordDAO().addWord(new Word(0,"Ostrich","ˈästriCH", "A flightless swift-running African bird with a long neck, long legs, and two toes on each foot. It is the largest living bird, with males reaching a height of up to 2.75 m.", 0.0, "", "https://media.owlbot.info/dictionary/images/gggk.jpg.400x400_q85_box-0,0,225,225_crop_detail.jpg"));

        }
        return myDB.wordDAO().getAllWords();
    }


}


