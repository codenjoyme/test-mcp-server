package dev.danvega.javaone;

public class Presentation {

    private String title;
    private String url;
    private int year;

    public Presentation(String title, String url, int year) {
        this.title = title;
        this.url = url;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String toString() {
        return "Title: " + title + ", URL: " + url + ", Year: " + year;
    }
}
