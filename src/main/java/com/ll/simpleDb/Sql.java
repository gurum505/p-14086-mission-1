package com.ll.simpleDb;

import com.ll.Article;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
    }

    public Sql append(String sql, Object... params) {
        _sql.append(" ");
        _sql.append(sql);
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

    public String get_sql() {
        return _sql.toString();
    }

    // ===================================
    // DDL API 영역
    // simpleDb.run 사용
    // ===================================

    public long insert() {
        return (long) simpleDb.run(this);
    }

    public int update() {
        return simpleDb.run(this);
    }

    public int delete() {
        return simpleDb.run(this);
    }

    // ===================================
    // DML Public API 영역
    // simpleDb.selectCommon 사용
    // mapper를 삽입해 적절한 리턴값으로 변환한다.
    // ===================================

    public List<Map<String, Object>> selectRows() {
        return simpleDb.selectCommon(this, resultSet -> {
            try {
                return selectRowsMapper(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> List<T> selectRows(Class<T> cls) {
        if (cls == Article.class) {
            return simpleDb.selectCommon(this, resultSet -> {
                        try {
                            return selectRowsMapper(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .stream().map(row -> cls.cast(new Article(row))).toList();
        }
        return null;
    }

    public Map<String, Object> selectRow() {
        return selectRows().getFirst();
    }

    public <T> T selectRow(Class<T> cls) {
        return selectRows(cls).getFirst();
    }

    public List<Long> selectLongs() {
        return simpleDb.selectCommon(this, resultSet -> {
            try {
                return selectLongsMapper(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Long selectLong() {
        return selectSingle(Long.class);
    }

    public LocalDateTime selectDatetime() {
        return selectSingle(LocalDateTime.class);
    }

    public String selectString() {
        return selectSingle(String.class);
    }

    public Boolean selectBoolean() {
        return selectSingle(Boolean.class);
    }

    // ===================================
    // private 영역
    // simpleDb.selectCommon 에 사용되는 Mapper
    // <T> T selectSingle
    // ===================================
    private <T> T selectSingle(Class<T> cls) {
        Object object = simpleDb.selectCommon(this, (resultSet -> {
            try {
                return selectSingleMapper(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));

        if (cls == Boolean.class && object instanceof Number) {
            return ((Number) object).intValue() == 1 ? (T) Boolean.TRUE : (T) Boolean.FALSE;
        }
        if (cls.isInstance(object)) {
            return cls.cast(object);
        }
        return null;
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

    private Object selectSingleMapper(ResultSet rs) throws SQLException {
        Object object = null;
        if (rs.next()) {
            object = rs.getObject(1);
        }
        return object;
    }


}
