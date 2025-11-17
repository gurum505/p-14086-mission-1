package com.ll.simpleDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
            for(int i = 1; i < _sqlParams.size() ;i++){
                Object arg = _sqlParams.get(i);
                if (arg instanceof String){
                    pstmt.setString(i,(String)arg);
                }else if( arg instanceof Integer){
                    pstmt.setInt(i,(int)arg);
                }
            }
            rs = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
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
            pstmt.setString(1,title);
            pstmt.setString(2,body);
            pstmt.setBoolean(3,isBlind);

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


}
