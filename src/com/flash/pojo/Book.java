package com.flash.pojo;

/**
 * 图书基类，定义所有类型图书的通用属性和方法
 */
public abstract class Book {
    protected int id;
    protected String title;
    protected String author;
    protected String isbn;
    protected String publishDate;

    public Book(int id, String title, String author, String isbn, String publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishDate = publishDate;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getPublishDate() { return publishDate; }

    /**
     * 获取图书分类
     * @return 图书分类
     */
    public abstract String getCategory();

    /**
     * 显示图书详细信息
     */
    public abstract void displayDetails();

    /**
     * 获取图书简要信息
     */
    public String getBasicInfo() {
        return String.format("ID: %d, 书名: %s, 作者: %s", id, title, author);
    }
}
