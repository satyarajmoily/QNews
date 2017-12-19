package com.howaboutthis.satyaraj.qnews;


class ListItemEvent {
    private String heading;
    private String description;
    private String url;
    private String urlToImage;
    private Long time;

    ListItemEvent(String heading, String description,String url,String urlToImage,Long time) {
        this.url = url;
        this.heading = heading;
        this.description = description;
        this.urlToImage = urlToImage;
        this.time = time;
    }
    ListItemEvent(String heading, String description,String url,Long time) {
        this.url = url;
        this.heading = heading;
        this.description = description;
        this.time = time;
    }

    String getHeading(){
        return heading;
    }

    String getDescription(){
        return description;
    }

    String getUrl() { return url; }

    String getUrlToImage() { return urlToImage; }

    Long getTime() { return time; }
}

