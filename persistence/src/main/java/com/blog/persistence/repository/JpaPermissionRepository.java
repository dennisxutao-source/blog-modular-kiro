package com.blog.persistence.repository;

import com.blog.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, Long> {
    
    Optional<PermissionEntity> findByName(String name);
    
    boolean existsByName(String name);
    
    List<PermissionEntity> findByResource(String resource);
    
    Optional<PermissionEntity> findByResourceAndAction(String resource, String action);
    
    @Query("SELECT p FROM PermissionEntity p WHERE p.resource = :resource")
    List<PermissionEntity> findAllByResource(@Param("resource") String resource);
}