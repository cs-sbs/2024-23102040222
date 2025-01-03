package com.flash.pojo;

/**
 * 计算机类图书
 */
public class ComputerBook extends Book {
    private String programmingLanguage;
    private static final String CATEGORY = "计算机";

    public ComputerBook(int id, String title, String author, String isbn, 
                       String publishDate, String programmingLanguage) {
        super(id, title, author, isbn, publishDate);
        this.programmingLanguage = programmingLanguage;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public void displayDetails() {
        System.out.printf("【计算机类】%s\n编程语言: %s\n", 
            getBasicInfo(), programmingLanguage);
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }
} 