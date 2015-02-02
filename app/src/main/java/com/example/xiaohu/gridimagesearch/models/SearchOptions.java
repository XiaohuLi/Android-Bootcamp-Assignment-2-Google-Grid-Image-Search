package com.example.xiaohu.gridimagesearch.models;

import java.io.Serializable;

/**
 * Created by xiaohu on 2/1/15.
 */
public class SearchOptions implements Serializable{
    public String size;
    public String color;
    public String type;
    public String site;

    public SearchOptions(String size, String color, String type, String site) {
        this.size = size;
        this.color = color;
        this.type = type;
        this.site = site;
    }

    public SearchOptions(){
        this("", "", "", "");
    }
}
