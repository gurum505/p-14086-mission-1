package com.ll.simpleDb;

import java.sql.*;
import java.time.LocalDateTime;
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

    public <T> T temp(Sql _sql, Function<ResultSet, T> mapper) {
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


    public List<Map<String, Object>> selectRows(Sql _sql) {
        return temp(_sql, resultSet -> {
            try {
                return selectRowsMapper(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Map<String, Object>> selectRowsMapper(ResultSet rs) throws SQLException {
        List<Map<String, Object>> data = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnName(i), rs.getObject(i));
            }
            data.add(row);
        }
        return data;
    }

    public Object selectSingle(Sql _sql) {
        return temp(_sql, (resultSet -> {
            try {
                return selectSingleMapper(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private Object selectSingleMapper(ResultSet rs) throws SQLException {
        Object object = null;
        if (rs.next()) {
            object = rs.getObject(1);
        }
        return object;
    }

    public List<Long> selectLongs(Sql _sql) {
        return temp(_sql, resultSet -> {
            try {
                return selectLongsMapper(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Long> selectLongsMapper(ResultSet rs) throws SQLException {
        List<Long> cnt = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                cnt.add(rs.getLong(i));
            }
        }
        return cnt;
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
}
