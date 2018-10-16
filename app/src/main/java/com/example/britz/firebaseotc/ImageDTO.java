package com.example.britz.firebaseotc;

import java.util.HashMap;
import java.util.Map;

public class ImageDTO {

    public String imageUrl;
    public String imageName;
    public String title;
    public String description;
    public String uid;
    public String userID;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

}