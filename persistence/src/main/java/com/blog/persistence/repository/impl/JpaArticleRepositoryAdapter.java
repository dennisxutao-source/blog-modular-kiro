package com.blog.persistence.repository.impl;

import com.blog.persistence.entity.ArticleEntity;
import com.blog.persistence.repository.ArticleRepository;
import com.blog.persistence.repository.JpaArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaArticleRepositoryAdapter implements ArticleRepository {
    
    private final JpaArticleRepository jpaRepository;

    @Autowired
    public JpaArticleRepositoryAdapter(JpaArticleRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ArticleEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<ArticleEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public ArticleEntity save(ArticleEntity article) {
        return jpaRepository.save(article);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}