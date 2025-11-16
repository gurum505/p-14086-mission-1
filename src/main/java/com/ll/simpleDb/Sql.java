package com.ll.simpleDb;

public class Sql {
    private String _sql;

    public Sql (String s) {
        _sql = s;
    }

    public long insert() {
        //int rs = SimpleDb.run(_sql);
        return 1;
    }

    public Sql append(String s){
        _sql += s;
        return new Sql(_sql);
    }

    public Sql append(String s1,String s2){
        _sql += s1;
        _sql += s2;
        return new Sql(_sql);
    }

}
