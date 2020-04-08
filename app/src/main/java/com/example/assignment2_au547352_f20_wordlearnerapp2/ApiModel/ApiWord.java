package com.example.assignment2_au547352_f20_wordlearnerapp2.ApiModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//http://www.jsonschema2pojo.org/

public class ApiWord {
    @SerializedName("definitions")
    @Expose
    private List<ApiWordDefinition> definitions = null;
    @SerializedName("word")
    @Expose
    private String word;
    @SerializedName("pronunciation")
    @Expose
    private String pronunciation;

    public List<ApiWordDefinition> getDefinitions(){
        return definitions;
    }

    public void setDefinitions(List<ApiWordDefinition> definitions){
        this.definitions = definitions;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}

