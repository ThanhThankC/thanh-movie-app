package com.example.thanhmovie.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {
    @SerializedName("results")
    private List<VideoResult> results;

    public List<VideoResult> getResults() { return results; }

    public static class VideoResult {
        @SerializedName("key")
        private String key;

        @SerializedName("site")
        private String site;

        @SerializedName("type")
        private String type;

        public String getKey() { return key; }
        public String getSite() { return site; }
        public String getType() { return type; }
    }
}