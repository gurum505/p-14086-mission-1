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
        return (long) simpleDb.run(this);
    }

    public int update() {
        return simpleDb.run(this);
    }

    public int delete() {
        return simpleDb.run(this);
    }

    public Map<String, Object> selectRow() {
        return simpleDb.selectRow(this);
    }

    public <T> T selectRow(Class<T> cls) {
        if (cls == Article.class) {
            return cls.cast(new Article(simpleDb.selectRow(this)));
        }
        return null;
    }

    public List<Map<String, Object>> selectRows() {
        return simpleDb.selectRows(this);
    }

    public <T> List<T> selectRows(Class<T> cls) {
        if (cls == Article.class) {
            return simpleDb.selectRows(this)
                    .stream().map(row -> cls.cast(new Article(row))).toList();
        }
        return null;
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

    public Long selectLong() {
        return selectSingle(Long.class);
    }

    public List<Long> selectLongs() {
        return simpleDb.selectLongs(this);
    }

    private <T> T selectSingle(Class<T> cls) {
        Object object = simpleDb.selectSingle(this);
        if (cls == Boolean.class && object instanceof Number) {
            return ((Number) object).intValue() == 1 ? (T) Boolean.TRUE : (T) Boolean.FALSE;
        }
        if (cls.isInstance(object)) {
            return cls.cast(object);
        }
        return null;
    }

    public String get_sql() {
        return _sql.toString();
    }
}
