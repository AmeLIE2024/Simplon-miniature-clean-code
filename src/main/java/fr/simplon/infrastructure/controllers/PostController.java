package fr.simplon.infrastructure.controllers;

import fr.simplon.domain.gateway.services.PostService;
import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.User;

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public void handleNewPost(String content, String mediaUrl, AttachmentType attachmentType, User owner) {
        postService.createPost(content, mediaUrl, owner, attachmentType);
    }

    public void handleLike(long postId, User owner) {
        postService.toggleLike(postId, owner.getId());
    }

    public void handleNewComment(long postId, String comment, User owner) {
        postService.addComment(postId, owner, comment);
    }

    public void handleFollowRequest(long targetUserId, User owner) {
        postService.followUser(targetUserId, owner);
    }
}