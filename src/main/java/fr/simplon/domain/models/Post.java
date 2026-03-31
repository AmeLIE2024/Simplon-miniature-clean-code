package fr.simplon.domain.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Post {

    private long id;
    private long ownerId;
    private String ownerUsername;
    private long parentId;
    private String content;
    private LocalDateTime createdAt;
    private boolean isDraft;
    private String mediaUrl;
    private AttachmentType attachmentType;
    private Set<Long> likedByUserIds = new HashSet<>();
    private List<Comment> comments = new ArrayList<>();

    public Post(long ownerId, String ownerUsername, long parentId, String content, String mediaUrl,
            AttachmentType attachmentType) {
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.parentId = parentId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.isDraft = false;
        this.mediaUrl = mediaUrl;
        this.attachmentType = attachmentType != null ? attachmentType : AttachmentType.NONE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public long getParentId() {
        return parentId;
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public Set<Long> getLikedByUserIds() {
        return likedByUserIds;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean hasMedia() {
        return attachmentType != null
                && attachmentType != AttachmentType.NONE
                && mediaUrl != null
                && !mediaUrl.isBlank();
    }
}
