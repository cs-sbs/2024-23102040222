package com.flash.dao;

import com.flash.factory.BookFactory;
import com.flash.pojo.Book;
import com.flash.pojo.ComputerBook;
import com.flash.pojo.LiteratureBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书数据访问对象，提供图书相关的数据库操作。
 */
public class BookDAO {
    private Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 添加图书。
     */
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, category, publish_date, extra_info) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getPublishDate());
            
            // 根据不同类型的图书获取额外信息
            String extraInfo = "";
            if (book instanceof ComputerBook) {
                extraInfo = ((ComputerBook) book).getProgrammingLanguage();
            } else if (book instanceof LiteratureBook) {
                extraInfo = ((LiteratureBook) book).getGenre();
            }
            stmt.setString(6, extraInfo);
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * 根据分类查询图书。
     */
    public List<Book> getBooksByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM books WHERE category = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, category);
        ResultSet resultSet = statement.executeQuery();
        List<Book> books = new ArrayList<>();
        while (resultSet.next()) {
            books.add(createBookFromResultSet(resultSet));
        }
        return books;
    }

    /**
     * 删除图书。
     */
    public boolean deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        return statement.executeUpdate() > 0;
    }

    /**
     * 获取所有图书。
     */
    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM books";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        List<Book> books = new ArrayList<>();
        while (resultSet.next()) {
            books.add(createBookFromResultSet(resultSet));
        }
        return books;
    }

    /**
     * 按书名搜索图书
     */
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "%" + title + "%");
        return executeBookQuery(statement);
    }

    /**
     * 按ISBN搜索图书
     */
    public List<Book> searchBooksByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn LIKE ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "%" + isbn + "%");
        return executeBookQuery(statement);
    }

    /**
     * 执行查询并返回图书列表
     */
    private List<Book> executeBookQuery(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        List<Book> books = new ArrayList<>();
        while (resultSet.next()) {
            books.add(createBookFromResultSet(resultSet));
        }
        return books;
    }

    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        return BookFactory.createBook(
            rs.getString("category"),
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("publish_date"),
            rs.getString("extra_info")
        );
    }
}
