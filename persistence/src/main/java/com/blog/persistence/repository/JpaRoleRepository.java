package com.blog.persistence.repository;

import com.blog.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {
    
    Optional<RoleEntity> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT r FROM RoleEntity r JOIN FETCH r.permissions WHERE r.name = :name")
    Optional<RoleEntity> findByNameWithPermissions(@Param("name") String name);
    
    @Query("SELECT r FROM RoleEntity r JOIN FETCH r.permissions WHERE r.name IN :names")
    Set<RoleEntity> findByNameInWithPermissions(@Param("names") Set<String> names);
}