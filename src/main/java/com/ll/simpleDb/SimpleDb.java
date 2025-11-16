package com.ll.simpleDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public void connect(String sql) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rs = pstmt.executeUpdate();

        } catch (SQLException e) {
        }
    }


    public void setDevMode(boolean b) {
    }

    public void run(String s) {
        connect(s);
    }

    public void run(String sql, String title, String body, boolean isBlind) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,title);
            pstmt.setString(2,body);
            pstmt.setBoolean(3,isBlind);

            int rs = pstmt.executeUpdate();

        } catch (SQLException e) {
        }
    }

    public Sql genSql() {

        return new Sql("");
    }


}
