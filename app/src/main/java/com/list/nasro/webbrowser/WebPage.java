package com.list.nasro.webbrowser;

/**
 * Created by redayoub on 6/12/19.
 */

public class WebPage {
    private int id;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebPage() {
    }

    public WebPage(int id, String url) {
        this.id = id;
        this.url = url;
    }
}
