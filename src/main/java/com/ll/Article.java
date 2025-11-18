package com.ll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private long id;
    private String title;
    private String body;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Boolean isBlind;

    public Article(Map<String, Object> row) {
        this.id = (long) row.get("id");
        this.title = (String) row.get("title");
        this.body = (String) row.get("body");
        this.createdDate = (LocalDateTime) row.get("createdDate");
        this.modifiedDate = (LocalDateTime) row.get("modifiedDate");
        this.isBlind = (Boolean) row.get("isBlind");
    }

    public Boolean isBlind() {
        return isBlind;
    }
}
