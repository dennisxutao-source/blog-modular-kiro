package com.blog.api.service;

import com.blog.api.model.Article;
import java.util.List;
import java.util.Optional;

public interface ArticleService {
    List<Article> getAllArticles();
    Optional<Article> getArticleById(Long id);
    Article createArticle(Article article);
    Article updateArticle(Long id, Article article);
    void deleteArticle(Long id);
}