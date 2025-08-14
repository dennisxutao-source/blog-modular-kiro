package com.blog.persistence.repository;

import com.blog.persistence.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaArticleRepository extends JpaRepository<ArticleEntity, Long> {
}