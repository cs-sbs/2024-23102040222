package com.flash.factory;

import com.flash.pojo.*;

/**
 * 图书工厂类，负责创建不同类型的图书对象
 */
public class BookFactory {
    public static Book createBook(String category, int id, String title, 
                                String author, String isbn, String publishDate, 
                                String extraInfo) {
        switch (category.toLowerCase()) {
            case "计算机":
                return new ComputerBook(id, title, author, isbn, publishDate, extraInfo);
            case "文学":
                return new LiteratureBook(id, title, author, isbn, publishDate, extraInfo);
            // 可以添加更多图书类型
            default:
                throw new IllegalArgumentException("不支持的图书类型: " + category);
        }
    }
} 