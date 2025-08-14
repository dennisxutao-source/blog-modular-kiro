package com.blog.persistence.repository.impl;

import com.blog.persistence.entity.ArticleEntity;
import com.blog.persistence.repository.ArticleRepository;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryArticleRepository implements ArticleRepository {
    private final Map<Long, ArticleEntity> articles = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public List<ArticleEntity> findAll() {
        return new ArrayList<>(articles.values());
    }

    @Override
    public Optional<ArticleEntity> findById(Long id) {
        return Optional.ofNullable(articles.get(id));
    }

    @Override
    public ArticleEntity save(ArticleEntity article) {
        if (article.getId() == null) {
            article.setId(nextId++);
            article.setCreatedAt(LocalDateTime.now());
            article.setUpdatedAt(LocalDateTime.now());
        } else {
            article.setUpdatedAt(LocalDateTime.now());
        }
        articles.put(article.getId(), article);
        return article;
    }

    @Override
    public void deleteById(Long id) {
        articles.remove(id);
    }
}