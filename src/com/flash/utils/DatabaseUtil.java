package com.flash.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库工具类，负责管理数据库连接
 */
public class DatabaseUtil {
    private static final Properties CONFIG = new Properties();
    private static final String CONFIG_FILE = "database.properties";
    
    /** 数据库连接配置 */
    private static String url;
    private static String user;
    private static String password;
    private static String driverClass;
    
    static {
        loadConfig();
        loadDriver();
    }
    
    /**
     * 加载数据库配置
     */
    private static void loadConfig() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                // 如果配置文件不存在，使用默认配置
                url = "jdbc:mysql://localhost:3307/library_database?useSSL=false&serverTimezone=UTC";
                user = "root";
                password = "123456";
                driverClass = "com.mysql.cj.jdbc.Driver";
            } else {
                CONFIG.load(input);
                url = CONFIG.getProperty("db.url", 
                    "jdbc:mysql://localhost:3306/library_database?useSSL=false&serverTimezone=UTC");
                user = CONFIG.getProperty("db.user", "root");
                password = CONFIG.getProperty("db.password", "123456");
                driverClass = CONFIG.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            }
        } catch (IOException e) {
            throw new RuntimeException("加载数据库配置文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 加载数据库驱动
     */
    private static void loadDriver() {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL驱动加载失败: " + e.getMessage(), e);
        }
    }

    private DatabaseUtil() {
        // 工具类不应该被实例化
    }

    /**
     * 创建新的数据库连接
     */
    static Connection createConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("useUnicode", "true");
        props.setProperty("characterEncoding", "UTF-8");
        props.setProperty("serverTimezone", "UTC");
        props.setProperty("autoReconnect", "true");
        props.setProperty("failOverReadOnly", "false");
        props.setProperty("maxReconnects", "3");
        
        return DriverManager.getConnection(url, props);
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection();
    }

    /**
     * 释放数据库连接到连接池
     */
    public static void releaseConnection(Connection connection) {
        ConnectionPool.getInstance().releaseConnection(connection);
    }

    /**
     * 关闭连接池和清理资源
     * 
     * @throws SQLException 如果关闭过程中发生错误
     */
    public static void shutdown() throws SQLException {
        try {
            ConnectionPool.getInstance().shutdown();
        } catch (SQLException e) {
            throw new SQLException("关闭数据库连接池失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查数据库连接是否可用
     * 
     * @return 如果连接可用返回true，否则返回false
     */
    public static boolean isConnectionAvailable() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
