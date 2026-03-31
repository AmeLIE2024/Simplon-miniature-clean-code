package fr.simplon.domain.models;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long authorId;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;

    public Comment(Long authorId, String authorUsername, String content) {
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}