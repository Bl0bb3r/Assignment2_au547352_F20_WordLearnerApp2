package com.example.assignment2_au547352_f20_wordlearnerapp2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.assignment2_au547352_f20_wordlearnerapp2.Model.Word;
import com.example.assignment2_au547352_f20_wordlearnerapp2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WordArrayAdapter extends ArrayAdapter<Word> {

    Context myContext;
    public List<Word> adapterWordList = new ArrayList<>();


    public WordArrayAdapter(Context context, ArrayList<Word> words) {
        super(context,0,words);
        myContext = context;
        adapterWordList = words;
    }

    public void Update(ArrayList<Word> words){

        adapterWordList.clear();
        adapterWordList.addAll(words);

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int current, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(myContext).inflate(R.layout.word_layout,parent,false);
        }

        Word myWord = adapterWordList.get(current);

        // Image - again using Picasso to insert from URL
        ImageView IVWordImage = listItem.findViewById(R.id.IVWordImage);
        Picasso.get().load(myWord.getImage()).into(IVWordImage);

        // Name
        TextView TVWordLabel = listItem.findViewById(R.id.TVWordLabel);
        TVWordLabel.setText(myWord.getName());

        // Pronunciation
        TextView TVWordPronunciation = listItem.findViewById(R.id.TVWordPronunciation);
        TVWordPronunciation.setText(myWord.getPronunciation());

        // Rating
        TextView TVUserWordRating = listItem.findViewById(R.id.TVUserWordRatingLabel);
        TVUserWordRating.setText(myWord.getUserRating().toString());


        return listItem;
    }


}
