package com.flash.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据库连接池，实现连接的复用和管理
 */
public class ConnectionPool {
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    
    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    
    private ConnectionPool() {
        connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            try {
                connectionPool.add(createConnection());
            } catch (SQLException e) {
                throw new RuntimeException("初始化连接池失败", e);
            }
        }
    }
    
    public static ConnectionPool getInstance() {
        return INSTANCE;
    }
    
    public Connection getConnection() throws SQLException {
        lock.lock();
        try {
            if (connectionPool.isEmpty()) {
                if (usedConnections.size() < MAX_POOL_SIZE) {
                    connectionPool.add(createConnection());
                } else {
                    throw new SQLException("没有可用的数据库连接");
                }
            }
            
            Connection connection = connectionPool.remove(connectionPool.size() - 1);
            usedConnections.add(connection);
            return connection;
        } finally {
            lock.unlock();
        }
    }
    
    public boolean releaseConnection(Connection connection) {
        lock.lock();
        try {
            if (connection != null && !connection.isClosed()) {
                connectionPool.add(connection);
                return usedConnections.remove(connection);
            }
            return false;
        } catch (SQLException e) {
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    private static Connection createConnection() throws SQLException {
        return DatabaseUtil.createConnection();
    }
    
    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }
    
    public int getAvailableConnections() {
        return connectionPool.size();
    }
    
    public void shutdown() throws SQLException {
        lock.lock();
        try {
            for (Connection conn : usedConnections) {
                releaseConnection(conn);
            }
            for (Connection conn : connectionPool) {
                conn.close();
            }
            connectionPool.clear();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 检查并清理无效连接
     */
    public void maintainPool() {
        lock.lock();
        try {
            List<Connection> invalidConnections = new ArrayList<>();
            
            // 检查空闲连接
            for (Connection conn : connectionPool) {
                try {
                    if (conn.isClosed() || !conn.isValid(1)) {
                        invalidConnections.add(conn);
                    }
                } catch (SQLException e) {
                    invalidConnections.add(conn);
                }
            }
            
            // 移除无效连接
            connectionPool.removeAll(invalidConnections);
            
            // 补充连接到初始大小
            while (connectionPool.size() < INITIAL_POOL_SIZE) {
                try {
                    connectionPool.add(createConnection());
                } catch (SQLException e) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 获取连接池状态信息
     */
    public String getPoolStatus() {
        lock.lock();
        try {
            return String.format(
                "连接池状态 - 总连接数: %d, 可用连接: %d, 使用中: %d",
                getSize(),
                connectionPool.size(),
                usedConnections.size()
            );
        } finally {
            lock.unlock();
        }
    }
} 