package me.seeun.blogdev.dto;

import lombok.Getter;
import me.seeun.blogdev.domain.Article;

@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
