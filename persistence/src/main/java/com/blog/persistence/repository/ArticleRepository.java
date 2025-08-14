package com.blog.persistence.repository;

import com.blog.persistence.entity.ArticleEntity;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    List<ArticleEntity> findAll();
    Optional<ArticleEntity> findById(Long id);
    ArticleEntity save(ArticleEntity article);
    void deleteById(Long id);
}