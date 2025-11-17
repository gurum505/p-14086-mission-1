package com.ll.simpleDb;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        // 공백 누락 방지: 이전 문자열 끝에 공백이 없고, 추가할 문자열 앞에 공백이 없다면 공백 추가
        if (!_sql.isEmpty() && !sql.startsWith(" ") && !_sql.toString().endsWith(" ")) {
            _sql.append(" ");
        }
        _sql.append(sql);
        return this;
    }

    public Sql append(String sql, Object... params) {
        append(sql); // 1번 메서드를 호출하여 SQL 문자열과 공백을 안전하게 추가

        // 파라미터 값을 리스트에 추가 (JDBC 바인딩을 위한 준비)
        if (params != null) {
            this._values.addAll(Arrays.asList(params));
        }
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

    public Map<String, Object> selectRow() {
        return simpleDb.selectRow(this);
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
}
