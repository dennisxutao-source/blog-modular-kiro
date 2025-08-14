package com.blog.core.service.impl;

import com.blog.api.model.Article;
import com.blog.api.service.ArticleService;
import com.blog.persistence.entity.ArticleEntity;
import com.blog.persistence.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id)
                .map(this::convertToModel);
    }

    @Override
    public Article createArticle(Article article) {
        ArticleEntity entity = convertToEntity(article);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        ArticleEntity savedEntity = articleRepository.save(entity);
        return convertToModel(savedEntity);
    }

    @Override
    public Article updateArticle(Long id, Article article) {
        ArticleEntity entity = convertToEntity(article);
        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());
        ArticleEntity savedEntity = articleRepository.save(entity);
        return convertToModel(savedEntity);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    private Article convertToModel(ArticleEntity entity) {
        return new Article(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getAuthor(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ArticleEntity convertToEntity(Article model) {
        return new ArticleEntity(
                model.getId(),
                model.getTitle(),
                model.getContent(),
                model.getAuthor(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}