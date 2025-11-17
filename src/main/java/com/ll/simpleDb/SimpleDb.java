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

    public SimpleDb(String localhost, String username, String password, String dbName) {
        this.localhost = localhost;
        this.username = username;
        this.password = password;
        this.dbName = dbName;

        this.url = "jdbc:mysql://" + localhost + ":3306/" + dbName +
                "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul";
    }

    public int run(Sql _sql) {
        return execute(_sql);
    }

    public int execute(Sql _sql) {
        int rs = 0;
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
            rs = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
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
            ResultSet rs = pstmt.executeQuery(_sql.get_sql());
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

    //test
    public int run(String _sql) {
        int rs = 0;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql)) {
            rs = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    //test
    public void run(String sql, String title, String body, boolean isBlind) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, body);
            pstmt.setBoolean(3, isBlind);

            int rs = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void setDevMode(boolean b) {

    }

    public Sql genSql() {
        return new Sql(this);
    }


    public Map<String, Object> selectRow(Sql _sql) {
        Map<String, Object> row = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(_sql.get_sql())) {

            ResultSet rs = pstmt.executeQuery(_sql.get_sql());
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

            ResultSet rs = pstmt.executeQuery(_sql.get_sql());
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

            ResultSet rs = pstmt.executeQuery(_sql.get_sql());
            while (rs.next()) {
                cnt++;
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

            ResultSet rs = pstmt.executeQuery(_sql.get_sql());
            while (rs.next()) {
                if (rs.getObject(1) instanceof String) {
                    s = (String) rs.getObject(1);
                }
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

            ResultSet rs = pstmt.executeQuery(_sql.get_sql());
            while (rs.next()) {
                if (rs.getObject(1) instanceof Boolean) {
                    b = (Boolean) rs.getObject(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return b;
    }
}
