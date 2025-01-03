package com.flash.ui;

import com.flash.dao.BookDAO;
import com.flash.dao.UserDAO;
import com.flash.factory.BookFactory;
import com.flash.pojo.Book;
import com.flash.pojo.User;
import com.flash.utils.DatabaseUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * 菜单处理类，负责处理用户界面交互
 * 实现了图书管理系统的所有用户交互功能
 * 
 * @author your-name
 * @version 1.0
 */
public class MenuHandler {
    /** 用于处理用户输入 */
    private final Scanner scanner;
    /** 用户数据访问对象 */
    private final UserDAO userDAO;
    /** 图书数据访问对象 */
    private final BookDAO bookDAO;
    /** 当前登录用户 */
    private User currentUser;

    /**
     * 构造函数，初始化菜单处理器
     * 
     * @param userDAO 用户数据访问对象
     * @param bookDAO 图书数据访问对象
     */
    public MenuHandler(UserDAO userDAO, BookDAO bookDAO) {
        this.scanner = new Scanner(System.in);
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }

    /**
     * 显示主菜单并处理用户输入
     * 这是系统的主要交互入口
     */
    public void showMainMenu() {
        while (true) {
            clearScreen();
            displayMainMenuOptions();
            
            try {
                handleMainMenuChoice();
            } catch (InputMismatchException e) {
                handleInputError();
            } catch (SQLException e) {
                handleDatabaseError(e);
            }
        }
    }

    /**
     * 显示主菜单选项
     */
    private void displayMainMenuOptions() {
        System.out.println("=== 图书管理系统 ===");
        System.out.println("--------------------");
        if (currentUser == null) {
            displayGuestOptions();
        } else {
            displayUserOptions();
        }
        System.out.println("8. 退出系统");
        System.out.println("--------------------");
        System.out.print("请输入选择 (1-8): ");
    }

    /**
     * 显示访客可用的选项
     */
    private void displayGuestOptions() {
        System.out.println("1. 用户注册");
        System.out.println("2. 用户登录");
    }

    /**
     * 显示已登录用户可用的选项
     */
    private void displayUserOptions() {
        System.out.println("当前用户：" + currentUser.getUsername() + 
                         " (" + (isAdmin() ? "管理员" : "普通用户") + ")");
        System.out.println("--------------------");
        System.out.println("3. 查看图书");
        System.out.println("4. 搜索图书");
        if (isAdmin()) {
            System.out.println("5. 添加图书");
            System.out.println("6. 删除图书");
        }
        System.out.println("7. 退出登录");
    }

    private boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    private void register() throws SQLException {
        System.out.print("请输入用户名：");
        String username = scanner.nextLine();
        System.out.print("请输入密码：");
        String password = scanner.nextLine();
        
        User newUser = new User(username, password, "user"); // 默认注册为普通用户
        if (userDAO.registerUser(newUser)) {
            System.out.println("注册成功！");
        } else {
            System.out.println("注册失败！");
        }
    }

