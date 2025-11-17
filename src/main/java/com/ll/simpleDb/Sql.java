package com.ll.simpleDb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sql {
    private String _sql;
    public List<Object> _values;

    public Sql (String sql) {
        _sql = sql;
        _values= new ArrayList<>();
        _values.add(null);
    }
    public Sql (String sql, List<Object> values) {
        this._sql = sql;
        this._values= values;
    }

    public Sql append(String s1,Object... args){
        _sql += s1;
        _values.addAll(Arrays.asList(args));
        return new Sql(_sql);
    }

    public long insert() {
        //int rs = SimpleDb.run(_sql);
        return 1;
    }

    public int update() {
        return 1;
    }

    public int delete() {
        return 1;
    }
}
