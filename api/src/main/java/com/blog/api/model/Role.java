package com.blog.api.model;

import java.time.LocalDateTime;
import java.util.Set;

public class Role {
    
    private Long id;
    private String name;
    private String description;
    private Boolean isSystem;
    private LocalDateTime createdAt;
    private Set<String> permissions;
    
    public Role() {}
    
    public Role(String name, String description, Boolean isSystem) {
        this.name = name;
        this.description = description;
        this.isSystem = isSystem;
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
    
    public Boolean getIsSystem() {
        return isSystem;
    }
    
    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Set<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isSystem=" + isSystem +
                ", createdAt=" + createdAt +
                '}';
    }
}