    private void login() throws SQLException {
        System.out.print("请输入用户名：");
        String username = scanner.nextLine();
        System.out.print("请输入密码：");
        String password = scanner.nextLine();

        currentUser = userDAO.loginUser(username, password);
        if (currentUser != null) {
            System.out.println("登录成功！欢迎 " + currentUser.getUsername());
        } else {
            System.out.println("登录失败！用户名或密码错误。");
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("已退出登录！");
    }

    private void viewBooks() throws SQLException {
        while (true) {
            clearScreen();
            System.out.println("=== 图书查看 ===");
            System.out.println("----------------");
            System.out.println("1. 查看所有图书");
            System.out.println("2. 按分类查看");
            System.out.println("3. 返回主菜单");
            System.out.println("----------------");
            System.out.print("请输入选择 (1-3): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 3) return;
            
            switch (choice) {
                case 1:
                    clearScreen();
                    System.out.println("=== 所有图书 ===");
                    List<Book> allBooks = bookDAO.getAllBooks();
                    displayBooks(allBooks);
                    break;
                case 2:
                    clearScreen();
                    System.out.println("=== 分类查看 ===");
                    System.out.print("请输入图书分类：");
                    String category = scanner.nextLine();
                    List<Book> books = bookDAO.getBooksByCategory(category);
                    displayBooks(books);
                    break;
                default:
                    System.out.println("无效的选择！");
            }
            pressEnterToContinue();
        }
    }

    private void searchBooks() throws SQLException {
        while (true) {
            clearScreen();
            System.out.println("=== 图书搜索 ===");
            System.out.println("----------------");
            System.out.println("1. 按书名搜索");
            System.out.println("2. 按ISBN搜索");
            System.out.println("3. 返回主菜单");
            System.out.println("----------------");
            System.out.print("请输入选择 (1-3): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 3) return;
            
            clearScreen();
            switch (choice) {
                case 1:
                    System.out.println("=== 书名搜索 ===");
                    System.out.println("----------------");
                    System.out.print("请输入书名关键字：");
                    String title = scanner.nextLine();
                    List<Book> booksByTitle = bookDAO.searchBooksByTitle(title);
                    displayBooks(booksByTitle);
                    break;
                case 2:
                    System.out.println("=== ISBN搜索 ===");
                    System.out.println("----------------");
                    System.out.print("请输入ISBN：");
                    String isbn = scanner.nextLine();
                    List<Book> booksByIsbn = bookDAO.searchBooksByIsbn(isbn);
                    displayBooks(booksByIsbn);
                    break;
                default:
                    System.out.println("无效的选择！");
            }
            pressEnterToContinue();
        }
    }

    private void addBook() throws SQLException {
        clearScreen();
        System.out.println("=== 添加图书 ===");
        System.out.println("----------------");
        System.out.println("1. 添加计算机类图书");
        System.out.println("2. 添加文学类图书");
        System.out.println("3. 返回主菜单");
        System.out.println("----------------");
        System.out.print("请选择图书类型 (1-3): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice == 3) return;
        
        System.out.print("书名: ");
        String title = scanner.nextLine();
        
        System.out.print("作者: ");
        String author = scanner.nextLine();
        
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        
        System.out.print("出版日期 (yyyy-MM-dd): ");
        String publishDate = scanner.nextLine();
        
        String category;
        String extraInfo;
        
        switch (choice) {
            case 1:
                category = "计算机";
                System.out.print("编程语言: ");
                extraInfo = scanner.nextLine();
                break;
            case 2:
                category = "文学";
                System.out.print("文学流派: ");
                extraInfo = scanner.nextLine();
                break;
            default:
                System.out.println("无效的选择！");
                return;
        }
        
        Book newBook = BookFactory.createBook(
            category, 0, title, author, isbn, publishDate, extraInfo
        );
        
        if (bookDAO.addBook(newBook)) {
            System.out.println("图书添加成功！");
        } else {
            System.out.println("图书添加失败！");
        }
    }

    private void deleteBook() throws SQLException {
        while (true) {
            clearScreen();
            System.out.println("=== 删除图书 ===");
            System.out.println("----------------");
            System.out.println("1. 查看所有图书");
            System.out.println("2. 输入ID删除");
            System.out.println("3. 返回主菜单");
            System.out.println("----------------");
            System.out.print("请输入选择 (1-3): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 3) return;
            
            switch (choice) {
                case 1:
                    clearScreen();
                    System.out.println("=== 所有图书 ===");
                    List<Book> allBooks = bookDAO.getAllBooks();
                    displayBooks(allBooks);
                    pressEnterToContinue();
                    break;
                case 2:
                    clearScreen();
                    System.out.println("=== 删除图书 ===");
                    System.out.println("----------------");
                    System.out.print("请输入要删除的图书ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    
                    System.out.println("----------------");
                    System.out.print("确认删除ID为 " + id + " 的图书？(Y/N): ");
                    String confirm = scanner.nextLine();
                    
                    if (confirm.equalsIgnoreCase("Y")) {
                        if (bookDAO.deleteBook(id)) {
                            System.out.println("图书删除成功！");
                        } else {
                            System.out.println("图书删除失败！可能图书ID不存在。");
                        }
                    } else {
                        System.out.println("已取消删除。");
                    }
                    pressEnterToContinue();
                    break;
                default:
                    System.out.println("无效的选择！");
                    pressEnterToContinue();
            }
        }
    }

    private void displayBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("\n没有找到任何图书！");
            return;
        }
        
        System.out.println("\n找到 " + books.size() + " 本图书：");
        System.out.println("----------------------------------------" +
                         "----------------------------------------");
        System.out.printf("%-4s | %-20s | %-15s | %-15s | %-10s | %-10s\n",
                "ID", "书名", "作者", "ISBN", "分类", "出版日期");
        System.out.println("----------------------------------------" +
                         "----------------------------------------");
        
        for (Book book : books) {
            System.out.printf("%-4d | %-20s | %-15s | %-15s | %-10s | %-10s\n",
                    book.getId(),
                    truncateString(book.getTitle(), 20),
                    truncateString(book.getAuthor(), 15),
                    truncateString(book.getIsbn(), 15),
                    truncateString(book.getCategory(), 10),
                    book.getPublishDate());
        }
        System.out.println("----------------------------------------" +
                         "----------------------------------------");
    }

    /**
     * 处理用户输入错误
     */
    private void handleInputError() {
        System.out.println("输入无效！请输入数字。");
        scanner.nextLine(); // 清除错误输入
        pressEnterToContinue();
    }

    /**
     * 处理数据库错误
     * 
     * @param e SQL异常
     */
    private void handleDatabaseError(SQLException e) {
        System.out.println("数据库操作失败：" + e.getMessage());
        pressEnterToContinue();
    }

    /**
     * 检查字符串是否为空或null
     * 
     * @param str 要检查的字符串
     * @return 如果字符串为空或null返回true，否则返回false
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 验证输入的日期格式是否正确
     * 
     * @param date 日期字符串
     * @return 如果格式正确返回true，否则返回false
     */
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    /**
     * 验证ISBN格式是否正确
     * 
     * @param isbn ISBN字符串
     * @return 如果格式正确返回true，否则返回false
     */
    private boolean isValidISBN(String isbn) {
        return isbn.matches("\\d{10}|\\d{13}");
    }

    /**
     * 截断字符串到指定长度
     * 
     * @param str 要截断的字符串
     * @param length 目标长度
     * @return 截断后的字符串
     */
    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    /**
     * 清除控制台屏幕
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        // 备选方案
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * 等待用户按回车键继续
     */
    private void pressEnterToContinue() {
        System.out.println("\n按回车键继续...");
        scanner.nextLine();
    }

    /**
     * 处理主菜单选择
     */
    private void handleMainMenuChoice() throws SQLException {
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        if (currentUser == null && (choice > 2 && choice < 7)) {
            System.out.println("请先登录！");
            pressEnterToContinue();
            return;
        }

        switch (choice) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                viewBooks();
                break;
            case 4:
                searchBooks();
                break;
            case 5:
                if (isAdmin()) {
                    addBook();
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 6:
                if (isAdmin()) {
                    deleteBook();
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 7:
                logout();
                break;
            case 8:
                System.out.println("感谢使用！再见！");
                DatabaseUtil.shutdown();
                System.exit(0);
            default:
                System.out.println("无效的选择，请重试！");
        }
        pressEnterToContinue();
    }

    // 其他方法实现...
} 