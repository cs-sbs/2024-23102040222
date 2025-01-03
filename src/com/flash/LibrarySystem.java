package com.flash;

import com.flash.dao.BookDAO;
import com.flash.dao.UserDAO;
import com.flash.ui.MenuHandler;
import com.flash.utils.DatabaseUtil;

import java.sql.Connection;

public class LibrarySystem {
    public static void main(String[] args) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            BookDAO bookDAO = new BookDAO(connection);
            
            MenuHandler menuHandler = new MenuHandler(userDAO, bookDAO);
            menuHandler.showMainMenu();
            
        } catch (Exception e) {
            System.out.println("系统错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
