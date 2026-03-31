package fr.simplon.domain.gateway;

import java.io.IOException;
import java.util.List;
import fr.simplon.domain.models.User;
import fr.simplon.domain.models.Post;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PostService {


    boolean checkExtensions(String extension);

    List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts);

    void createPost(Post post);

    void toggleLike(long postId, User currentUser);

    void addComment(long postId, User author, String content);

    void handleLike(HttpServletRequest req, User owner);

    void handleNewComment(HttpServletRequest req, User owner);

    void handleFollowRequest(HttpServletRequest req, User owner);

    void handleNewPost(HttpServletRequest req, HttpServletResponse resp, User owner)
            throws ServletException, IOException;
}
