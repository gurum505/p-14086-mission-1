package com.ll.simpleDb;

public class SimpleDb {
    private String localhost;
    private String username;
    private String password;
    private String dbName;

    public SimpleDb(String localhost, String username, String password, String dbName) {
        this.localhost = localhost;
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    public void setDevMode(boolean b) {
    }

    public void run(String s){}

    public void run(String s, String title, String body, boolean isBlind) {
    }

    public Sql genSql() {
        return new Sql("");
    }
}
