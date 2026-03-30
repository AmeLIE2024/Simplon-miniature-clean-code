package org.miniature.domain.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Post {
    private static long nbPosts = 0;
    private List<Map<String, Object>> comments = new ArrayList<>();

    private long id;
    private long owner;
    private String ownerUsername;
    private long parent;
    private String content;
    private LocalDateTime createdAt;
    private boolean isDraft = false;
    private Set<Long> likedByUserIds = new HashSet<>();

    private String mediaUrl;
    private AttachmentType attachmentType = AttachmentType.NONE;

    public Post(long id, long owner, String ownerUsername, long parent, String content) {
        this.id = ++nbPosts;
        this.owner = owner;
        this.ownerUsername = ownerUsername;
        this.parent = parent;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Post(long owner, long parent, String content, LocalDateTime createdAt, boolean isDraft) {
        this.id = ++nbPosts;
        this.owner = owner;
        this.parent = parent;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.isDraft = isDraft;
    }

    public Post(long id, long owner, String ownerUsername, long parent, String content, String mediaUrl,
            AttachmentType attachmentType) {
        this.id = ++nbPosts;
        this.owner = owner;
        this.ownerUsername = ownerUsername;
        this.parent = parent;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.mediaUrl = mediaUrl;
        this.attachmentType = attachmentType != null ? attachmentType : AttachmentType.NONE;
    }

    public static long getNbPosts() {
        return nbPosts;
    }

    public List<Map<String, Object>> getComments() {
        return comments;
    }

    public long getId() {
        return id;
    }

    public long getOwner() {
        return owner;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public long getParent() {
        return parent;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public Set<Long> getLikedByUserIds() {
        return likedByUserIds;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public int getLikeCount() {
        return likedByUserIds.size();
    }

    public boolean isLikedBy(long userId) {
        return likedByUserIds.contains(userId);
    }

    public void toggleLike(long userId) {
        if (likedByUserIds.contains(userId)) {
            likedByUserIds.remove(userId);
        } else {
            likedByUserIds.add(userId);
        }
    }

    public boolean hasMedia() {
        return attachmentType != null
                && attachmentType != AttachmentType.NONE
                && mediaUrl != null
                && !mediaUrl.isBlank();
    }

    public int compareTo(Post post) {
        return getCreatedAt().compareTo(post.getCreatedAt());
    }

    public void addComment(long userId, String username, String content) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("username", username);
        comment.put("content", content);
        comment.put("createdAt", LocalDateTime.now());
        comments.add(comment);
    }
}
