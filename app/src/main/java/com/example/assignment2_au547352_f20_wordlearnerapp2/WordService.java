package com.example.assignment2_au547352_f20_wordlearnerapp2;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
    private String myApiToken = "Token d2e37d2b3a696009061a4d4788c0c48462aaeee3";

    // Create Volley
    private Context myContext;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private ImageRequest imageRequest;

    // Create binder
    public class WordBinder extends Binder {
        public WordService getService() {return WordService.this;}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connectWordDB();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Bind","OnBind called");
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void connectWordDB() {
        Log.d("Initiate Database","Database initiated:" );
            if (wordDB == null) {
                wordDB = Room.databaseBuilder(this, WordDB.class, "my_words").allowMainThreadQueries().fallbackToDestructiveMigration().build();
                Log.d("DB connected",wordDB.toString());
            }
    }

    //onStartCommand - starting timer as we launch app through service.

    public int onStartCommand(Intent intent, int flags, int startId){
        myTimer = new Timer();
        myTimerTask = new myTimer();
        myTimer.schedule(myTimerTask, 100, 60000);
        return START_STICKY;
    }

    // OnResponse with error listener - filling searchkey with API to lookup words on API
    // Should read all json objects using Gson.

    public void GetAPIWords(String searchKey, Context context, final boolean start){
        this.myContext =context;
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.GET + searchKey, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("OnResponse", "OnResponse called");
                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
                int size = jsonArray.size();
                if (start) {
                    size = 10;
                }
                Word word;
                for (int i = 0; i < size; i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    word = objectMapper(jsonObject);

                    if (getWord(word.getName()) == null) {
                        addWord(word);
                    } else {
                        Word temporary = getWord(word.getName());
                        deleteWord(temporary.getID());
                        addWord(word);
                    }
                }
                Intent intent = new Intent(BROADCASTER_UPDATING_LIST);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ResponseError",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    //Attempt to create mapper for objects we receive from API.

    private Word objectMapper(JsonObject jsonObject){
        Word apiWord = new Word();
        apiWord.setUserRating(0.0);
        apiWord.setNotes("");
        apiWord.setName(jsonObject.get("word").toString().substring(1,jsonObject.get("name").toString().length()-1));
        apiWord.setPronunciation(jsonObject.get("pronunciation").toString().substring(1,jsonObject.get("pronunciation").toString().length()-1));
        apiWord.setDescription(jsonObject.get("definition").toString().substring(1,jsonObject.get("definition").toString().length()-1));
        apiWord.setID(new Random().nextInt(9999));
        String imageURL = jsonObject.get("image_url").toString().substring(1,jsonObject.get("image_url").toString().length()-1);
        apiWord.URL = imageURL;
        if (imageURL!="null"){
            apiWord.setImage(R.drawable.nophoto);
        }
        else {
            apiWord.setImage(R.drawable.nophoto);
        }
        return apiWord;
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
    class myTimer extends TimerTask{

        @Override
        public void run() {
            notificationWords = (GetWordList(getApplicationContext()));

            Random random = new Random();
            int randomNumber = random.nextInt(notificationWords.size());

            Word notificationWordList = notificationWords.get(randomNumber);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Channel01","myChannel", NotificationManager.IMPORTANCE_LOW);
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(WordService.this, "Channel01")
                    .setContentText("Check out this word of the day: "+notificationWordList.getName()+" "+notificationWordList.getPronunciation())
                    .setContentTitle(getText(R.string.app_name))
                    .setSmallIcon(R.mipmap.word_learner_red01)
                    .setChannelId("Channel01")
                    .build();
            startForeground(10, notification);
        }
    }

    public ArrayList<Word> GetWordList(Context context) {
        ArrayList<Word> wordList = new ArrayList<>();
        try {
            InputStreamReader animalWordsFile = new InputStreamReader(context.getAssets().open("animals.csv"));
            BufferedReader bufferedReader = new BufferedReader(animalWordsFile);
            String line;
            String notes = " ";
            int id = 0;
            boolean rowLabel = true;

            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if(rowLabel) {
                    rowLabel = false;
                    continue;
                }

                // taking data from .csv file - ImgMapper taking first datatype (name) as parameter so we can use switchcase on name
                // to pick what image we insert from drawable.

                Word newWord = new Word(id, data[0], data[1], data[2], 0.0, notes, (ImgMapper(data[0])));
                wordList.add(newWord);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordList;
    }

    private int ImgMapper(String name) {
        switch (name) {
            case "Lion":
                return (R.drawable.lion);
            case "Leopard":
                return (R.drawable.leopard);
            case "Cheetah":
                return (R.drawable.cheetah);
            case "Elephant":
                return (R.drawable.elephant);
            case "Giraffe":
                return (R.drawable.giraffe);
            case "Kudu":
                return (R.drawable.kudo);
            case "Gnu":
                return (R.drawable.gnu);
            case "Oryx":
                return (R.drawable.oryx);
            case "Camel":
                return (R.drawable.camel);
            case "Shark":
                return (R.drawable.shark);
            case "Crocodile":
                return (R.drawable.crocodile);
            case "Snake":
                return (R.drawable.snake);
            case "Buffalo":
                return (R.drawable.buffalo);
            case "Ostrich":
                return (R.drawable.ostrich);
            default:
                return (R.drawable.nophoto);
        }
    }

}
