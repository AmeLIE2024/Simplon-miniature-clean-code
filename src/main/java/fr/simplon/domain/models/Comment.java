package fr.simplon.domain.models;

import java.time.LocalDateTime;

public class Comment {
    private long authorId;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;

    public Comment(long authorId, String authorUsername, String content) {
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public long getAuthorId() {
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
}