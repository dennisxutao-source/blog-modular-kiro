package com.blog.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class PermissionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 200)
    private String description;
    
    @Column(nullable = false, length = 50)
    private String resource;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<RoleEntity> roles = new HashSet<>();

    // 构造函数
    public PermissionEntity() {}

    public PermissionEntity(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}