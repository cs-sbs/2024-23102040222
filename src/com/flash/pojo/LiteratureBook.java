package com.flash.pojo;

/**
 * 文学类图书
 */
public class LiteratureBook extends Book {
    private String genre;
    private static final String CATEGORY = "文学";

    public LiteratureBook(int id, String title, String author, String isbn, 
                         String publishDate, String genre) {
        super(id, title, author, isbn, publishDate);
        this.genre = genre;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public void displayDetails() {
        System.out.printf("【文学类】%s\n文学流派: %s\n", 
            getBasicInfo(), genre);
    }

    public String getGenre() {
        return genre;
    }
} 