package com.ll.simpleDb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    }

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

    public void setDevMode(boolean b) {

    }

    public Sql genSql() {
        return new Sql(this);
    }

    public int run(Sql _sql) {
        return run(_sql.get_sql(), _sql.get_values().toArray());
    }

    public int run(String sql, Object... params) {
        int rs = 0;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                setParam(pstmt, i + 1, params[i]);
            }
            rs = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

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


    public List<Map<String, Object>> selectRows(Sql _sql) {
        List<Map<String, Object>> data = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            List<Object> _sqlParams = _sql.get_values();
            for (int i = 1; i < _sqlParams.size(); i++) {
                Object arg = _sqlParams.get(i);
                if (arg instanceof String) {
                    pstmt.setString(i, (String) arg);
                } else if (arg instanceof Integer) {
                    pstmt.setInt(i, (int) arg);
                }
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.put(rsmd.getColumnName(i), rs.getObject(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return data;
    }


    public Map<String, Object> selectRow(Sql _sql) {
        Map<String, Object> row = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.put(rsmd.getColumnName(i), rs.getObject(i));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return row;
    }

    public LocalDateTime selectDateTime(Sql _sql) {
        LocalDateTime ldt = null;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                if (rs.getObject(1) instanceof LocalDateTime) {
                    ldt = (LocalDateTime) rs.getObject(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ldt;
    }

    public Long selectLong(Sql _sql) {
        long cnt = 0L;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            List<Object> _sqlParams = _sql.get_values();
            for (int i = 1; i < _sqlParams.size(); i++) {
                Object arg = _sqlParams.get(i);
                if (arg instanceof String) {
                    pstmt.setString(i, (String) arg);
                } else if (arg instanceof Integer) {
                    pstmt.setInt(i, (int) arg);
                }
            }
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                cnt = rs.getLong(1);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cnt;
    }

    public String selectString(Sql _sql) {
        String s = "";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                s = rs.getString(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    public Boolean selectBoolean(Sql _sql) {
        Boolean b = null;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                b = rs.getBoolean(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return b;
    }

    public List<Long> selectLongs(Sql _sql) {
        List<Long> cnt = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            List<Object> _sqlParams = _sql.get_values();
            for (int i = 1; i < _sqlParams.size(); i++) {
                Object arg = _sqlParams.get(i);
                if (arg instanceof String) {
                    pstmt.setString(i, (String) arg);
                } else if (arg instanceof Integer) {
                    pstmt.setInt(i, (int) arg);
                } else if (arg instanceof Long) {
                    pstmt.setLong(i, (long) arg);
                }
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    cnt.add(rs.getLong(i));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cnt;
    }

    public void close() {
    }

    public void startTransaction() {

    }

    public void rollback() {
    }

    public void commit() {
    }
}
