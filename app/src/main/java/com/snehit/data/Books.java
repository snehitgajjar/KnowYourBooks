package com.snehit.data;

import java.io.Serializable;

/**
 * Created by snehitgajjar on 8/31/15.
 */
public class Books implements Serializable {

    private final String URL = "https://graph.facebook.com/";
    private String url;
    private String title;
    private String price;
    private String author;



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String pageId) {
        this.url = URL+pageId+"/"+"picture";
        System.out.println("image url ....:..... "+url);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
