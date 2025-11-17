package com.ll.simpleDb;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class Sql {
    private final SimpleDb simpleDb;
    private String _sql;
    private List<Object> _values;

    public Sql (SimpleDb simpleDb) {
        this.simpleDb = simpleDb;
        this._sql = "";
        this._values= new ArrayList<>();
        this._values.add(null);
    }

    public Sql append(String s1,Object... args){
        _sql += s1 + " ";
        _values.addAll(Arrays.asList(args));
        return this;
    }

    public long insert() {
        System.out.println("== rawSql ==");
        System.out.println(_sql);
        return simpleDb.run(this);
    }

    public int update() {
        System.out.println("== rawSql ==");
        System.out.println(_sql);
        return simpleDb.run(this);
    }

    public int delete() {
        System.out.println("== rawSql ==");
        System.out.println(_sql);
        return simpleDb.run(this);
    }

    public String getParamString(int index){
        return String.valueOf(_values.get(index));
    }

    public int getParamInt(int index){
        return Integer.parseInt(String.valueOf(_values.get(index)));
    }


}
