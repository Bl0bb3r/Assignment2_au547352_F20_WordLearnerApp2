package com.example.assignment2_au547352_f20_wordlearnerapp2.ApiModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//http://www.jsonschema2pojo.org/

public class ApiWordDefinition {
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("definition")
        @Expose
        private String definition;
        @SerializedName("example")
        @Expose
        private String example;
        @SerializedName("image_url")
        @Expose
        private String imageUrl;
        @SerializedName("emoji")
        @Expose
        private String emoji;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getExample() {
            return example;
        }

        public void setExample(String example) {
            this.example = example;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getEmoji() {
            return emoji;
        }

        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }
}


