package com.blog.persistence.repository.impl;

import com.blog.api.model.Permission;
import com.blog.persistence.entity.PermissionEntity;
import com.blog.persistence.repository.JpaPermissionRepository;
import com.blog.persistence.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaPermissionRepositoryAdapter implements PermissionRepository {

    @Autowired
    private JpaPermissionRepository jpaPermissionRepository;

    @Override
    @Transactional
    public Permission save(Permission permission) {
        PermissionEntity entity = toEntity(permission);
        PermissionEntity savedEntity = jpaPermissionRepository.save(entity);
        return toModel(savedEntity);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return jpaPermissionRepository.findById(id)
                .map(this::toModel);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return jpaPermissionRepository.findByName(name)
                .map(this::toModel);
    }

    @Override
    public List<Permission> findAll() {
        return jpaPermissionRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Permission> findAll(Pageable pageable) {
        return jpaPermissionRepository.findAll(pageable)
                .map(this::toModel);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaPermissionRepository.deleteById(id);
    }

    @Override
    public List<Permission> findByResource(String resource) {
        return jpaPermissionRepository.findByResource(resource).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Permission> findByResourceAndAction(String resource, String action) {
        return jpaPermissionRepository.findByResourceAndAction(resource, action)
                .map(this::toModel);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaPermissionRepository.existsByName(name);
    }

    @Override
    public boolean existsByResourceAndAction(String resource, String action) {
        return jpaPermissionRepository.findByResourceAndAction(resource, action).isPresent();
    }

    @Override
    public List<String> findAllResources() {
        return jpaPermissionRepository.findAll().stream()
                .map(PermissionEntity::getResource)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findActionsByResource(String resource) {
        return jpaPermissionRepository.findByResource(resource).stream()
                .map(PermissionEntity::getAction)
                .distinct()
                .collect(Collectors.toList());
    }

    private PermissionEntity toEntity(Permission permission) {
        PermissionEntity entity = new PermissionEntity();
        entity.setId(permission.getId());
        entity.setName(permission.getName());
        entity.setDescription(permission.getDescription());
        entity.setResource(permission.getResource());
        entity.setAction(permission.getAction());
        entity.setCreatedAt(permission.getCreatedAt());
        return entity;
    }

    private Permission toModel(PermissionEntity entity) {
        Permission permission = new Permission();
        permission.setId(entity.getId());
        permission.setName(entity.getName());
        permission.setDescription(entity.getDescription());
        permission.setResource(entity.getResource());
        permission.setAction(entity.getAction());
        permission.setCreatedAt(entity.getCreatedAt());
        return permission;
    }
}