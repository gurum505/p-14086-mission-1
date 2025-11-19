package com.ll.simpleDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SimpleDb {
    private final String localhost;
    private final String username;
    private final String password;
    private final String dbName;
    private final String url;
    private Map<String, Connection> connections;

    public SimpleDb(String localhost, String username, String password, String dbName) {
        this.localhost = localhost;
        this.username = username;
        this.password = password;
        this.dbName = dbName;

        this.url = "jdbc:mysql://" + localhost + ":3306/" + dbName +
                "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul";
        this.connections = new HashMap<>();

    }

    public Sql genSql() {
        return new Sql(this);
    }

    public void setDevMode(boolean b) {
    }

    // ===================================
    // CREATE, INSERT, DELETE Public API 영역
    // 초기 데이터 생성 및 테스트
    // ===================================

    public int run(Sql _sql) {
        return run(_sql.get_sql(), _sql.get_values().toArray());
    }

    public int run(String sql, Object... params) {
        int rs = 0;
        Connection conn = getCurrentThreadConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                setParam(pstmt, i + 1, params[i]);
            }
            rs = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
        return rs;
    }

    // ===================================
    // SELCECT Public API 영역
    // 데이터 조회
    // ===================================

    public <T> T selectCommon(Sql _sql, Function<ResultSet, T> mapper) {
        T object = null;
        Connection conn = getCurrentThreadConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            List<Object> _sqlParams = _sql.get_values();
            for (int i = 0; i < _sqlParams.size(); i++) {
                setParam(pstmt, i + 1, _sqlParams.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            object = mapper.apply(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
        return object;
    }

    // ===================================
    // Connection 영역
    // connection 연결 및 리소스 해제 수동조작
    // ===================================

    private Connection getCurrentThreadConnection() {
        Connection conn = connections.get(Thread.currentThread().getName());
        if (conn != null) {
            return conn;
        }
        try {
            conn = DriverManager.getConnection(this.url, this.username, this.password);
            connections.put(Thread.currentThread().getName(), conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public void close() {
        try {
            Connection conn = getCurrentThreadConnection();
            if (!conn.getAutoCommit()) {
                return;
            }
            connections.remove(Thread.currentThread().getName());
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void startTransaction() {
        try {
            Connection conn = getCurrentThreadConnection();
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            Connection conn = getCurrentThreadConnection();
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    public void commit() {
        try {
            Connection conn = getCurrentThreadConnection();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    // ===================================
    // pstmt 세팅
    // ?에 param의 요소 순차적으로 삽입
    // ===================================

    private void setParam(PreparedStatement pstmt, int index, Object param) throws SQLException {
        if (param instanceof String) {
            pstmt.setString(index, (String) param);
        } else if (param instanceof Integer) {
            pstmt.setInt(index, (int) param);
        } else if (param instanceof Long) {
            pstmt.setLong(index, (long) param);
        } else if (param instanceof Boolean) {
            pstmt.setBoolean(index, (boolean) param);
        }
    }
}
