package fr.simplon.infrastructure.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;
import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.Comment;
import fr.simplon.domain.models.ImageExtension;
import fr.simplon.domain.models.VideoExtension;
import fr.simplon.domain.repository.PostRepositoryInterface;
import fr.simplon.domain.services.FileStorageService;
import fr.simplon.domain.services.PostService;
import fr.simplon.infrastructure.repository.PostRepository;

public class PostServiceImpl implements PostService {

 
    private final PostRepositoryInterface postRepository;
    private final FileStorageService fileStorageService;

    public PostServiceImpl(PostRepository postRepository,
            FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public boolean checkExtension(String extension) {
        for (ImageExtension img : ImageExtension.values()) {
            if (img.name().equals(extension))
                return true;
        }
        for (VideoExtension video : VideoExtension.values()) {
            if (video.name().equals(extension))
                return true;
        }
        return false;
    }

    @Override
    public List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts) {
        if ("subscriptions".equals(feedType) && currentUser != null) {
            List<Post> filtered = new ArrayList<>();
            for (Post post : allPosts) {
                if (currentUser.isFollowing(post.getOwnerId())) {
                    filtered.add(post);
                }
            }
            return filtered;
        }
        return allPosts;
    }

    @Override
    public void createPost(String content, String mediaUrl, User owner, AttachmentType attachmentType) {
        Post post = new Post(
                owner.getId(),
                owner.getUsername(),
                0L,
                content,
                mediaUrl,
                attachmentType);
        postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAllPosts();
    }

    @Override
    public void toggleLike(long postId, long userId) {
        Post post = postRepository.findPostById(postId);
        if (post != null) {
            Set<Long> likes = post.getLikedByUserIds();
            if (likes.contains(userId)) {
                likes.remove(userId);
            } else {
                likes.add(userId);
            }
            postRepository.update(post);
        }
    }

    @Override
    public void addComment(long postId, User author, String content) {
        Post post = postRepository.findPostById(postId);
        if (post != null) {
            Comment comment = new Comment(
                    author.getId(),
                    author.getUsername(),
                    content.trim());
            post.getComments().add(comment);
            postRepository.update(post);
        }
    }

    @Override
    public void followUser(long targetUserId, User currentUser) {
        if (currentUser.getId() == targetUserId)
            return;
        currentUser.follow(targetUserId);
    }
}