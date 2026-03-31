package fr.simplon.infrastructure.services.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.simplon.domain.gateway.ErrorHandlingStrategy;
import fr.simplon.domain.gateway.FileStorageService;
import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;
import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.models.ImageExtension;
import fr.simplon.domain.models.VideoExtension;
import fr.simplon.domain.repository.PostRepositoryInterface;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.infrastructure.repository.PostRepository;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.FileStorageServiceImpl;
import fr.simplon.infrastructure.strategies.errors.ErrorHandlingStrategyImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class PostServiceImpl implements PostService {

    private ImageExtension imageExtension;
    private VideoExtension videoExtension;

    private UserRepositoryInterface userRepository;
    private PostRepositoryInterface postRepository;
    private ErrorHandlingStrategy errorHandlingStrategy;
    private FileStorageService fileStorageService;

    private List<Post> postList = new ArrayList<>();

    public PostServiceImpl() {
    }

    public PostServiceImpl(PostRepositoryInterface postRepository, ErrorHandlingStrategy errorHandlingStrategy, UserRepositoryInterface userRepository, FileStorageService fileStorageService) {
        this.errorHandlingStrategy = errorHandlingStrategy;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public boolean checkExtensions(String extension) {
        for(ImageExtension img : ImageExtension.values()){
           if(img.name().equals(extension)){
               return true;
           }
        }
        for(VideoExtension video : VideoExtension.values()){
            if(video.name().equals(extension)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts) {

        if ("subscriptions".equals(feedType) && currentUser != null) {
            for (Post post : postList) {
                if (currentUser.isFollowing(post.getOwner())) {
                    postList.add(post);
                }
            }
        }
        return allPosts;
    }

    @Override
    public void createPost(Post post) {
        postList.add(post);
        Collections.sort(postList, Comparator.reverseOrder());
    }

    @Override
    public void toggleLike(long postId, User currentUser) {
        Post post = postRepository.findPostById(postId);

        if (post != null) {
            post.toggleLike(currentUser.getId());
            postRepository.update(post);
        }
    }

    @Override
    public void addComment(long postId, User author, String content) {
        Post post = postRepository.findPostById(postId);
        if (post != null) {
            post.addComment(author.getId(), author.getUsername(), content.trim());
            postRepository.update(post);
        }
    }

    @Override
    public void handleLike(HttpServletRequest req, User owner) {
        String buttonLike = req.getParameter("buttonLike");

        if (buttonLike != null && owner != null) {
            try {
                long postId = Long.parseLong(buttonLike);
                toggleLike(postId, owner);
            } catch (NumberFormatException e) {
                errorHandlingStrategy.handleError(e);
            }
        }
    }
    @Override
    public void handleNewComment(HttpServletRequest req, User owner) {
        String newComment = req.getParameter("newComment");
        String postIdStr = req.getParameter("postId");

        if (newComment != null && newComment.trim().isEmpty() && postIdStr != null && owner != null) {
            try {
                long postId = Long.parseLong(postIdStr);
                addComment(postId, owner, newComment);
            } catch (NumberFormatException e) {
                errorHandlingStrategy.handleError(e);
            }
        }
    }

    @Override
    public void handleFollowRequest(HttpServletRequest req, User owner) {
        String followUsername = req.getParameter("follow");

        if (followUsername != null && owner != null) {
            User userToFollow = userRepository.findByUserName(followUsername);

            if (userToFollow != null) {
                owner.follow(userToFollow.getId());
            }
        }
    }

    @Override
    public void handleNewPost( User owner)
            throws  IOException {

    }

}
