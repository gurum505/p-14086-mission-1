package com.ll.simpleDb;

import com.ll.Article;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Sql {
    private final SimpleDb simpleDb;
    private StringBuilder _sql;
    private List<Object> _values;

    public Sql(SimpleDb simpleDb) {
        this.simpleDb = simpleDb;
        this._sql = new StringBuilder();
        this._values = new ArrayList<>();
        this._values.add(null);
    }

    public Sql append(String sql) {
        if (!_sql.isEmpty() && !sql.startsWith(" ") && !_sql.toString().endsWith(" ")) {
            _sql.append(" ");
        }
        _sql.append(sql);
        return this;
    }

    public Sql append(String sql, Object... params) {
        append(sql);
        if (params != null) {
            this._values.addAll(Arrays.asList(params));
        }
        return this;
    }

    public Sql appendIn(String sql, Object... params) {
        if (params != null) {
            sql = sql.replaceFirst("\\?", String.join(",", Collections.nCopies(params.length, "?")));
            this._values.addAll(Arrays.asList(params));
        }
        append(sql);
        return this;
    }

    public long insert() {
//        System.out.println("== rawSql ==");
//        System.out.println(_sql);
        return simpleDb.run(this);
    }

    public int update() {
//        System.out.println("== rawSql ==");
//        System.out.println(_sql);
        return simpleDb.run(this);
    }

    public int delete() {
//        System.out.println("== rawSql ==");
//        System.out.println(_sql);
        return simpleDb.run(this);
    }

    public String getParamString(int index) {
        return String.valueOf(_values.get(index));
    }

    public int getParamInt(int index) {
        return Integer.parseInt(String.valueOf(_values.get(index)));
    }


    public List<Map<String, Object>> selectRows() {
        return simpleDb.selectRows(this);
    }

    public <T> List<T> selectRows(Class<T> cls) {
        return simpleDb.selectRows(this)
                .stream().map(row -> (T) new Article(row)).collect(Collectors.toList());
    }

    public Map<String, Object> selectRow() {
        return simpleDb.selectRow(this);
    }

    public <T> T selectRow(Class<T> cls) {
        return (T) new Article(simpleDb.selectRow(this));
    }

    public LocalDateTime selectDatetime() {

        return simpleDb.selectDateTime(this);
    }

    public Long selectLong() {
        return simpleDb.selectLong(this);
    }

    public String selectString() {
        return simpleDb.selectString(this);
    }

    public Boolean selectBoolean() {
        return simpleDb.selectBoolean(this);
    }

    public String get_sql() {
        return _sql.toString();
    }

    public List<Long> selectLongs() {
        return simpleDb.selectLongs(this);
    }
}
